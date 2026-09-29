package org.example.notifyservice.consumer;

import org.example.notifyservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
public class TicketCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(TicketCreatedConsumer.class);

    private final EmailService emailService;

    public TicketCreatedConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${notification.kafka.reservation-created-topic}")
    public void consume(String email) {
        if (!StringUtils.hasText(email)) {
            log.warn("Bỏ qua sự kiện reservation có email rỗng");
            return;
        }
        log.info("Nhận sự kiện tạo reservation thành công cho email: {}", email);
        emailService.sendReservationCreatedEmail(email);
        log.info("Đã gửi email xác nhận đặt phòng tới: {}", email);
    }
}
