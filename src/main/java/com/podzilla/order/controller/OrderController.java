package com.podzilla.order.controller;

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


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order API", description = "Order management operations")
public class OrderController {

    private final OrderService orderService;
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }



    @PostMapping
    @Operation(summary = "Create a new order",
            description = "Creates a new order and returns the created order")
    @ApiResponse(responseCode = "200", description = "Order created "
            + "successfully")
    public ResponseEntity<Order> createOrder(@RequestBody final Order order) {
        LOGGER.info("Creating new order: {}", order);
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }



    @GetMapping
    @Operation(summary = "Get all orders",
            description = "Returns a list of all orders")
    @ApiResponse(responseCode = "200", description = "List of orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        LOGGER.info("Fetching all orders");
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }



    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID",
            description = "Returns an order by its ID")
    @ApiResponse(responseCode = "200", description = "Order found")
    public ResponseEntity<Order> getOrderById(@PathVariable final UUID id) {
        LOGGER.info("Fetching order with ID: {}", id);
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return ResponseEntity.ok(order);
    }



    @PatchMapping("/{id}")
    @Operation(summary = "Update an order",
            description = "Updates an existing order and returns the updated "
                    + "order")
    @ApiResponse(responseCode = "200", description = "Order updated "
            + "successfully")
    public ResponseEntity<Order> updateOrder(@PathVariable final UUID id,
                                             @RequestBody final Order order) {
        LOGGER.info("Updating order with ID: {}", id);
        Order updatedOrder = orderService.updateOrder(id, order);
        return ResponseEntity.ok(updatedOrder);
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order",
            description = "Deletes an order by its ID")
    @ApiResponse(responseCode = "204", description = "Order deleted "
            + "successfully")
    public ResponseEntity<Void> deleteOrder(@PathVariable final UUID id) {
        LOGGER.info("Deleting order with ID: {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get order by user ID",
            description = "Fetches an order based on the provided user ID"
    )
    @ApiResponse(
            responseCode = "200", description = "Order found"
    )
    public ResponseEntity<Optional<Order>> getOrderByUserId(
            @PathVariable final UUID userId) {
        Optional<Order> order = orderService.getOrderByUserId(userId);
        LOGGER.info("Order found for user ID: {}", userId);
        return ResponseEntity.ok(order);
    }



    @PutMapping("/cancel/{id}")
    @Operation(
            summary = "Cancel order",
            description = "Cancels an order based on the provided order ID"
    )
    @ApiResponse(
            responseCode = "200", description = "Order cancelled"
    )
    public ResponseEntity<Order> cancelOrder(@PathVariable final UUID id,
                                             @RequestBody final String reason) {
        Order order = orderService.cancelOrder(id, reason);
        LOGGER.info("Order with ID: {} cancelled", id);
        return ResponseEntity.ok(order);
    }



    @PutMapping("/status/{id}")
    @Operation(
            summary = "Update order status",
            description = "Updates the status of an order based on "
                    + "the provided order ID"
    )
    @ApiResponse(
            responseCode = "200", description = "Order status updated"
    )
    public ResponseEntity<Order> updateOrderStatus(@PathVariable final UUID id,
                                       @RequestBody final OrderStatus status) {
        Order order = orderService.updateOrderStatus(id, status);
        LOGGER.info("Order status updated for ID: {}", id);
        return ResponseEntity.ok(order);
    }



    @GetMapping("/trackOrder/{id}")
    @Operation(
            summary = "Track order",
            description = "Tracks the location of an order based on "
                    + "the provided order ID"
    )
    @ApiResponse(
            responseCode = "200", description = "Order location tracked"
    )
    public ResponseEntity<OrderLocation> trackOrder(@PathVariable final UUID id) {
        OrderLocation location = orderService.trackOrder(id);
        LOGGER.info("Order location tracked for ID: {}", id);
        return ResponseEntity.ok(location);
    }
}
