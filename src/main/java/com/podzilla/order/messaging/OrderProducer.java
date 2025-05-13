package com.podzilla.order.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;

    public OrderProducer(final RabbitTemplate rabbitTemplate,
                         final RabbitMQConfig rabbitMQConfig) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendStockReservationRequest(
                        final Object stockReservationRequest) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                stockReservationRequest
        );
    }

    public void sendOrderPlaced(
            final Object orderPlaced) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ORDER_PLACED_ROUTING_KEY,
                orderPlaced
        );
    }
}
