package com.example.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.internet.MimeMessage;

/**
 * 发送邮件的工具类
 * @author zhw
 */
@Component
public class EmailUtils {

    private static String FROM_EMAIL;

    @Value("${spring.mail.username}")
    public void setFromEmail(String fromEmail) {
        FROM_EMAIL = fromEmail;
    }

    public static String sendSimpleEmail(String code, String mail, JavaMailSender jms) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(mail); // 接收地址
            message.setSubject("codebase"); // 标题
            message.setText("【codebase】欢迎加入社区,验证码是: "+code+" ,如与您无关,请忽略"); // 内容
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
            helper.setFrom(FROM_EMAIL);
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
