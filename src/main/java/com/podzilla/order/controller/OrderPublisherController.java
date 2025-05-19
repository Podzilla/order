package com.podzilla.order.controller;

import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.*;
import com.podzilla.order.model.Order;
import com.podzilla.order.model.OrderLocation;
import com.podzilla.order.model.OrderStatus;
import com.podzilla.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/testOrders")
@Tag(name = "Order API", description = "Order management operations")
public class OrderPublisherController {
    private final OrderService orderService;
    private final EventPublisher eventPublisher;
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderPublisherController(final OrderService orderService, final EventPublisher eventPublisher) {
        this.orderService = orderService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/testPublishCartCheckoutEvent")
    public ResponseEntity<String> testPublishCartCheckoutEvent() {
        LOGGER.info("Testing Event Publisher");
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(UUID.randomUUID().toString(), 5, new BigDecimal(100.0)));
        DeliveryAddress deliveryAddress = new DeliveryAddress(
                "123 Main St",
                "Springfield",
                "IL",
                "USA",
                "62701"
        );
        CartCheckedoutEvent event = new CartCheckedoutEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                orderItems,
                new BigDecimal(500.0),
                deliveryAddress,
                10.0,
                20.0,
                "signature",
                ConfirmationType.SIGNATURE
        );
        eventPublisher.publishEvent(EventsConstants.CART_CHECKEDOUT, event);
        return ResponseEntity.ok("Event published successfully");
    }

    @GetMapping("/testPublishWarehouseStockReservedEvent/{id}")
    public ResponseEntity<String> testPublishWarehouseStockReservedEvent(@PathVariable final UUID id) {
        LOGGER.info("Testing Event Publisher");

        WarehouseStockReservedEvent event = new WarehouseStockReservedEvent(
                id.toString());
        eventPublisher.publishEvent(EventsConstants.WAREHOUSE_STOCK_RESERVED, event);
        return ResponseEntity.ok("Event published successfully");
    }

    @GetMapping("/testPublishWarehouseOrderFulfillmentFailedEvent/{id}")
    public ResponseEntity<String> testWarehouseOrderFulfillmentFailedEvent(@PathVariable final UUID id) {
        LOGGER.info("Testing Event Publisher");

        WarehouseOrderFulfillmentFailedEvent event = new WarehouseOrderFulfillmentFailedEvent(
                id.toString(), "Order Fulfillment Failed");
        eventPublisher.publishEvent(EventsConstants.WAREHOUSE_ORDER_FULFILLMENT_FAILED, event);
        return ResponseEntity.ok("Event published successfully");
    }

    @GetMapping("/testOrderAssignedToCourierEvent/{id}")
    public ResponseEntity<String> testOrderAssignedToCourierEvent(@PathVariable final UUID id) {
        LOGGER.info("Testing Event Publisher");

        OrderAssignedToCourierEvent event = new OrderAssignedToCourierEvent(
                id.toString(), UUID.randomUUID().toString(), new BigDecimal(150.9), 10.0, 20.0, "signature",
                ConfirmationType.SIGNATURE);
        eventPublisher.publishEvent(EventsConstants.ORDER_ASSIGNED_TO_COURIER, event);
        return ResponseEntity.ok("Event published successfully");
    }

    @GetMapping("/testOrderDeliveredEvent/{id}/{courierId}")
    public ResponseEntity<String> testOrderDeliveredEvent(@PathVariable final UUID id, @PathVariable final String courierId) {
        LOGGER.info("Testing Event Publisher");

        OrderDeliveredEvent event = new OrderDeliveredEvent(
                id.toString(), courierId.toString(), new BigDecimal(150.9));
        eventPublisher.publishEvent(EventsConstants.ORDER_DELIVERED, event);
        return ResponseEntity.ok("Event published successfully");
    }

    @GetMapping("/testOrderOutForDeliveryEvent/{id}/{courierId}")
    public ResponseEntity<String> testOrderOutForDeliveryEvent(@PathVariable final UUID id, @PathVariable final String courierId) {
        LOGGER.info("Testing Event Publisher");

        OrderOutForDeliveryEvent event = new OrderOutForDeliveryEvent(
                id.toString(), courierId.toString());
        eventPublisher.publishEvent(EventsConstants.ORDER_OUT_FOR_DELIVERY, event);
        return ResponseEntity.ok("Event published successfully");
    }

    @GetMapping("/testOrderPackagedEvent/{id}")
    public ResponseEntity<String> testOrderPackagedEvent(@PathVariable final UUID id) {
        LOGGER.info("Testing Event Publisher");

        OrderPackagedEvent event = new OrderPackagedEvent(
                id.toString());
        eventPublisher.publishEvent(EventsConstants.ORDER_PACKAGED, event);
        return ResponseEntity.ok("Event published successfully");
    }

    @GetMapping("/testOrderDeliveryFailedEvent/{id}/{courierId}")
    public ResponseEntity<String> testOrderDeliveryFailedEvent(@PathVariable final UUID id,
                                                               @PathVariable final UUID courierId) {
        LOGGER.info("Testing Event Publisher");

        OrderDeliveryFailedEvent event = new OrderDeliveryFailedEvent(
                id.toString(), courierId.toString(), "Delivery failed");
        eventPublisher.publishEvent(EventsConstants.ORDER_DELIVERY_FAILED, event);
        return ResponseEntity.ok("Event published successfully");
    }

}
