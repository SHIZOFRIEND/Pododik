package com.example.practicpogoda;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface WeatherApiService {
    @GET("current.json")
    Call<WeatherResponse> getWeather(
            @Query("key") String apiKey,
            @Query("q") String cityName
    );
    @GET("forecast.json")
    Call<WeatherForecastResponse> getWeatherForecast7Days(@Query("key") String apiKey, @Query("q") String cityName, @Query("days") int days);
    @GET("forecast.json")
    Call<WeatherForecastResponse> getWeatherForecast14Days(@Query("key") String apiKey, @Query("q") String cityName, @Query("days") int days);
}