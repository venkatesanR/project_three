package com.techmania.spboot.config;

import com.techmania.spboot.beans.Coffee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableConfigurationProperties(CoffeeProperties.class)
public class CoffeeConfiguration {
    @Autowired
    private CoffeeProperties coffee;

    @Bean
    @ConditionalOnMissingBean
    public Coffee coffee() {
        return new Coffee(coffee.getName(), coffee.getPrice(), System.currentTimeMillis());
    }
}