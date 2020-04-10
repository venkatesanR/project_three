package com.techmania.spboot.configuration.kafka;

import com.techmania.spboot.properties.BaseProperties;
import lombok.Builder;

@Builder
public class KafkaConfiguration implements BaseProperties {
    private String host;
    private String brokers;
}