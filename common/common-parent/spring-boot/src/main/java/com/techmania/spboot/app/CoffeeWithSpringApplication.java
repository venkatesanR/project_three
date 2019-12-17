package com.techmania.spboot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoffeeWithSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoffeeWithSpringApplication.class);
    }

    //TODO
    //ConditionalOnClass
    //ConditionalOnMissingBean
}