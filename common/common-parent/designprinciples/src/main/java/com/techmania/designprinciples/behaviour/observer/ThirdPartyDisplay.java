package com.techmania.designprinciples.behaviour.observer;

import java.util.Observable;
import java.util.Observer;

public class ThirdPartyDisplay implements Observer, Display {
    private Observable observable;
    private WeatherData weatherData;

    public ThirdPartyDisplay(Observable observable) {
        this.observable = observable;
        this.observable.addObserver(this);
    }

    @Override
    public void display() {
        System.out.println("From SunTV: " + weatherData.toString());
    }

    @Override
    public void update(Observable o, Object data) {
        if (o instanceof WeatherDataPublisher) {
            this.weatherData = ((WeatherDataPublisher) o).getWeatherData();
            display();
        }
    }
}
