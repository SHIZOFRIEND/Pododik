package com.example.practicpogoda;

import com.google.gson.annotations.SerializedName;
public class CurrentWeather {
    @SerializedName("temp_c")
    private double temperatureCelsius;
    @SerializedName("temp_f")
    private double temperatureFahrenheit;
    @SerializedName("condition")
    private WeatherCondition weatherCondition;
    @SerializedName("humidity")
    private int humidity;
    @SerializedName("wind_kph")
    private double windSpeedKph;
    @SerializedName("wind_mph")
    private double windSpeedMph;
    @SerializedName("city_name")
    private String cityName;
    public double getTemperatureCelsius() {
        return temperatureCelsius;
    }
    public double getTemperatureFahrenheit() {
        return temperatureFahrenheit;
    }
    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    public WeatherCondition getWeatherCondition() {
        return weatherCondition;
    }
    public int getHumidity() {
        return humidity;
    }
    public double getWindSpeedMph() {
        return windSpeedMph;
    }
    public double getWindSpeedKph() {
        return windSpeedKph;
    }
}
