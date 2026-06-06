package com.example.controller.chat.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.controller.chat.config.DeepSeekConfig;
import com.example.controller.chat.dto.MsgDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
@Slf4j
public class DeepSeekStreamClient {

    @Resource
    private DeepSeekConfig config;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(200, TimeUnit.SECONDS)
            .build();

    /**
     * 流式发送消息，每收到一个 chunk 回调 onChunk，完成时回调 onComplete
     */
    public void sendMessageStream(List<MsgDTO> msgList,
                                   Consumer<String> onChunk,
                                   Runnable onComplete,
                                   Consumer<Throwable> onError) {
        try {
            JSONObject body = new JSONObject();
            body.put("model", config.getModel());
            body.put("messages", buildMessages(msgList));
            body.put("stream", true);
            body.put("max_tokens", config.getMaxTokens());
            body.put("temperature", config.getTemperature());

            Request request = new Request.Builder()
                    .url(config.getBaseUrl() + "/v1/chat/completions")
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(body.toJSONString(),
                            MediaType.parse("application/json")))
                    .build();

            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                String err = response.body() != null ? response.body().string() : "unknown";
                log.error("DeepSeek API error: {} - {}", response.code(), err);
                onChunk.accept("AI 服务响应异常");
                onComplete.run();
                return;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));

            int maxLoops = config.getMaxResponseTime() * 10;
            int count = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (count++ > maxLoops) {
                    onChunk.accept("\n\n*响应超时*");
                    break;
                }
                if (line.isEmpty() || "data: [DONE]".equals(line)) {
                    continue;
                }
                if (line.startsWith("data: ")) {
                    try {
                        JSONObject data = JSONObject.parseObject(line.substring(6));
                        JSONArray choices = data.getJSONArray("choices");
                        if (choices != null && !choices.isEmpty()) {
                            JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
                            if (delta != null && delta.containsKey("content")) {
                                String chunk = delta.getString("content");
                                if (chunk != null) {
                                    onChunk.accept(chunk);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.debug("SSE parse skip: {}", line);
                    }
                }
            }
            reader.close();
            response.close();
            onComplete.run();
        } catch (Exception e) {
            log.error("DeepSeek stream error: {}", e.getMessage());
            onError.accept(e);
        }
    }

    private JSONArray buildMessages(List<MsgDTO> msgList) {
        JSONArray arr = new JSONArray();
        for (MsgDTO msg : msgList) {
            JSONObject item = new JSONObject();
            item.put("role", msg.getRole());
            item.put("content", msg.getContent());
            arr.add(item);
        }
        return arr;
    }
}
