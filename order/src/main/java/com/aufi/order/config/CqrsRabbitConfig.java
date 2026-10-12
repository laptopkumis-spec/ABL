package com.aufi.order.config;

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
 * Konfigurasi RabbitMQ sebagai EVENT BUS untuk CQRS.
 *
 * Command side mem-publish event ke exchange "order.exchange" dengan routing key
 * "order.created" / "order.updated" / "order.deleted". Semua event itu masuk ke
 * queue "order.projection.queue" yang didengarkan Query side untuk membangun
 * read model.
 */
@Configuration
public class CqrsRabbitConfig {

    public static final String EXCHANGE = "order.exchange";
    public static final String PROJECTION_QUEUE = "order.projection.queue";
    public static final String ROUTING_PATTERN = "order.*";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue orderProjectionQueue() {
        return new Queue(PROJECTION_QUEUE, true);
    }

    @Bean
    public Binding orderProjectionBinding(Queue orderProjectionQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderProjectionQueue).to(orderExchange).with(ROUTING_PATTERN);
    }

    /** Converter JSON (dengan dukungan java.time) untuk payload event. */
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }
}
