package com.techmania.designprinciples.behaviour.observer;

import java.util.Objects;

public class WeatherData {
    private Double temprature;
    private Double humidity;
    private Double presure;

    public WeatherData(Double temprature, Double humidity, Double presure) {
        this.temprature = temprature;
        this.humidity = humidity;
        this.presure = presure;
    }

    public Double getTemprature() {
        return temprature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getPresure() {
        return presure;
    }

    @Override
    public String toString() {
        return String.format("[Temprature:%s,Humidity:%s,Presure:%s]", temprature, humidity, presure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temprature, humidity, presure);
    }

    @Override
    public boolean equals(Object obj) {
        WeatherData that = (WeatherData) obj;
        return (temprature.equals(that.temprature) &&
                humidity.equals(that.humidity) &&
                presure.equals(that.presure));
    }
}
