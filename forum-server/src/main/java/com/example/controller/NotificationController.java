package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.annotation.Authentication;
import com.example.constant.AuthConstant;
import com.example.constant.Result;
import com.example.domain.dao.Notification;
import com.example.service.NotificationService;
import com.example.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhw
 */
@RestController
@RequestMapping("/notify")
@Api(tags = "通知接口")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    /**
     * 获取全部系统公告（receiver_id IS NULL）
     */
    @GetMapping("/getAll")
    @ApiOperation(value = "获取全部系统公告")
    public Result<List<Notification>> getAll() {
        List<Notification> list = notificationService.list(
                new LambdaQueryWrapper<Notification>().isNull(Notification::getReceiverId)
                        .orderByDesc(Notification::getCreateTime));
        return Result.success(list);
    }

    /**
     * 获取当前用户未读通知数量
     */
    @GetMapping("/unread-count")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "获取未读通知数量")
    public Result<Integer> getUnreadCount() {
        String userId = UserUtils.getCurrentUser();
        int count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 分页获取当前用户的通知列表
     */
    @GetMapping("/list")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "获取用户通知列表")
    public Result<IPage<Notification>> getList(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserUtils.getCurrentUser();
        IPage<Notification> page = notificationService.getListByUser(userId, pageNo, pageSize);
        return Result.success(page);
    }

    /**
     * 标记单条通知为已读
     */
    @PutMapping("/read/{id}")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "标记通知已读")
    public Result<Void> markAsRead(@PathVariable Integer id) {
        String userId = UserUtils.getCurrentUser();
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "全部标记已读")
    public Result<Void> markAllAsRead() {
        String userId = UserUtils.getCurrentUser();
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    /**
     * 删除单条通知
     */
    @DeleteMapping("/{id}")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "删除通知")
    public Result<Void> deleteNotify(@PathVariable Integer id) {
        String userId = UserUtils.getCurrentUser();
        notificationService.deleteNotify(id, userId);
        return Result.success();
    }
}
