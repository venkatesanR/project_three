package com.techmania.spboot.controllers;

import com.techmania.spboot.beans.Coffee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/coffee")
public class CoffeeController {
    private static final Logger logger = LoggerFactory.getLogger(CoffeeController.class);

    @Autowired
    private Coffee coffee;

    @RequestMapping("/getcoffee")
    public Coffee getCoffee() {
        logger.info("Coffee created from factory: {}", coffee);
        return coffee;
    }
}