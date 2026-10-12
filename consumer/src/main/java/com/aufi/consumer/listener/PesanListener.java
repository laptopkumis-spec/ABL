package com.aufi.consumer.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.aufi.consumer.config.RabbitMQConfig;
import com.aufi.consumer.service.ConsumerService;

/**
 * Consumer: mendengarkan queue "pesan.queue".
 * Setiap pesan yang dikirim producer akan diterima di sini, dicatat di log,
 * dan disimpan agar bisa dilihat lewat endpoint REST.
 */
@Component
public class PesanListener {

    private static final Logger log = LoggerFactory.getLogger(PesanListener.class);

    private final ConsumerService consumerService;

    public PesanListener(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void terima(String pesan) {
        log.info(">> [CONSUMER] Pesan diterima dari RabbitMQ [{}]: {}", RabbitMQConfig.QUEUE, pesan);
        consumerService.simpan(pesan);
    }
}
