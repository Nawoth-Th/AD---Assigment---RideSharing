package com.ridelink.driver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private String color;
    private VehicleType vehicleType;
    private int passengerCapacity;
}
