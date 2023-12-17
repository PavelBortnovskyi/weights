package com.neo.weights;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "com.neo.weights")
public class WeightsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeightsApplication.class, args);
    }
}
