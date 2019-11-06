package com.techmania.spboot.config;

import com.techmania.spboot.beans.Coffee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CoffeeProperties.class)
public class CoffeeConfiguration {
    @Autowired
    private CoffeeProperties coffee;

    @Bean
    public Coffee coffee() {
        return new Coffee(coffee.getName(), coffee.getPrice());
    }
}