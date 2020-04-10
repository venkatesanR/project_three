package com.techmania.spboot.config;

import com.techmania.spboot.properties.BaseProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

@ConfigurationProperties("keyvalue")
@Getter
@Setter
public class KeyValueConfiguration {
    private String data;
    private Inbound inbound;

    @ConstructorBinding
    @Getter
    static class Inbound {
        private final Map<String, ConfigOne> artist;

        Inbound(Map<String, ConfigOne> artist) {
            this.artist = artist;
        }
    }
}
