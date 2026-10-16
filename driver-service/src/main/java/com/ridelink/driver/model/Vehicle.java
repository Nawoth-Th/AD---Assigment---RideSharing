package com.ridelink.driver.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Vehicle {

    @Schema(description = "Vehicle manufacturer", example = "Toyota")
    private String make;

    @Schema(description = "Vehicle model", example = "Prius")
    private String model;

    @Schema(description = "Manufacturing year", example = "2021")
    private int year;

    @Schema(description = "Registered license plate", example = "WP CAB-1234")
    private String licensePlate;

    @Schema(description = "Vehicle exterior color", example = "White")
    private String color;

    @Schema(description = "Category of vehicle (SEDAN, SUV, HATCHBACK, VAN)", example = "SEDAN")
    private VehicleType vehicleType;

    @Schema(description = "Maximum passenger capacity", example = "4")
    private int passengerCapacity;

    public Vehicle() {
    }

    public Vehicle(String make, String model, int year, String licensePlate, String color, VehicleType vehicleType, int passengerCapacity) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.color = color;
        this.vehicleType = vehicleType;
        this.passengerCapacity = passengerCapacity;
    }
}
