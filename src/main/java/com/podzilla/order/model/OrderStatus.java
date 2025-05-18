package com.podzilla.order.model;

public enum OrderStatus {
    PENDING,
    PLACED,
    PACKAGED,
    ASSIGNED_TO_COURIER,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FULFILLMENT_FAILED,
    DELIVERY_FAILED,
    CANCELLED,
}

