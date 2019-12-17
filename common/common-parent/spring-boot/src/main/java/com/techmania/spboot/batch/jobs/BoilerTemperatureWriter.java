package com.techmania.spboot.batch.jobs;

import org.springframework.batch.item.ItemWriter;
import java.util.List;

public class BoilerTemperatureWriter implements ItemWriter<String> {
    @Override
    public void write(List<? extends String> list) throws Exception {
        System.out.println("Writing");
    }
}
