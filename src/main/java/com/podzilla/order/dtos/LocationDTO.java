package com.podzilla.order.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    private BigDecimal first;  // Latitude
    private BigDecimal second; // Longitude
}
