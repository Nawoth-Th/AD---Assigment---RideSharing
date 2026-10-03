package com.ridelink.ride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication
public class RideServiceApplication {

    public static void main(String[] args) {
        loadEnv();
        SpringApplication.run(RideServiceApplication.class, args);
    }

    private static void loadEnv() {
        try {
            java.nio.file.Path env = java.nio.file.Paths.get(".env");
            if (!java.nio.file.Files.exists(env)) {
                env = java.nio.file.Paths.get("../.env");
            }
            if (java.nio.file.Files.exists(env)) {
                for (String line : java.nio.file.Files.readAllLines(env)) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                        int idx = line.indexOf('=');
                        String key = line.substring(0, idx).trim();
                        String value = line.substring(idx + 1).trim();
                        if (System.getProperty(key) == null && System.getenv(key) == null) {
                            System.setProperty(key, value);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
