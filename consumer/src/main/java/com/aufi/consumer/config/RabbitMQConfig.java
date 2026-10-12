package com.aufi.consumer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurasi RabbitMQ untuk consumer.
 * Mendeklarasikan queue yang SAMA dengan producer ("pesan.queue") agar consumer
 * menerima pesan yang dikirim oleh producer-service.
 */
@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "pesan.queue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    /**
     * Converter JSON agar pesan yang dikirim producer (format JSON)
     * bisa dibaca dengan benar oleh @RabbitListener.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
