package com.podzilla.order.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    private double first;  // Latitude
    private double second; // Longitude
}
