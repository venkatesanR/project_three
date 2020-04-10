package com.techmania.spboot.config;

import com.techmania.spboot.properties.BaseProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

@ConfigurationProperties("event")
@Setter
@Getter
@ToString
public class EventDrivenAppConfig {
    private DynaConfigInvokerFactory invokerFactory;

    public EventDrivenAppConfig(@Autowired Environment environment) {
        this.invokerFactory = new DynaConfigInvokerFactory(environment);
    }

    private ConfigOne one = new ConfigOne();
    private ConfigTwo two = new ConfigTwo();
    private ConfigThree three = new ConfigThree();

    @Getter
    @Setter
    public class BaseConfiguration {
        private String prefix;
        private String type;
        private BaseProperties properties;

        public BaseConfiguration(String prefix) {
            this.prefix = prefix;
        }

        public BaseProperties getProperties() {
            if (properties == null) {
                properties = invokerFactory.getConfig(type, prefix);
            }
            return properties;
        }
    }

    public class ConfigOne extends BaseConfiguration {
        public ConfigOne() {
            super("event.one");
        }
    }

    public class ConfigTwo extends BaseConfiguration {
        public ConfigTwo() {
            super("event.two");
        }
    }

    public class ConfigThree extends BaseConfiguration {
        public ConfigThree() {
            super("event.three");
        }
    }
}
