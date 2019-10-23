package com.techmania.designprinciples.behaviour.observer;

import java.util.Observable;

public class WeatherDataPublisher extends Observable {
    private WeatherData weatherData;

    public void onWeatherChange(WeatherData weatherData) {
        if (this.weatherData != null && this.weatherData.equals(weatherData)) {
            System.out.println("No change in data found,So avoid emitting changes");
            return;
        }
        setChanged();
        this.weatherData = weatherData;
        notifyObservers(weatherData);
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }
}
