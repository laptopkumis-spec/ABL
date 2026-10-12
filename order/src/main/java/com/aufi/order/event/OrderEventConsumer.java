package com.aufi.order.event;

import com.aufi.order.config.CqrsRabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer event: mendengarkan queue projection dan menyerahkan event ke
 * projector untuk memperbarui read model.
 */
@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final OrderViewProjector projector;

    public OrderEventConsumer(OrderViewProjector projector) {
        this.projector = projector;
    }

    @RabbitListener(queues = CqrsRabbitConfig.PROJECTION_QUEUE)
    public void onEvent(OrderEvent event) {
        log.info(">> [CQRS] Event diterima dari RabbitMQ: {} orderId={}", event.getEventType(), event.getOrderId());
        projector.apply(event);
    }
}
