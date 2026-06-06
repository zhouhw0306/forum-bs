package com.example.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.Result;
import com.example.constant.SourceEnum;
import com.example.domain.dao.Source;
import com.example.domain.dao.SourceHasfavour;
import com.example.domain.dao.SourceHasthumb;
import com.example.domain.dao.User;
import com.example.domain.vo.NotificationMessage;
import com.example.mapper.UserMapper;
import com.example.service.SourceHasfavourService;
import com.example.service.SourceHasthumbService;
import com.example.service.SourceService;
import com.example.mapper.SourceMapper;
import com.example.utils.UserUtils;
import com.example.websocket.WebSocketComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
* @author zhw
* @description 针对表【tb_source】的数据库操作Service实现
* @createDate 2022-12-31 15:37:16
*/
@Slf4j
@Service
public class SourceServiceImpl extends ServiceImpl<SourceMapper, Source>
    implements SourceService{

    @Resource
    UserMapper userMapper;

    @Resource
    private SourceHasfavourService sourceHasfavourService;

    @Resource
    private SourceHasthumbService sourceHasthumbService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> pass(Integer id, Integer type) {
        if (type == null) {
            return Result.error();
        }
        Source source = getById(id);
        // 拒绝
        if (type == -1) {
            this.lambdaUpdate().set(Source::getState, -1).eq(Source::getId, id).update();
        }
        // 通过
        if (type == 1) {
            this.lambdaUpdate().set(Source::getState, 1).eq(Source::getId, id).update();
            // 添加积分
            userMapper.addScore(source.getUserId(), 2);
        }
        notifyUserOfApprovalResult(source, type == 1);
        return Result.success(type);
    }

    @Override
    public IPage<Source> pageVo(String type, String sort, Integer pageNo, Integer pageSize) {
        QueryWrapper<Source> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", SourceEnum.findEnumByName(type));
        queryWrapper.eq("state", 1);
        queryWrapper.orderByDesc(sort);
        IPage<Source> page = new Page<>(pageNo,pageSize);
        IPage<Source> iPage = page(page, queryWrapper);
        List<Source> records = iPage.getRecords();

        for (Source record : records) {
            User byId = userMapper.selectById(record.getUserId());
            record.setUser(byId);
            // 封装点赞 收藏
            String currentUser = UserUtils.getCurrentUser();
            if (currentUser != null) {
                hasFavourOrThumb(record,currentUser);
            }
        }
        return iPage;
    }

    @Override
    public Result<Source> queryById(Integer id) {
        Source record = getById(id);
        record.setUser(userMapper.selectById(record.getUserId()));
        // 封装点赞 收藏
        String currentUser = UserUtils.getCurrentUser();
        if (currentUser != null) {
            hasFavourOrThumb(record, currentUser);
        }
        return Result.success(record);

    }

    /**
     * 判断用户是否点赞或收藏该资源
     */
    public void hasFavourOrThumb(Source source,String currentUser) {
        SourceHasthumb thumb = sourceHasthumbService.lambdaQuery()
                .eq(SourceHasthumb::getUserId,currentUser)
                .eq(SourceHasthumb::getSourceId,source.getId())
                .one();
        SourceHasfavour favour = sourceHasfavourService.lambdaQuery()
                .eq(SourceHasfavour::getUserId,currentUser)
                .eq(SourceHasfavour::getSourceId,source.getId())
                .one();
        source.setHasThumb(thumb != null);
        source.setHasFavour(favour != null);
    }

    @Override
    public void notifyUserOfApprovalResult(Source source, boolean approved) {
        // 构建通知消息
        String messageTitle = approved ? "资源审核通过通知" : "资源审核未通过通知";
        String messageContent = approved ?
                "您分享的资源《" + source.getTitle() + "》已通过审核！" :
                "您分享的资源《" + source.getTitle() + "》未能通过审核，原因可能是内容不符合规范。";

        // 创建通知对象
        NotificationMessage notification = new NotificationMessage(
                messageTitle,
                messageContent,
                new Date(),
                approved ? "SUCCESS" : "WARNING",
                source.getId()
        );

        // 发送WebSocket通知给资源作者
        WebSocketComponent.sendOne(source.getUserId(), JSONUtil.toJsonStr(Result.success(notification)));

        log.info("【WebSocket消息】已向用户 {} 发送资源审核结果通知，资源ID：{}，结果：{}",
                source.getUserId(), source.getId(), approved ? "通过" : "未通过");
    }
}




