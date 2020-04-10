package com.techmania.spboot.config;


import com.techmania.spboot.configuration.kafka.KafkaConfiguration;
import com.techmania.spboot.configuration.mysql.RestConfiguration;
import com.techmania.spboot.properties.BaseProperties;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DynaConfigInvokerFactory {
    private Map<String, Function<String, BaseProperties>> FUNCTION_MAPPER = new HashMap<>();

    private Environment environment;

    public DynaConfigInvokerFactory(Environment environment) {
        this.environment = environment;
        FUNCTION_MAPPER.put("rest", restConfig);
        FUNCTION_MAPPER.put("kafka", kafkaConfig);
    }


    private Function<String, BaseProperties> kafkaConfig = (prefix) -> {
        String host = prefix.concat(".kafka.host");
        String brokers = prefix.concat(".kafka.brokers");
        return KafkaConfiguration.builder()
                .host(environment.getProperty(host))
                .brokers(environment.getProperty(brokers)).build();
    };

    private Function<String, BaseProperties> restConfig = (prefix) -> {
        String endpoint = prefix.concat(".rest.endpoint");
        return RestConfiguration.builder()
                .endpoint(environment.getProperty(endpoint)).build();
    };

    public BaseProperties getConfig(String funcType, String prefix) {
        return FUNCTION_MAPPER.get(funcType).apply(prefix);
    }
}
