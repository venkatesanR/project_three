package com.techmania.spboot.batch.jobs;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TemperatureAnalyser {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job waterHeatAnalyzer(@Autowired Step boilerSensor) {
        return jobBuilderFactory.get("waterHeatAnalyzer")
                .incrementer((parameters) -> {
                    Map<String, JobParameter> data = new HashMap<>();
                    data.put("jobParam",
                            new JobParameter(UUID.randomUUID().toString()));
                    return new JobParameters(data);
                })
                .start(boilerSensor)
                .build();
    }

    @Bean
    public Step boilerSensor() {
        return stepBuilderFactory.get("boilerSensor")
                .<String, String>chunk(10)
                .reader(new BoilerTemperatureReader())
                .writer(new BoilerTemperatureWriter())
                .processor(new BoilerTemperatureProcessor()).build();
    }
}