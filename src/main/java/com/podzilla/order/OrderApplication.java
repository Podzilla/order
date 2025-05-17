package com.podzilla.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.podzilla"})
public class OrderApplication {

    public static void main(final String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
