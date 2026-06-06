package com.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析get请求的参数
 */
@Slf4j
public class HttpUtils {

    public static Map<String, String> params2Map(String param) {

        Map<String, String> map = new HashMap<>();
        String[] arr = param.split("&");
        for (String s : arr) {
            String[] arr2 = s.split("=");
            if (arr2.length != 2) continue;
            String key = arr2[0];
            String value = arr2[1];
            map.put(key, value);
        }
        return map;
    }

    public static String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try (InputStream inputStream = request.getInputStream()) {
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.warn("getBodyString出现问题！");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error(ExceptionUtils.getMessage(e));
                }
            }
        }
        return sb.toString();
    }
}
