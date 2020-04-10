package com.techmania.spboot.configuration;

import com.techmania.spboot.config.EventDrivenAppConfig;
import com.techmania.spboot.config.KeyValueConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        EventDrivenAppConfig.class,
        KeyValueConfiguration.class})
public class BasicConfiguration {
}
