package com.example.practicpogoda;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("current")
    private CurrentWeather currentWeather;
    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }
}
