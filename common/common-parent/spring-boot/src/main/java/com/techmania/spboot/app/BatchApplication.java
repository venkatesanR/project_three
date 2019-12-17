package com.techmania.spboot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.GenericApplicationContext;

@SpringBootApplication
@ComponentScan("com.techmania.spboot.batch")
public class BatchApplication {
    public static void main(String[] args) {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        SpringApplication.run(BatchApplication.class);
    }
}
