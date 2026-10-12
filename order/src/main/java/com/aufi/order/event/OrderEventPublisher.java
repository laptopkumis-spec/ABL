package com.aufi.order.event;

import com.aufi.order.config.CqrsRabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Menerbitkan event ke RabbitMQ (event bus). Hanya sisi Command yang memanggil
 * ini.
 */
@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(OrderEvent event) {
        String routingKey = "order." + event.getEventType().name().toLowerCase();
        rabbitTemplate.convertAndSend(CqrsRabbitConfig.EXCHANGE, routingKey, event);
    }
}
