package com.podzilla.order.service.statusstrategy;

import com.podzilla.order.model.Order;

public interface OrderStatusStrategy {
    void handle(Order order);
}
