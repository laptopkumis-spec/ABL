package com.aufi.emailservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurasi RabbitMQ untuk emailservice.
 *
 * Mendengarkan exchange yang SAMA dengan order-service ("order.exchange"), tetapi
 * memakai QUEUE SENDIRI ("order.email.queue") agar tidak berebut pesan dengan
 * queue projector CQRS (competing consumers).
 */
@Configuration
public class EmailRabbitConfig {

    public static final String EXCHANGE = "order.exchange";
    public static final String EMAIL_QUEUE = "order.email.queue";
    /** Dengarkan ketiga jenis event order. */
    public static final String ROUTING_PATTERN = "order.*";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue orderEmailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Binding orderEmailBinding(Queue orderEmailQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderEmailQueue).to(orderExchange).with(ROUTING_PATTERN);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }
}
