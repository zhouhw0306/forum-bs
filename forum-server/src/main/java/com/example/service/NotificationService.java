package com.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.dao.Notification;

/**
* @author zhw
* @description 针对表【tb_notification】的数据库操作Service
* @createDate 2022-10-02 20:01:29
*/
public interface NotificationService extends IService<Notification> {

    /**
     * 创建通知并实时推送（入库 + WebSocket双写）
     */
    Notification createAndPush(Notification notification);

    /**
     * 获取用户未读通知数量
     */
    int getUnreadCount(String userId);

    /**
     * 分页获取用户通知列表（按时间倒序）
     */
    IPage<Notification> getListByUser(String userId, int pageNo, int pageSize);

    /**
     * 标记单条通知为已读
     */
    boolean markAsRead(Integer id, String userId);

    /**
     * 标记用户所有通知为已读
     */
    boolean markAllAsRead(String userId);

    /**
     * 删除单条通知
     */
    boolean deleteNotify(Integer id, String userId);
}
