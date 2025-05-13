package com.podzilla.order.messaging;

import com.podzilla.order.model.OrderStatus;
import com.podzilla.order.service.OrderService;
import com.podzilla.order.model.StockReservationResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderConsumer {

    private final OrderService orderService;

    public OrderConsumer(final OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitMQConfig.STOCK_RESERVED_QUEUE)
    public void handleStockReserved(final StockReservationResponse message) {
        System.out.println("✅ Stock reserved for order: "
                + message.getOrderId()
                + ", product: " + message.getOrderId());

        orderService.placeOrder(UUID.fromString(message.getOrderId()));
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_FAILED_QUEUE)
    public void handleOrderFulfillmentFailed(
            final StockReservationResponse message) {
        System.out.println("❌ Order fulfillment failed for order: "
                + message.getOrderId()
                + ", reason: " + message.getReason());

        orderService.updateOrderStatus(
                UUID.fromString(message.getOrderId()), OrderStatus.FAILED);
    }
}
