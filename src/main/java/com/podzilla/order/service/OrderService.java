package com.podzilla.order.service;

import com.podzilla.mq.events.*;
import com.podzilla.order.exception.NotFoundException;
import com.podzilla.order.messaging.OrderProducer;
import com.podzilla.order.model.Order;
import com.podzilla.order.model.OrderProduct;
import com.podzilla.order.model.OrderStatus;
import com.podzilla.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    @Autowired
    public OrderService(final OrderRepository orderRepository,
                        final OrderProducer orderProducer) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
    }

    public Order createOrder(final Order order) {
        log.info("Creating new order: {}", order);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        OrderStockReservationRequestedEvent stockReservationRequest =
                OrderStockReservationRequestedEvent.builder()
                        .orderId(order.getId().toString())
                        .items(getOrderItems(order))
                        .build();
        orderProducer.sendStockReservationRequest(stockReservationRequest);
        return order;
    }

    public Order placeOrder(final UUID orderId) {
        log.info("Placing order with ID: {}", orderId);
        Order order = updateOrderStatus(orderId, OrderStatus.PLACED);
        OrderPlacedEvent orderPlaced =
                OrderPlacedEvent.builder()
                        .orderId(order.getId().toString())
                        .customerId(order.getUserId().toString())
                        .items(getOrderItems(order))
                        .totalAmount(order.getTotalAmount())
                        .build();
        orderProducer.sendOrderPlaced(orderPlaced);
        return order;
    }

    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(final UUID id) {
        log.info("Fetching order with ID: {}", id);
        return orderRepository.findById(id);
    }

    public Order updateOrder(final UUID id, final Order updatedOrder) {
        log.info("Updating order with ID: {}", id);
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            BeanUtils.copyProperties(updatedOrder, order, "id");
            order.setUpdatedAt(LocalDateTime.now());
            log.info("Order with id: {} was found and updated", id);
            return orderRepository.save(order);
        }
        log.warn("Order with id: {} was not found", id);
        throw new RuntimeException("Order not found with id: " + id);
    }

    public void deleteOrder(final UUID id) {
        if (orderRepository.existsById(id)) {
            log.warn("Deleting order with ID: {}", id);
            orderRepository.deleteById(id);
        } else {
            log.warn("Order with ID: {} not found for deletion", id);
            throw new RuntimeException("Order not found with id: " + id);
        }
    }

    public Optional<Order> getOrderByUserId(final UUID userId) {
        log.info("Fetching order with user ID: {}", userId);

        Optional<Order> order = orderRepository.findByUserId(userId);

        checkNotFoundException(order.orElse(null),
                "Order not found with user ID: " + userId);

        return orderRepository.findByUserId(userId);
    }

    public Order cancelOrder(final UUID id) {
        log.info("Cancelling order with ID: {}", id);

        Optional<Order> existingOrder = orderRepository.findById(id);

        checkNotFoundException(existingOrder.orElse(null),
                "Order not found with id: " + id);

        Order order = existingOrder.get();
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        OrderCancelledEvent orderCancelledEvent =
                OrderCancelledEvent.builder()
                        .orderId(order.getId().toString())
                        .customerId(order.getUserId().toString())
                        .reason("Customer requested cancellation")
                        .build();
        orderProducer.sendCancelOrder(orderCancelledEvent);
        return order;
    }

    public Order updateOrderStatus(final UUID id,
                                   final OrderStatus status) {
        log.info("Updating order status with ID: {}", id);

        Optional<Order> existingOrder = orderRepository.findById(id);

        checkNotFoundException(existingOrder.orElse(null),
                "Order not found with id: " + id);

        Order order = existingOrder.get();
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    private void checkNotFoundException(final Object value,
                                        final String message) {
        if (value == null) {
            throw new NotFoundException(message);
        }
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
