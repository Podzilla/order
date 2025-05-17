package com.podzilla.order.messaging;

import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.QueueResolver;
import com.podzilla.mq.events.*;
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
        if(payload instanceof WarehouseStockReservedEvent warehouseStockReservedEvent) {
            log.info("✅ Stock reserved for order: {}", warehouseStockReservedEvent.getOrderId());
            orderService.placeOrder(UUID.fromString(warehouseStockReservedEvent.getOrderId()));
        }
        if(payload instanceof WarehouseOrderFulfillmentFailedEvent warehouseOrderFulfillmentFailedEvent){
            log.info("❌ Order fulfillment failed for order: {}, reason: {}",
                    warehouseOrderFulfillmentFailedEvent.getOrderId(),
                    warehouseOrderFulfillmentFailedEvent.getReason());
            orderService.updateOrderStatus(
                    UUID.fromString(warehouseOrderFulfillmentFailedEvent.getOrderId()), OrderStatus.FAILED);
        }
    }

    @RabbitListener(queues = EventsConstants.ORDER_ORDER_EVENT_QUEUE)
    public void trackOrder(final BaseEvent payload) {
        if(payload instanceof OrderPackagedEvent orderPackagedEvent) {
            log.info("✅ Order packaged for order: {}",
                    orderPackagedEvent.getOrderId());
            orderService.updateOrderStatus(
                    UUID.fromString(orderPackagedEvent.getOrderId()),
                    OrderStatus.PACKAGED);
        }
        if(payload instanceof OrderOutForDeliveryEvent orderOutForDeliveryEvent){
            log.info("✅ Order out for delivery for order: {}",
                    orderOutForDeliveryEvent.getOrderId());
            orderService.updateOrderStatus(
                    UUID.fromString(orderOutForDeliveryEvent.getOrderId()),
                    OrderStatus.SHIPPED);
        }
        if(payload instanceof OrderDeliveredEvent orderDeliveredEvent) {
            log.info("✅ Order delivered for order: {}",
                    orderDeliveredEvent.getOrderId());
            orderService.updateOrderStatus(
                    UUID.fromString(orderDeliveredEvent.getOrderId()),
                    OrderStatus.DELIVERED);
        }
        if(payload instanceof CartCheckedoutEvent cartCheckedoutEvent) {
            log.info("✅ Cart checked out for user with id: {}",
                    cartCheckedoutEvent.getCustomerId());
            Address address = new Address();
            address.setStreet(cartCheckedoutEvent.getDeliveryAddress().getStreet());
            address.setCity(cartCheckedoutEvent.getDeliveryAddress().getCity());
            address.setState(cartCheckedoutEvent.getDeliveryAddress().getState());
            address.setCountry(cartCheckedoutEvent.getDeliveryAddress().getCountry());
            address.setPostalCode(cartCheckedoutEvent.getDeliveryAddress().getPostalCode());
            List<OrderProduct> orderProducts =
                    cartCheckedoutEvent.getItems().stream()
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
            orderService.createOrder(order);
        }
    }

    public static void main(String[] args) {
       System.out.println( QueueResolver.getQueueForServiceEvent(EventsConstants.SERVICE_ORDER,
               EventsConstants.ORDER_PLACED));
    }
}
