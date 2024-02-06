package com.example.practicpogoda;
public class Forecast {
    private String date;
    private String city;
    private int weatherIcon;
    private String temperature;

    public Forecast(String date, String city, int weatherIcon, String temperature) {
        this.date = date;
        this.city = city;
        this.weatherIcon = weatherIcon;
        this.temperature = temperature;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(int weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
