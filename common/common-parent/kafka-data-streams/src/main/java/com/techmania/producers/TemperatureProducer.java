package com.techmania.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class TemperatureProducer {
    private KafkaProducer kafkaProducer = null;

    public TemperatureProducer() {
        load();
    }

    public void send(String data) {
        ProducerRecord<String, String> temprature = new ProducerRecord("test", "key", data);
        try {
            RecordMetadata out = (RecordMetadata) kafkaProducer.send(temprature).get();
            System.out.println(out);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void asyncSend() {

    }

    private void load() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer(properties);
    }

    public static void main(String[] args) {
        TemperatureProducer producer = new TemperatureProducer();
        int count = 0;
        while (count < 10) {
            try {
                count += 1;
                producer.send(String.valueOf(System.nanoTime()));
                Thread.sleep(5000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}