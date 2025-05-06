package com.podzilla.order.service;

import com.podzilla.order.model.Order;
import com.podzilla.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(final Order order) {
        log.info("Creating new order: {}", order);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(final long id) {
        log.info("Fetching order with ID: {}", id);
        return orderRepository.findById(id);
    }

    public Order updateOrder(final long id, final Order updatedOrder) {
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

    public void deleteOrder(final long id) {
        if (orderRepository.existsById(id)) {
            log.warn("Deleting order with ID: {}", id);
            orderRepository.deleteById(id);
        } else {
            log.warn("Order with ID: {} not found for deletion", id);
            throw new RuntimeException("Order not found with id: " + id);
        }
    }
}
