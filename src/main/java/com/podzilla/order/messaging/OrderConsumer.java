package com.podzilla.order.messaging;

import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.BaseEvent;
import com.podzilla.mq.events.CartCheckedoutEvent;
import com.podzilla.mq.events.OrderAssignedToCourierEvent;
import com.podzilla.mq.events.OrderDeliveredEvent;
import com.podzilla.mq.events.OrderOutForDeliveryEvent;
import com.podzilla.mq.events.OrderPackagedEvent;
import com.podzilla.mq.events.WarehouseOrderFulfillmentFailedEvent;
import com.podzilla.mq.events.WarehouseStockReservedEvent;
import com.podzilla.order.model.Address;
import com.podzilla.order.model.Order;
import com.podzilla.order.model.OrderProduct;
import com.podzilla.order.model.OrderStatus;

import com.podzilla.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class OrderConsumer {

    private final OrderService orderService;

    public OrderConsumer(final OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = EventsConstants.ORDER_INVENTORY_EVENT_QUEUE)
    public void handleStockReserved(final BaseEvent payload) {
        if (payload instanceof WarehouseStockReservedEvent
                warehouseStockReservedEvent) {
            handleWarehouseStockReservedEvent(warehouseStockReservedEvent);
        }
        if (payload instanceof WarehouseOrderFulfillmentFailedEvent
                warehouseOrderFulfillmentFailedEvent) {

            handleWarehouseOrderFulfillmentFailedEvent(
                    warehouseOrderFulfillmentFailedEvent);
        }
    }

    @RabbitListener(queues = EventsConstants.ORDER_ORDER_EVENT_QUEUE)
    public void trackOrder(final BaseEvent payload) {
        if (payload instanceof OrderPackagedEvent orderPackagedEvent) {
            handleOrderPackagedEvent(orderPackagedEvent);
        }
        if (payload instanceof OrderAssignedToCourierEvent
                orderAssignedToCourierEvent) {
            handleOrderAssignedToCourierEvent(orderAssignedToCourierEvent);
        }
        if (payload instanceof OrderOutForDeliveryEvent
                orderOutForDeliveryEvent) {
            handleOrderOutForDeliveryEvent(orderOutForDeliveryEvent);
        }
        if (payload instanceof OrderDeliveredEvent orderDeliveredEvent) {
            handleOrderDeliveredEvent(orderDeliveredEvent);
        }
        if (payload instanceof CartCheckedoutEvent cartCheckedoutEvent) {
            handleCartCheckoutEvent(cartCheckedoutEvent);
        }
    }

    private void handleWarehouseStockReservedEvent(
            final WarehouseStockReservedEvent warehouseStockReservedEvent) {

        log.info("✅ Stock reserved for order: {}",
                warehouseStockReservedEvent.getOrderId());
        orderService.placeOrder(UUID.fromString(warehouseStockReservedEvent.getOrderId()));
    }

    private void handleWarehouseOrderFulfillmentFailedEvent(
            final WarehouseOrderFulfillmentFailedEvent
                    warehouseOrderFulfillmentFailedEvent) {
        log.info("❌ Order fulfillment failed for order: {}, reason: {}",
                warehouseOrderFulfillmentFailedEvent.getOrderId(),
                warehouseOrderFulfillmentFailedEvent.getReason());
        orderService.cancelOrder(
                UUID.fromString(warehouseOrderFulfillmentFailedEvent.getOrderId()),
                warehouseOrderFulfillmentFailedEvent.getReason());
    }

    private void handleCartCheckoutEvent(
            final CartCheckedoutEvent cartCheckedoutEvent) {
        log.info("✅ Cart checked out for user with id: {}",
                cartCheckedoutEvent.getCustomerId());
        Address address = new Address();
        address.setStreet(cartCheckedoutEvent.getDeliveryAddress().getStreet());
        address.setCity(cartCheckedoutEvent.getDeliveryAddress().getCity());
        address.setState(cartCheckedoutEvent.getDeliveryAddress().getState());
        address.setCountry(cartCheckedoutEvent.getDeliveryAddress().getCountry());
        address.setPostalCode(cartCheckedoutEvent.getDeliveryAddress().getPostalCode());
        List<OrderProduct> orderProducts = cartCheckedoutEvent.getItems().stream()
                .map(orderProduct -> {
                    OrderProduct product = new OrderProduct();
                    product.setProductId(UUID.fromString(orderProduct.getProductId()));
                    product.setQuantity(orderProduct.getQuantity());
                    product.setPricePerUnit(orderProduct.getPricePerUnit());
                    return product;
                }).toList();
        Order order = new Order();
        order.setUserId(UUID.fromString(cartCheckedoutEvent.getCustomerId()));
        order.setTotalAmount(cartCheckedoutEvent.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(address);
        order.setOrderProducts(orderProducts);
        order.setConfirmationType(cartCheckedoutEvent.getConfirmationType());
        order.setSignature(cartCheckedoutEvent.getSignature());
        orderService.createOrder(order);
    }

    private void handleOrderAssignedToCourierEvent(final OrderAssignedToCourierEvent orderAssignedToCourierEvent) {
        log.info("✅ Order assigned to courier for order: {}",
                orderAssignedToCourierEvent.getOrderId());
        orderService.updateOrder(
                UUID.fromString(orderAssignedToCourierEvent.getOrderId()),
                Order.builder()
                        .courierId(UUID.fromString(orderAssignedToCourierEvent.getCourierId()))
                        .status(OrderStatus.ORDER_ASSIGNED_TO_COURIER)
                        .build());
    }

    private void handleOrderDeliveredEvent(
            final OrderDeliveredEvent orderDeliveredEvent) {
        log.info("✅ Order delivered for order: {}",
                orderDeliveredEvent.getOrderId());
        orderService.updateOrderStatus(UUID.fromString(orderDeliveredEvent.getOrderId()), OrderStatus.DELIVERED);
    }

    private void handleOrderOutForDeliveryEvent(final OrderOutForDeliveryEvent orderOutForDeliveryEvent) {
        log.info("✅ Order out for delivery for order: {}", orderOutForDeliveryEvent.getOrderId());
        orderService.updateOrder(
                UUID.fromString(orderOutForDeliveryEvent.getOrderId()),
                Order.builder()
                        .courierId(UUID.fromString(orderOutForDeliveryEvent.getCourierId()))
                        .status(OrderStatus.OUT_FOR_DELIVERY)
                        .build());
    }

    private void handleOrderPackagedEvent(final OrderPackagedEvent orderPackagedEvent) {
        log.info("✅ Order packaged for order: {}",
                orderPackagedEvent.getOrderId());
        orderService.updateOrderStatus(
                UUID.fromString(orderPackagedEvent.getOrderId()),
                OrderStatus.PACKAGED);
    }
}
