package com.podzilla.order.controller;

import com.podzilla.order.model.Order;
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
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @ApiResponse(responseCode = "200", description = "Order created " +
            "successfully")
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
    public ResponseEntity<Order> getOrderById(@PathVariable final long id) {
        LOGGER.info("Fetching order with ID: {}", id);
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an order",
            description = "Updates an existing order and returns the updated "
                    + "order")
    @ApiResponse(responseCode = "200", description = "Order updated " +
            "successfully")
    public ResponseEntity<Order> updateOrder(@PathVariable final long id,
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
    public ResponseEntity<Void> deleteOrder(@PathVariable final long id) {
        LOGGER.info("Deleting order with ID: {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
