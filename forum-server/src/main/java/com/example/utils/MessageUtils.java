package com.example.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Locale;

@Component
public class MessageUtils {

    private static MessageSource messageSource;

    @Resource
    public void setMessageSource(MessageSource messageSource) {
        MessageUtils.messageSource = messageSource;
    }

    /**
     * 获取国际化消息
     * @param code 消息键
     * @return 对应语言的消息
     */
    public static String get(String code) {
        return get(code, null);
    }

    /**
     * 获取国际化消息（带参数）
     * @param code 消息键
     * @param args 参数
     * @return 对应语言的消息
     */
    public static String get(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(code, args, code, locale);
        } catch (Exception e) {
            return code;
        }
    }
}
