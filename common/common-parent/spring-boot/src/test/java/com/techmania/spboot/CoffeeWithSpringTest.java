package com.techmania.spboot;

import com.techmania.spboot.beans.Coffee;
import com.techmania.spboot.config.CoffeeConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

public class CoffeeWithSpringTest {

    private OutputCapture outputCapture = new OutputCapture();

    private final ApplicationContextRunner
            contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CoffeeConfiguration.class));

    @Test
    public void beanLoading() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(Coffee.class);
            assertThat(context.getBean(Coffee.class).getName()).isEqualTo("costa cafe");
        });
    }

    @Bean
    public Coffee coffee() {
        return new Coffee("TasteMeIamNew", 20, System.currentTimeMillis());
    }
}