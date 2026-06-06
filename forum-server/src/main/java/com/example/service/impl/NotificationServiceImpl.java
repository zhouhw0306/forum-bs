package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.dao.Notification;
import com.example.domain.vo.NotificationMessage;
import com.example.mapper.NotificationMapper;
import com.example.service.NotificationService;
import com.example.websocket.WebSocketComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author zhw
* @description 针对表【tb_notification】的数据库操作Service实现
* @createDate 2022-10-02 20:01:29
*/
@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
    implements NotificationService {

    @Override
    public Notification createAndPush(Notification notification) {
        notification.setCreateTime(new Date());
        if (notification.getIsRead() == null) {
            notification.setIsRead(0);
        }
        // 入库
        save(notification);
        log.info("【通知】入库成功, id={}, type={}, receiverId={}", notification.getId(), notification.getType(), notification.getReceiverId());

        // WebSocket 实时推送（仅推送给指定用户）
        if (notification.getReceiverId() != null) {
            NotificationMessage message = new NotificationMessage();
            message.setTitle(notification.getTitle());
            message.setContent(notification.getContent());
            message.setTimestamp(notification.getCreateTime());
            message.setType("info");
            message.setCategory(notification.getType());
            message.setResourceType(notification.getResourceType());
            if (notification.getResourceId() != null) {
                message.setResourceId(Integer.valueOf(notification.getResourceId()));
            }
            WebSocketComponent.sendOneObject(notification.getReceiverId(), message);
            log.info("【通知】WebSocket推送, receiverId={}, type={}", notification.getReceiverId(), notification.getType());
        }

        return notification;
    }

    @Override
    public int getUnreadCount(String userId) {
        long count = count(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getReceiverId, userId)
                .eq(Notification::getIsRead, 0));
        return (int) count;
    }

    @Override
    public IPage<Notification> getListByUser(String userId, int pageNo, int pageSize) {
        Page<Notification> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getReceiverId, userId)
                .orderByDesc(Notification::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public boolean markAsRead(Integer id, String userId) {
        return update(new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getId, id)
                .eq(Notification::getReceiverId, userId)
                .set(Notification::getIsRead, 1));
    }

    @Override
    public boolean markAllAsRead(String userId) {
        return update(new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getReceiverId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1));
    }

    @Override
    public boolean deleteNotify(Integer id, String userId) {
        return remove(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getId, id)
                .eq(Notification::getReceiverId, userId));
    }
}




