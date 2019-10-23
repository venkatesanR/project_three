package com.techmania.designprinciples.behaviour.observer;

import java.util.Observable;
import java.util.Observer;

public class StateMonitorDisplay implements Observer, Display {
    private Observable observable;
    private WeatherData weatherData;

    public StateMonitorDisplay(Observable observable) {
        this.observable = observable;
        this.observable.addObserver(this);
    }

    @Override
    public void display() {
        System.out.println("From NationalTV" + weatherData.toString());
    }

    @Override
    public void update(Observable o, Object data) {
        if (o instanceof WeatherDataPublisher) {
            this.weatherData = ((WeatherDataPublisher) o).getWeatherData();
            display();
        }
    }
}
