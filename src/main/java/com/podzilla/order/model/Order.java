package com.podzilla.order.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GenerationType;
import com.podzilla.mq.events.ConfirmationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    private UUID courierId;

    private BigDecimal totalAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Address shippingAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private ConfirmationType confirmationType;

    private String signature;

    private double orderLatitude;

    private double orderLongitude;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnore
    private List<OrderProduct> orderProducts = new ArrayList<>();

    public static class Builder {
        private UUID id;
        private UUID userId;
        private UUID courierId;
        private BigDecimal totalAmount;
        private Address shippingAddress;
        private OrderStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private ConfirmationType confirmationType;
        private String signature;
        private double orderLatitude;
        private double orderLongitude;
        private List<OrderProduct> orderProducts = new ArrayList<>();

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder userId(final UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder courierId(final UUID courierId) {
            this.courierId = courierId;
            return this;
        }

        public Builder totalAmount(final BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder shippingAddress(final Address shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Builder status(final OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(final LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder confirmationType(final ConfirmationType confirmationType) {
            this.confirmationType = confirmationType;
            return this;
        }

        public Builder signature(final String signature) {
            this.signature = signature;
            return this;
        }

        public Builder orderLatitude(final double orderLatitude) {
            this.orderLatitude = orderLatitude;
            return this;
        }

        public Builder orderLongitude(final double orderLongitude) {
            this.orderLongitude = orderLongitude;
            return this;
        }

        public Builder orderProducts(final List<OrderProduct> orderProducts) {
            this.orderProducts = orderProducts;
            return this;
        }

        public Order build() {
            Order order = new Order();
            order.setId(id);
            order.setUserId(userId);
            order.setCourierId(courierId);
            order.setTotalAmount(totalAmount);
            order.setShippingAddress(shippingAddress);
            order.setStatus(status);
            order.setCreatedAt(createdAt);
            order.setUpdatedAt(updatedAt);
            order.setConfirmationType(confirmationType);
            order.setSignature(signature);
            order.setOrderLatitude(orderLatitude);
            order.setOrderLongitude(orderLongitude);
            order.setOrderProducts(orderProducts);
            return order;
        }
    }
}
