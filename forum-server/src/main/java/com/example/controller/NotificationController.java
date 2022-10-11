package com.example.controller;

import com.example.constant.Result;
import com.example.domain.Notification;
import com.example.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhw
 */
@RestController
@RequestMapping("/notify")
public class NotificationController {

    @Resource
    NotificationService notificationService;

    @GetMapping("/getAll")
    public Result getAll(){
        List<Notification> list = notificationService.list();
        return Result.success(list);
    }

}
