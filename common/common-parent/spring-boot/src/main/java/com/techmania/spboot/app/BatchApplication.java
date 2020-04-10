package com.techmania.spboot.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class BatchApplication {
    public static void run(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.scan("com.techmania.spboot.batch");
    }
}
