package com.podzilla.order.controller;


import com.podzilla.order.model.Order;
import com.podzilla.order.model.OrderStatus;
import com.podzilla.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody final Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable final long id) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable final long id,
                                             @RequestBody final Order order) {
        Order updatedOrder = orderService.updateOrder(id, order);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable final long id) {
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
            @PathVariable final long userId) {
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
    public ResponseEntity<Order> cancelOrder(@PathVariable final long id) {
        Order order = orderService.cancelOrder(id);
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
    public ResponseEntity<Order> updateOrderStatus(@PathVariable final long id,
                                       @RequestBody final OrderStatus status) {
        Order order = orderService.updateOrderStatus(id, status);
        LOGGER.info("Order status updated for ID: {}", id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/checkout/{id}")
    @Operation(
            summary = "Checkout order",
            description = "Checks out an order based on the provided order ID"
    )
    @ApiResponse(
            responseCode = "200", description = "Order checked out"
    )
    public ResponseEntity<Order> checkoutOrder(@PathVariable final long id) {
        Order order = orderService.checkoutOrder(id);
        LOGGER.info("Order with ID: {} checked out", id);
        return ResponseEntity.ok(order);
    }
}
