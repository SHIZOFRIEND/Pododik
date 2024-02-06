package com.example.practicpogoda;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherForecastResponse {
    @SerializedName("forecast")
    private Forecast forecast;

    public Forecast getForecast() {
        return forecast;
    }

    public static class Forecast {
        @SerializedName("forecastday")
        private List<ForecastDay> forecastDays;

        public List<ForecastDay> getForecastDays() {
            return forecastDays;
        }
    }

    public static class ForecastDay {
        @SerializedName("date")
        private String date;

        @SerializedName("day")
        private Day day;

        public String getDate() {
            return date;
        }

        public Day getDay() {
            return day;
        }
    }

    public static class Day {
        @SerializedName("maxtemp_c")
        private double maxTempCelsius;

        @SerializedName("mintemp_c")
        private double minTempCelsius;

        @SerializedName("condition")
        private WeatherCondition weatherCondition;

        public double getMaxTempCelsius() {
            return maxTempCelsius;
        }

        public double getMinTempCelsius() {
            return minTempCelsius;
        }

        public WeatherCondition getWeatherCondition() {
            return weatherCondition;
        }
    }
}
