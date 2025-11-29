package com.booknplay.turf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TurfServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TurfServiceApplication.class, args);
    }
}
