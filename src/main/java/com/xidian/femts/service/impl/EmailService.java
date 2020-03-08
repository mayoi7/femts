package com.xidian.femts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author LiuHaonan
 * @date 14:01 2020/2/1
 * @email acerola.orion@foxmail.com
 */
@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * 发送者的邮箱
     */
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 主机地址
     */
    @Value("${femts.host.addr}")
    private String host;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送一封邮件
     *
     * @param to      接收方邮箱
     * @param subject 主题
     * @param content 内容
     */
    private void sendMail(String to, String subject, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);

        mailSender.send(msg);
    }

    /**
     * 发送一封html格式的邮件
     *
     * @param to      接收方邮箱
     * @param subject 主题
     * @param content 内容
     */
    private void sendHtmlMail(String to, String subject, String content) {
        MimeMessage msg = mailSender.createMimeMessage();
        try {
            Multipart multipart = new MimeMultipart();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            // 第二个参数为true表示该邮件是HTML格式
            helper.setText(content, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("[邮件]html邮件发送异常");
        }

    }

    /**
     * 发送激活邮件（与密码重置不同，激活邮件没有时间限制）
     * @param to 接收方邮箱
     * @param url 激活链接（不需要主机ip和端口号）
     */
    public void sendActiveMail(String to, String url) {
        String text = "<div>您好<br/>点击下方链接即可激活<br/>";
        String title = "【金融企业文稿追溯系统】帐号激活链接";

        String href = "<a href=\"https://" + host + "/" + url + "\">点击此链接进行激活</a></div>";

        String content = text + href;
        sendHtmlMail(to, title, content);
    }

    /**
     * 发送一封重置密码邮件（有时间限制）
     *
     * @param to  接收方邮箱
     * @param url 重置密码的链接（不需要主机ip和端口号）
     */
    public void sendResetPasswordMail(String to, String url) {
        String text = "<div>您好<br/>点击下方链接即可重置密码，有效时间为30分钟<br/>";
        String title = "【金融企业文稿追溯系统】密码重置链接";

        String href = "<a href=\"https://" + host + "/" + url + "\">点击此链接进行密码重置</a></div>";

        String content = text + href;
        sendHtmlMail(to, title, content);
    }
}
