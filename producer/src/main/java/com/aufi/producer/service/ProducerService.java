package com.aufi.producer.service;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service producer: bertugas MENGIRIM pesan ke RabbitMQ.
 */
@Service
public class ProducerService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue queue;

    public ProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMassage(String pesan) {
        rabbitTemplate.convertAndSend(queue.getName(), pesan);
        System.out.println("Pesan terkirim : " + pesan);
    }
}
