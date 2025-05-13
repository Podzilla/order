package com.podzilla.order.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class StockReservationResponse {
    private String orderId;
    private String reason;
}
