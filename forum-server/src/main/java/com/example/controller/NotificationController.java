package com.example.controller;

import com.example.constant.Result;
import com.example.domain.dao.Notification;
import com.example.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "公告接口")
public class NotificationController {

    @Resource
    NotificationService notificationService;

    @GetMapping("/getAll")
    @ApiOperation(value = "获取全部公告")
    public Result<List<Notification>> getAll() {
        List<Notification> list = notificationService.list();
        return Result.success(list);
    }

}
