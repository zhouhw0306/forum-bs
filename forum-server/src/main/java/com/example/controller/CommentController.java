package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.annotation.Authentication;
import com.example.constant.Result;
import com.example.domain.Comment;
import com.example.domain.User;
import com.example.service.CommentService;
import com.example.service.UserService;
import com.example.utils.SensitiveFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

/**
 * 评论接口
 * @author zhou
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    CommentService commentService;

    @Resource
    UserService userService;

    @Resource
    SensitiveFilter sensitiveFilter;

    /**
     *  加载文章评论
     */
    @GetMapping("/getCommentsByArticle")
    public Result getCommentsByArticle(String articleId){

        List<Comment> list = commentService.query().eq("article_id",articleId).eq("level","0").orderByDesc("create_time").list();
        // 封装评论的user信息
        for (Comment comm : list) {
            // 封装评论作者信息
            List<User> list0 = userService.query().select("avatar", "username").eq("id", comm.getAuthorId()).list();
            if (list0 != null){
                comm.setUser(list0.get(0));
            }
            // 封装子评论信息
            List<Comment> childrens = pushChildrenComment(comm);
            comm.setChildrens(childrens);
        }

        return Result.success(list);
    }

    /**
     *  封装子评论
     */
    public List<Comment> pushChildrenComment(Comment comm){
        // 封装子评论
        List<Comment> childrens = commentService.query().eq("parent_id",comm.getId()).orderByDesc("create_time").list();
        // 封装子评论的user信息
        for (Comment children : childrens) {
            List<User> list1 = userService.query().select("avatar", "username").eq("id", children.getAuthorId()).list();
            if (list1 != null){
                children.setUser(list1.get(0));
            }
            if (children.getLevel().equals("2")){
                List<User> list2 = userService.query().select("avatar", "username").eq("id", children.getToUid()).list();
                if (list2 != null){
                    children.setToUser(list2.get(0));
                }
            }
        }
        return childrens;
    }

    /**
     * 添加评论
     */
    @PostMapping("/pushComment")
    public Result pushComment(Comment comment){
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        return commentService.addComm(comment);
    }

    /**
     * 获取所有评论
     */
    @Authentication
    @PostMapping("/getCommentAll")
    public Result getCommentAll(){
        List<Comment> list = commentService.query().eq("level", 0).orderByDesc("create_time").list();
        // 封装评论的user信息
        for (Comment comm : list) {
            // 封装评论作者信息
            List<User> list0 = userService.query().select("avatar", "username").eq("id", comm.getAuthorId()).list();
            if (list0 != null){
                comm.setUser(list0.get(0));
            }
            // 封装子评论信息
            List<Comment> childrens = pushChildrenComment(comm);
            comm.setChildrens(childrens);
        }
        return Result.success(list);
    }
}
