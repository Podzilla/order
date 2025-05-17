package com.podzilla.order.messaging;

import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.OrderCancelledEvent;
import com.podzilla.mq.events.OrderPlacedEvent;
import com.podzilla.mq.events.OrderStockReservationRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProducer {


    private final EventPublisher eventPublisher;


    public void sendStockReservationRequest(
            final OrderStockReservationRequestedEvent orderStockReservationRequestedEvent) {

        eventPublisher.publishEvent(
                EventsConstants.ORDER_STOCK_RESERVATION_REQUESTED,
                orderStockReservationRequestedEvent
        );
    }

    public void sendOrderPlaced(
            final OrderPlacedEvent orderPlaced) {
        eventPublisher.publishEvent(
                EventsConstants.ORDER_PLACED,
                orderPlaced
        );
    }

    public void sendCancelOrder(
            final OrderCancelledEvent orderCancelledEvent) {
        eventPublisher.publishEvent(
                EventsConstants.ORDER_CANCELLED,
                orderCancelledEvent
        );
    }
}
