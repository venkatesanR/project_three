package com.techmania.spboot.configuration.mysql;

import com.techmania.spboot.properties.BaseProperties;
import lombok.Builder;

@Builder
public class RestConfiguration implements BaseProperties {
    private String endpoint;
}