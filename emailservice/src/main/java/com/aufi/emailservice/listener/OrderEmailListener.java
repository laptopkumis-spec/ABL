package com.aufi.emailservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.aufi.emailservice.config.EmailRabbitConfig;
import com.aufi.emailservice.event.OrderEvent;
import com.aufi.emailservice.service.EmailService;

/**
 * Mendengarkan queue "order.email.queue". Setiap event order yang masuk akan
 * diproses menjadi email.
 */
@Component
public class OrderEmailListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEmailListener.class);

    private final EmailService emailService;

    public OrderEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = EmailRabbitConfig.EMAIL_QUEUE)
    public void onOrderEvent(OrderEvent event) {
        log.info(">> [EMAIL] Event diterima dari RabbitMQ: {} orderId={}", event.getEventType(), event.getOrderId());
        emailService.process(event);
    }
}
