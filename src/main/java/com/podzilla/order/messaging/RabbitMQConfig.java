package com.podzilla.order.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Exchange
    public static final String EXCHANGE = "shared_exchange";

    // Outbound (sending to warehouse)
    public static final String ORDER_ROUTING_KEY =
            "order.stock_reservation_requested";

    public static final String ORDER_PLACED_ROUTING_KEY =
            "order.placed";

    // Inbound (responses from warehouse)
    public static final String STOCK_RESERVED_ROUTING_KEY =
            "warehouse.stock_reserved";
    public static final String ORDER_FAILED_ROUTING_KEY =
            "warehouse.order_fulfillment_failed";

    // Queues this service will consume from (responses from warehouse)
    public static final String STOCK_RESERVED_QUEUE =
            "order.stock_reserved.queue";
    public static final String ORDER_FAILED_QUEUE =
            "order.fulfillment_failed.queue";

    @Bean
    public Queue stockReservedQueue() {
        return new Queue(STOCK_RESERVED_QUEUE);
    }

    @Bean
    public Queue orderFailedQueue() {
        return new Queue(ORDER_FAILED_QUEUE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding warehouseStockReservedBinding(
                                    final Queue stockReservedQueue,
                                    final TopicExchange exchange) {
        return BindingBuilder
                .bind(stockReservedQueue)
                .to(exchange)
                .with("warehouse.stock_reserved");
    }

    @Bean
    public Binding warehouseFulfillmentFailedBinding(
                                    final Queue orderFailedQueue,
                                    final TopicExchange exchange) {
        return BindingBuilder
                .bind(orderFailedQueue)
                .to(exchange)
                .with("warehouse.order_fulfillment_failed");
    }
}
