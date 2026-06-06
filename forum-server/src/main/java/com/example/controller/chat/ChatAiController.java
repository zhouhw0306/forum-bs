package com.example.controller.chat;

import cn.hutool.core.util.StrUtil;
import com.example.controller.chat.client.DeepSeekStreamClient;
import com.example.controller.chat.dto.MsgDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.Collections;

@RestController
@RequestMapping(value = "/chat")
@Api(tags = "chatAi")
@Slf4j
public class ChatAiController {

    @Resource
    private DeepSeekStreamClient deepSeekClient;

    @GetMapping("/ask")
    @ApiOperation(value = "获取DeepSeek流式回答（SSE）")
    public SseEmitter ask(@RequestParam String issue) {
        SseEmitter emitter = new SseEmitter(200_000L);

        if (StrUtil.isBlank(issue)) {
            sendSafe(emitter, "请输入有效问题");
            emitter.complete();
            return emitter;
        }

        log.info("用户提问: {}", issue);
        MsgDTO msg = MsgDTO.createUserMsg(issue);

        new Thread(() -> {
            try {
                deepSeekClient.sendMessageStream(
                        Collections.singletonList(msg),
                        chunk -> sendSafe(emitter, chunk),
                        () -> safeComplete(emitter),
                        error -> {
                            log.error("DeepSeek stream error: {}", error.getMessage());
                            sendSafe(emitter, "\n\n*服务暂时不可用*");
                            safeComplete(emitter);
                        }
                );
            } catch (Exception e) {
                log.error("chat error: {}", e.getMessage(), e);
                safeComplete(emitter);
            }
        }).start();

        return emitter;
    }

    private void sendSafe(SseEmitter emitter, String data) {
        try {
            emitter.send(SseEmitter.event().data(data));
        } catch (Exception ignored) {
        }
    }

    private void safeComplete(SseEmitter emitter) {
        try {
            emitter.complete();
        } catch (Exception ignored) {
        }
    }
}
