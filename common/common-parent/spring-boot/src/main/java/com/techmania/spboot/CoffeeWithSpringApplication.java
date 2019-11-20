package com.techmania.spboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ignore this is for CI baby
 */
@SpringBootApplication
public class CoffeeWithSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoffeeWithSpringApplication.class);
    }

    //ConditionalOnClass
    //ConditionalOnMissingBean
}