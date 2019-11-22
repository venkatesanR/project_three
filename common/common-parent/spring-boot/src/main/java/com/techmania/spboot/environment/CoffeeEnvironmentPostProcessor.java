package com.techmania.spboot.environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public class CoffeeEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CoffeeEnvironmentPostProcessor.class);

    //TODO:Fixme me logger
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println("************ Started customizing coffee environment *************");
    }
}
