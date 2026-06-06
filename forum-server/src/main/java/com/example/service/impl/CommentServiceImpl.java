package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.NotificationTypeEnum;
import com.example.domain.dao.Article;
import com.example.domain.dao.Comment;
import com.example.domain.dao.Notification;
import com.example.domain.dao.User;
import com.example.mapper.ArticleMapper;
import com.example.mapper.UserMapper;
import com.example.service.CommentService;
import com.example.mapper.CommentMapper;
import com.example.service.NotificationService;
import com.example.service.UserService;
import com.example.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author zhw
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private NotificationService notificationService;

    @Override
    @Transactional
    public Comment addComm(Comment comment) {
        String authorId = UserUtils.getCurrentUser();
        comment.setAuthorId(authorId);
        commentMapper.insert(comment);
        //封装author对象
        Comment comm = commentMapper.selectById(comment.getId());
        User author = userMapper.selectById(authorId);
        author.setPassword("it's a secret");
        comm.setUser(author);
        //封装toUser对象
        if ("2".equals(comm.getLevel())) {
            User toUser = userMapper.selectById(comm.getToUid());
            comm.setToUser(toUser);
        }
        articleMapper.addCommCount(comment.getArticleId());

        // 触发通知
        try {
            Article article = articleMapper.selectById(comment.getArticleId());
            if ("0".equals(comm.getLevel())) {
                // 文章下的一级评论 → 通知文章作者
                if (article != null && !authorId.equals(article.getUserId())) {
                    sendCommentNotify(authorId, article.getUserId(), comm);
                }
            } else {
                // 回复评论（level=1或2）→ 通知被回复者
                if (comm.getToUid() != null && !authorId.equals(comm.getToUid())) {
                    sendReplyNotify(authorId, comm.getToUid(), comm);
                }
            }
        } catch (Exception e) {
            log.error("发送评论通知失败", e);
        }

        return comm;
    }

    @Override
    @Transactional
    public void deleteComment(String id, String level) {
        if ("0".equals(level)) {
            remove(new QueryWrapper<Comment>().eq("parent_id", id));
            removeById(id);
        }
        if ("1".equals(level) || "2".equals(level)) {
            removeById(id);
        }
    }

    @Override
    public List<Comment> getCommentsByAid(String articleId) {
        List<Comment> list = query().eq("article_id", articleId).eq("level", "0").orderByDesc("create_time").list();
        // 封装评论的user信息
        for (Comment comm : list) {
            // 封装评论作者信息
            List<User> list0 = userService.query().select("id","avatar", "username").eq("id", comm.getAuthorId()).list();
            if (list0 != null && list0.size() > 0) {
                comm.setUser(list0.get(0));
            }
            // 封装子评论信息
            List<Comment> childrens = pushChildrenComment(comm);
            comm.setChildren(childrens);
        }

        return list;
    }

    @Override
    public List<Comment> getCommentAll() {
        List<Comment> commentList = query().eq("level", 0).orderByDesc("create_time").list();
        // 封装评论的user信息
        for (Comment comm : commentList) {
            // 封装评论作者信息
            List<User> list0 = userService.query().select("id","avatar", "username").eq("id", comm.getAuthorId()).list();
            if (list0 != null && list0.size() > 0) {
                comm.setUser(list0.get(0));
            }
            // 封装子评论信息
            List<Comment> childrens = pushChildrenComment(comm);
            comm.setChildren(childrens);
        }
        return commentList;
    }


    /**
     * 封装子评论
     */
    public List<Comment> pushChildrenComment(Comment comm) {
        // 封装子评论
        List<Comment> childrens = query().eq("parent_id", comm.getId()).orderByDesc("create_time").list();
        // 封装子评论的user信息
        for (Comment children : childrens) {
            List<User> list1 = userService.query().select("id","avatar", "username").eq("id", children.getAuthorId()).list();
            if (list1 != null && list1.size() > 0) {
                children.setUser(list1.get(0));
            }
            if (children.getLevel().equals("2")) {
                List<User> list2 = userService.query().select("id","avatar", "username").eq("id", children.getToUid()).list();
                if (list2 != null && list2.size() > 0) {
                    children.setToUser(list2.get(0));
                }
            }
        }
        return childrens;
    }


    @Override
    public Integer likeComment(String commentId, String type) {
        String currentUser = UserUtils.getCurrentUser();
        Boolean isMember = stringRedisTemplate.opsForSet().isMember("LIKE_COMMON:" + currentUser, commentId);
        if (Boolean.TRUE.equals(isMember)) {
            stringRedisTemplate.opsForSet().remove("LIKE_COMMON:" + currentUser, commentId);
            commentMapper.disLikeComment(commentId);
            return -1;
        } else {
            stringRedisTemplate.opsForSet().add("LIKE_COMMON:" + currentUser, commentId);
            int result = commentMapper.likeComment(commentId);
            // 触发点赞通知
            try {
                Comment comment = commentMapper.selectById(commentId);
                if (comment != null && !currentUser.equals(comment.getAuthorId())) {
                    sendLikeNotify(currentUser, comment);
                }
            } catch (Exception e) {
                log.error("发送点赞通知失败", e);
            }
            return result;
        }
    }

    @Override
    public List<String> allLikeCommonId() {
        Set<String> members = stringRedisTemplate.opsForSet().members("LIKE_COMMON:" + UserUtils.getCurrentUser());
        if (members == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(members);
    }

    // ======================== 通知触发 ========================

    /**
     * 发送评论通知（给文章作者）
     */
    private void sendCommentNotify(String senderId, String receiverId, Comment comment) {
        User sender = userMapper.selectById(senderId);
        Notification notify = new Notification();
        notify.setType(NotificationTypeEnum.COMMENT.getCode());
        notify.setTitle("新评论");
        notify.setContent((sender != null ? sender.getUsername() : "有人") + " 评论了你的文章");
        notify.setSenderId(senderId);
        notify.setReceiverId(receiverId);
        notify.setResourceType("ARTICLE");
        notify.setResourceId(comment.getArticleId());
        notificationService.createAndPush(notify);
    }

    /**
     * 发送回复通知（给被回复者）
     */
    private void sendReplyNotify(String senderId, String receiverId, Comment comment) {
        User sender = userMapper.selectById(senderId);
        Notification notify = new Notification();
        notify.setType(NotificationTypeEnum.REPLY.getCode());
        notify.setTitle("新回复");
        notify.setContent((sender != null ? sender.getUsername() : "有人") + " 回复了你的评论");
        notify.setSenderId(senderId);
        notify.setReceiverId(receiverId);
        notify.setResourceType("ARTICLE");
        notify.setResourceId(comment.getArticleId());
        notificationService.createAndPush(notify);
    }

    /**
     * 发送点赞通知（给评论作者）
     */
    private void sendLikeNotify(String senderId, Comment comment) {
        User sender = userMapper.selectById(senderId);
        Notification notify = new Notification();
        notify.setType(NotificationTypeEnum.LIKE.getCode());
        notify.setTitle("新点赞");
        notify.setContent((sender != null ? sender.getUsername() : "有人") + " 赞了你的评论");
        notify.setSenderId(senderId);
        notify.setReceiverId(comment.getAuthorId());
        notify.setResourceType("ARTICLE");
        notify.setResourceId(comment.getArticleId());
        notificationService.createAndPush(notify);
    }
}




