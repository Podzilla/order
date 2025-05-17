package com.podzilla.order.service.statusstrategy;

import com.podzilla.order.model.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderStatusStrategyFactory {

    private final Map<OrderStatus, OrderStatusStrategy> strategyMap;

    @Autowired
    public OrderStatusStrategyFactory(final List<OrderStatusStrategy> strategies) {
        strategyMap = new HashMap<>();
        for (OrderStatusStrategy strategy : strategies) {
            if (strategy instanceof CreatedOrderStrategy) {
                strategyMap.put(OrderStatus.PENDING, strategy);
            }
        }
    }

    public OrderStatusStrategy getStrategy(final OrderStatus status) {
        return strategyMap.get(status);
    }
}

