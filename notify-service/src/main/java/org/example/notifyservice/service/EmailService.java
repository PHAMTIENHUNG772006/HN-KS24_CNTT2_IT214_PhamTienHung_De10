package org.example.notifyservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${notification.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendReservationCreatedEmail(String recipient) {
        if (!StringUtils.hasText(recipient)) {
            throw new IllegalArgumentException("Email người nhận không được để trống dữ liệu");
        }
        if (!StringUtils.hasText(from)) {
            throw new IllegalStateException("Chưa cấu hình mail_form và mail_username");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient.trim());
        message.setSubject("Đặt vé thành công");
        message.setText("Xin chào,Phiếu vé đi của bạn đã được hệ thống tiếp nhận thành công .");

        mailSender.send(message);
    }
}
