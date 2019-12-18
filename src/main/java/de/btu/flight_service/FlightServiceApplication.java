package de.btu.flight_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.btu")
public class FlightServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightServiceApplication.class, args);
    }

}
