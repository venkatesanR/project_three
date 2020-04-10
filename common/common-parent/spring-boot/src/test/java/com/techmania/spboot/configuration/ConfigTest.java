package com.techmania.spboot.configuration;

import com.techmania.spboot.config.KeyValueConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = BasicConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigTest {
    @Autowired
    private KeyValueConfiguration keyValueConfiguration;
    @Autowired
    private com.techmania.spboot.config.EventDrivenAppConfig eventDrivenAppConfig;

    @Test
    public void testKeyValues() {
        System.out.println(keyValueConfiguration);
    }

    @Test
    public void testProperties() {
        System.out.println(eventDrivenAppConfig.getOne().getProperties());
        System.out.println(eventDrivenAppConfig.getTwo().getProperties());
        System.out.println(eventDrivenAppConfig.getThree().getProperties());
    }
}
