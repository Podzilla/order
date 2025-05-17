//package com.podzilla.order.model;
//
//import jakarta.persistence.Entity;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.List;
//
//@Entity
//@Getter
//@Setter
//public class OrderPlaced implements Serializable {
//    private String orderId;
//    private String customerId;
//    private BigDecimal totalAmount;
//    private List<OrderProduct> items;
//    private Address deliveryAddress;
//
//    public OrderPlaced(final Order order) {
//        this.orderId = order.getId().toString();
//        this.customerId = order.getUserId().toString();
//        this.totalAmount = order.getTotalAmount();
//        this.items = order.getOrderProducts();
//        this.deliveryAddress = order.getShippingAddress();
//    }
//}
