package com.techmania.spboot.config;

import com.techmania.spboot.properties.BaseProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("actor")
public class ConfigOne implements BaseProperties {
    private String keyOne;
    private String keyTwo;
}
