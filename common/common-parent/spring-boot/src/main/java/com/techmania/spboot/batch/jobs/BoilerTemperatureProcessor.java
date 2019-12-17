package com.techmania.spboot.batch.jobs;

import org.springframework.batch.item.ItemProcessor;

public class BoilerTemperatureProcessor implements ItemProcessor<String, String> {
    @Override
    public String process(String s) throws Exception {
        System.out.println("Reading");
        return "From processor";
    }
}
