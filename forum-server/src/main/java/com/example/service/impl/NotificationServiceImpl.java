package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.dao.Notification;
import com.example.service.NotificationService;
import com.example.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

/**
* @author zhw
* @description 针对表【tb_notification】的数据库操作Service实现
* @createDate 2022-10-02 20:01:29
*/
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
    implements NotificationService{

}




