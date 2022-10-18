package com.example.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;


public class EmailUtils {


    private static String from = "s246686@163.com";


    public static String sendSimpleEmail(String code,String mail,JavaMailSender jms) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(mail); // 接收地址
            message.setSubject("鸭鸭校园论坛"); // 标题
            message.setText("【鸭鸭】欢迎注册鸭鸭校园论坛,验证码是: "+code+" ,如与您无关,请忽略"); // 内容
            jms.send(message);
            return "发送成功";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String sendHtmlEmail(JavaMailSender jms,String mail) {
        MimeMessage message = null;
        try {
            message = jms.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(mail); // 接收地址
            helper.setSubject("一封HTML格式的邮件"); // 标题
            // 带HTML格式的内容
            StringBuffer sb = new StringBuffer("<p style='color:#42b983'>使用Spring Boot发送HTML格式邮件。</p>");
            helper.setText(sb.toString(), true);
            jms.send(message);
            return "发送成功";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


}
