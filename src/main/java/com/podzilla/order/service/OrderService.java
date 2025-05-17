package com.podzilla.order.service;

import com.podzilla.mq.events.OrderCancelledEvent;
import com.podzilla.mq.events.OrderItem;
import com.podzilla.mq.events.OrderPlacedEvent;
import com.podzilla.mq.events.DeliveryAddress;
import com.podzilla.order.dtos.LocationDTO;
import com.podzilla.order.exception.NotFoundException;
import com.podzilla.order.messaging.OrderProducer;
import com.podzilla.order.model.Order;
import com.podzilla.order.model.OrderLocation;
import com.podzilla.order.model.OrderProduct;
import com.podzilla.order.model.OrderStatus;
import com.podzilla.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.podzilla.order.service.statusstrategy.OrderStatusStrategy;
import com.podzilla.order.service.statusstrategy.OrderStatusStrategyFactory;

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
    private final WebClient webClient;
    private final OrderStatusStrategyFactory strategyFactory;

    @Value("${api.gateway.url}")
    private String apiGatewayUrl;

    @Autowired
    public OrderService(final OrderRepository orderRepository,
                        final OrderProducer orderProducer,
                        final WebClient webClient,
                        final OrderStatusStrategyFactory strategyFactory) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
        this.webClient = webClient;
        this.strategyFactory = strategyFactory;
    }

    public Order createOrder(final Order order) {
        log.info("Creating new order: {}", order);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        OrderStatusStrategy strategy = strategyFactory.getStrategy(OrderStatus.PENDING);
        strategy.handle(order);

//        OrderStockReservationRequestedEvent stockReservationRequest =
//                OrderStockReservationRequestedEvent.builder()
//                        .orderId(order.getId().toString())
//                        .items(getOrderItems(order))
//                        .build();
//        orderProducer.sendStockReservationRequest(stockReservationRequest);
        return order;
    }

    public Order placeOrder(final UUID orderId) {
        log.info("Placing order with ID: {}", orderId);
        Order order = updateOrderStatus(orderId, OrderStatus.PLACED);
        DeliveryAddress deliveryAddress = new DeliveryAddress(
                order.getShippingAddress().getStreet(),
                order.getShippingAddress().getCity(),
                order.getShippingAddress().getState(),
                order.getShippingAddress().getCountry(),
                order.getShippingAddress().getPostalCode()
        );
        OrderPlacedEvent orderPlaced =
                OrderPlacedEvent.builder()
                        .orderId(order.getId().toString())
                        .customerId(order.getUserId().toString())
                        .items(getOrderItems(order))
                        .totalAmount(order.getTotalAmount())
                        .deliveryAddress(deliveryAddress)
                        .confirmationType(order.getConfirmationType())
                        .signature(order.getSignature())
                        .orderLatitude(order.getOrderLatitude())
                        .orderLongitude(order.getOrderLongitude())
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

    public Order cancelOrder(final UUID id, final String reason) {
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
                        .reason(reason)
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

    public OrderLocation trackOrder(final UUID id) {
        log.info("Tracking order with ID: {}", id);
        Optional<Order> existingOrder = orderRepository.findById(id);
        checkNotFoundException(existingOrder.orElse(null),
                "Order not found with id: " + id);
        String url = apiGatewayUrl + "/delivery-tasks/" + id + "/location";
        LocationDTO location = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(LocationDTO.class)
                .block();

        if (location == null) {
            throw new RuntimeException("Failed to get location for order " + id);
        }
        log.info("Order location: {}", location);
        return new OrderLocation(location.getFirst(), location.getSecond());
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
