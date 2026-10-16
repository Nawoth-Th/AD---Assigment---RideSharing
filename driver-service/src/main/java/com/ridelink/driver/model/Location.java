package com.ridelink.driver.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Location {
    private Double latitude;
    private Double longitude;
    private String address;
    @Builder.Default
    private Instant updatedAt = Instant.now();

    public Location() {
        this.updatedAt = Instant.now();
    }

    public Location(Double latitude, Double longitude, String address, Instant updatedAt) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }
}
