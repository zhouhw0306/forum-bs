package com.example.controller.chat;

import cn.hutool.core.util.StrUtil;
import com.example.constant.Result;
import com.example.controller.chat.client.XfXhStreamClient;
import com.example.controller.chat.config.XfXhConfig;


import com.example.controller.chat.dto.MsgDTO;
import com.example.controller.chat.listener.XfXhWebSocketListener;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;

/**
 * @author zhw
 */
@RestController
@RequestMapping(value = "/chat")
@Api(tags = "chatAi")
@Slf4j
public class ChatAiController {

    @Resource
    private XfXhStreamClient xfXhStreamClient;

    @Resource
    private XfXhConfig xfXhConfig;

    @GetMapping("/ask")
    @ApiOperation(value = "获取回答")
    public Result<String> listCategorys(@RequestParam String issue) {
        log.info("消息{}",issue);
        // 如果是无效字符串，则不对大模型进行请求
        if (StrUtil.isBlank(issue)) {
            return Result.success("无效问题，请重新输入");
        }
        // 获取连接令牌
        if (!xfXhStreamClient.operateToken(XfXhStreamClient.GET_TOKEN_STATUS)) {
            return Result.success("当前大模型连接数过多，请稍后再试");
        }

        // 创建消息对象
        MsgDTO msgDTO = MsgDTO.createUserMsg(issue);
        // 创建监听器
        XfXhWebSocketListener listener = new XfXhWebSocketListener();
        // 发送问题给大模型，生成 websocket 连接
        WebSocket webSocket = xfXhStreamClient.sendMsg(UUID.randomUUID().toString().substring(0, 10), Collections.singletonList(msgDTO), listener);
        if (webSocket == null) {
            // 归还令牌
            xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
            return Result.success("系统内部错误，请联系管理员");
        }
        try {
            int count = 0;
            // 为了避免死循环，设置循环次数来定义超时时长
            int maxCount = xfXhConfig.getMaxResponseTime() * 5;
            while (count <= maxCount) {
                Thread.sleep(200);
                log.info("第{}次获取结果{}",count,listener.isWsCloseFlag());
                if (listener.isWsCloseFlag()) {
                    break;
                }
                count++;
            }
            if (count > maxCount) {
                return Result.success("大模型响应超时，请联系管理员");
            }
            // 响应大模型的答案
            return Result.success(listener.getAnswer().toString());
        } catch (InterruptedException e) {
            log.error("错误：" + e.getMessage());
            return Result.success("系统内部错误，请联系管理员");
        } finally {
            // 关闭 websocket 连接
            webSocket.close(1000, "");
            // 归还令牌
            xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
        }
    }

}
