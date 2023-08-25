package com.example.aop;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.example.annotation.RepeatSubmit;
import com.example.exception.RateLimitException;
import com.example.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 防止表单重复提交
 * @author zhou
 */
@Slf4j
@Aspect
@Component
public class RepeatSubmitAspect {

    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";

    public final String TOKEN = "token";

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Before("@annotation(repeatSubmit)")
    public void before(JoinPoint jp, RepeatSubmit repeatSubmit){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String nowParams = getParams(request);
        if (StringUtils.isEmpty(nowParams)) {
            return;
        }
        Map<Object, Object> nowDataMap = new HashMap<>(2);
        nowDataMap.put(REPEAT_PARAMS, MD5.create().digestHex(nowParams));
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

        // 构建提交表单的唯一标识（指定key + url + 个人标识）
        String url = request.getRequestURI();
        String token = StringUtils.trimToEmpty(request.getHeader(TOKEN));
        String repeatKey = REPEAT_SUBMIT_KEY + url + ":" + token;

        synchronized (repeatKey.intern()){
            Map<Object, Object> preDataMap = stringRedisTemplate.opsForHash().entries(repeatKey);
            if (MapUtil.isNotEmpty(preDataMap) && compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap, repeatSubmit.interval())) {
                throw new RateLimitException(repeatSubmit.message());
            }
            stringRedisTemplate.opsForHash().putAll(repeatKey, nowDataMap);
            stringRedisTemplate.expire(repeatKey,repeatSubmit.interval(), TimeUnit.MILLISECONDS);
        }

    }

    /**
     * 判断参数是否相同
     */
    private boolean compareParams(Map<Object, Object> nowMap, Map<Object, Object> preMap) {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 判断两次间隔时间
     */
    private boolean compareTime(Map<Object, Object> nowMap, Map<Object, Object> preMap, int interval) {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        return (time1 - time2) < interval;
    }

    private String getParams(HttpServletRequest request){
        String nowParams = null;
        try {
            BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                sb.append(inputStr);
            }
            if (StringUtils.isEmpty(nowParams = sb.toString())){
                nowParams = JSONUtil.toJsonStr(request.getParameterMap());
            }
        } catch (IOException e) {
            throw new SystemException("读取请求参数异常");
        }
        return nowParams;
    }
}
