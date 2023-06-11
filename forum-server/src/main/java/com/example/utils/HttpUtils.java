package com.example.utils;

import java.util.HashMap;
import java.util.Map;

public class HttpUtils {


    public static Map<String, String> params2Map(String param) {

        Map<String, String> map = new HashMap<>();
        String[] arr = param.split("&");
        for (String s : arr) {
            String[] arr2 = s.split("=");
            if (arr2.length!=2) continue;
            String key = arr2[0];
            String value = arr2[1];
            map.put(key, value);
        }
        return map;
    }
}
