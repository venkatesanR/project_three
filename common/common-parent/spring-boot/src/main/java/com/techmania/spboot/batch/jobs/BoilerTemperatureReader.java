package com.techmania.spboot.batch.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

public class BoilerTemperatureReader implements ItemReader<String> {
    private static final Logger logger = LoggerFactory.getLogger(BoilerTemperatureReader.class);

    @Override
    public String read() {
        System.out.println("Reading");
        logger.debug("Reading data from boiler sensor on: {}", System.currentTimeMillis());
        return "Boiler data";
    }
}
