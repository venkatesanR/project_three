package com.techmania.designprinciples.behaviour.observer;

public class WeatherStations {
    public static void main(String[] args) {
        System.out.println("Started presenting weather data");
        WeatherStations stations = new WeatherStations();

        WeatherDataPublisher dataPublisher = new WeatherDataPublisher();
        new StateMonitorDisplay(dataPublisher);
        ThirdPartyDisplay sunTv = new ThirdPartyDisplay(dataPublisher);

        dataPublisher.onWeatherChange(stations.getWeatherData(10.3, 20.3, 30.3));
        dataPublisher.onWeatherChange(stations.getWeatherData(10.3, 20.3, 30.3));
        dataPublisher.deleteObserver(sunTv);
        dataPublisher.onWeatherChange(stations.getWeatherData(90.3, 54.2, 30.3));

        System.out.println("Completed presenting weather data");
    }

    private WeatherData getWeatherData(Double temprature, Double humidity, Double presure) {
        return new WeatherData(temprature, humidity, presure);
    }
}
