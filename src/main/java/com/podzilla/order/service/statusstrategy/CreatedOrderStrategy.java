package com.podzilla.order.service.statusstrategy;

import com.podzilla.mq.events.OrderItem;
import com.podzilla.mq.events.OrderStockReservationRequestedEvent;
import com.podzilla.order.model.Order;
import com.podzilla.order.model.OrderProduct;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.podzilla.order.messaging.OrderProducer;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreatedOrderStrategy implements OrderStatusStrategy {

    private final OrderProducer orderProducer;

    @Autowired
    public CreatedOrderStrategy(final OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @Override
    public void handle(final Order order) {
        System.out.println("Handling CREATED status - notify user, reserve stock");

        OrderStockReservationRequestedEvent stockReservationRequest =
                OrderStockReservationRequestedEvent.builder()
                        .orderId(order.getId().toString())
                        .items(getOrderItems(order))
                        .build();

        orderProducer.sendStockReservationRequest(stockReservationRequest);
    }

    private List<OrderItem> getOrderItems(final Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderProduct> orderProducts = order.getOrderProducts();
        for (OrderProduct product : orderProducts) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId().toString());
            orderItem.setQuantity(product.getQuantity());
            orderItem.setPricePerUnit(product.getPricePerUnit());
            orderItems.add(orderItem);
        }
        return orderItems;
    }
}

