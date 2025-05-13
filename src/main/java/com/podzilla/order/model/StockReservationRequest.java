package com.podzilla.order.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
public class StockReservationRequest implements Serializable {
    private String orderId;
    private List<OrderItem> items;

    public StockReservationRequest(
            final String orderId,
            final List<OrderItem> items) {
        this.orderId = orderId;
        this.items = items;
    }
}
