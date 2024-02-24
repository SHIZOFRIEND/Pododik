package com.example.practicpogoda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_NOTIFICATION_PERMISSION = 123;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    public static final String ACTION_UPDATE_WIDGET = "com.example.practicpogoda.ACTION_UPDATE_WIDGET";

    private static final String PREF_LAST_CITY = "last_city";

    private static final String CHANNEL_ID = "weather_notifications";

    private static final String BASE_URL = "https://api.weatherapi.com/v1/";
    private static final String API_KEY = "21d4ec3dea1b4331b2e162049242402";
    private String CITY_NAME = "";
    private RecyclerView recyclerView7Days;
    private RecyclerView recyclerView14Days;
    private ForecastAdapter forecastAdapter7Days;
    private ForecastAdapter forecastAdapter14Days;
    private AppSettings appSettings;
    private SharedPreferences sharedPreferences;

    private TextView temperatureTextView;
    private TextView weatherConditionTextView;
    private TextView humidityTextView;
    private TextView windSpeedTextView;
    private EditText cityNameEditText;
    private ImageView weatherIconImageView;
    private boolean isLocationFetched = false;
    private Retrofit retrofit;
    private WeatherApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appSettings = new AppSettings(this);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(WeatherApiService.class);

        cityNameEditText = findViewById(R.id.cityNameEditText);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        weatherConditionTextView = findViewById(R.id.weatherConditionTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        recyclerView7Days = findViewById(R.id.forecastRecyclerView7Days);
        recyclerView14Days = findViewById(R.id.forecastRecyclerView14Days);

        forecastAdapter7Days = new ForecastAdapter(new ArrayList<>());
        recyclerView7Days.setAdapter(forecastAdapter7Days);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String temperatureUnit = sharedPreferences.getString("temperature_unit", "Celsius");
        forecastAdapter14Days = new ForecastAdapter(new ArrayList<>());
        recyclerView14Days.setAdapter(forecastAdapter14Days);
        RecyclerView recyclerView7Days = findViewById(R.id.forecastRecyclerView7Days);
        LinearLayoutManager layoutManager7Days = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView7Days.setLayoutManager(layoutManager7Days);

        ImageButton PreferenseButton = findViewById(R.id.PreferenseButton);
        PreferenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Preferences.class);
                startActivity(intent);
            }
        });
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String lastCity = preferences.getString(PREF_LAST_CITY, "");
        if (!lastCity.isEmpty()) {
            CITY_NAME = lastCity;
            fetchWeatherData();
            fetchWeatherForecast7Days(CITY_NAME);
            fetchWeatherForecast14Days(CITY_NAME);
            cityNameEditText.setText(CITY_NAME);
        }
        RecyclerView recyclerView14Days = findViewById(R.id.forecastRecyclerView14Days);
        LinearLayoutManager layoutManager14Days = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView14Days.setLayoutManager(layoutManager14Days);
        ImageButton newsButton = findViewById(R.id.newsbutton);
        newsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeatherNews();
            }
        });
        ImageButton weatherButton = findViewById(R.id.weatherButton);
        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeatherMap();
            }
        });

        fetchDefaultCityByLocation();

        cityNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String newCityName = s.toString().trim();
                if (!newCityName.isEmpty()) {
                    CITY_NAME = newCityName;
                    fetchWeatherData();

                    fetchWeatherForecast7Days(CITY_NAME);
                    fetchWeatherForecast14Days(CITY_NAME);
                    SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("cityName", newCityName);
                    editor.apply();
                }
            }
        });


    }

    private void fetchDefaultCityByLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();


                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    if (!addresses.isEmpty()) {
                                        CITY_NAME = addresses.get(0).getLocality();
                                        Log.d("Location", "City Name: " + CITY_NAME);
                                        updateCityNameEditText(CITY_NAME);
                                        fetchWeatherData();
                                        fetchWeatherForecast7Days(CITY_NAME);
                                        fetchWeatherForecast14Days(CITY_NAME);
                                    } else {
                                        Log.e("Location", "No address found for the location");

                                    }
                                } catch (IOException e) {
                                    Log.e("Location", "Error getting city name from location", e);
                                }
                            } else {
                                Log.e("Location", "Location is null");
                            }
                        }
                    });
        }
    }

    private void updateCityNameEditText(String cityName) {
        cityNameEditText.setText(cityName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                fetchDefaultCityByLocation();
            } else {

                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("com.example.practicpogoda.APP_CLOSED");
        sendBroadcast(intent);
    }
    private void fetchWeatherData() {
        Call<WeatherResponse> call = service.getWeather(API_KEY, CITY_NAME);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    updateUI(weatherResponse,sharedPreferences);
                    updateWidgetCity(CITY_NAME);
                    showWeatherNotification(weatherResponse);
                    SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PREF_LAST_CITY, CITY_NAME);
                    editor.apply();
                } else {
                    Log.e("WeatherAPI", "Failed to get weather data");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherAPI", "Error fetching weather data", t);
            }
        });
    }
    private void showWeatherNotification(WeatherResponse weatherResponse) {
        String temperature = String.valueOf(weatherResponse.getCurrentWeather().getTemperatureCelsius());
        String condition = weatherResponse.getCurrentWeather().getWeatherCondition().getConditionText();


        boolean isNotificationEnabled = appSettings.isNotificationEnabled();

        if (!isNotificationEnabled) {

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.cancelAll();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.weather_icon)
                .setContentTitle("Current Weather")
                .setContentText("Temperature: " + temperature + "°C, Condition: " + condition)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private void updateWidgetCity(String cityName) {

        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cityName", cityName);
        editor.apply();

        Intent updateWidgetIntent = new Intent(ACTION_UPDATE_WIDGET);
        updateWidgetIntent.putExtra("cityName", cityName);
        sendBroadcast(updateWidgetIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent("com.example.practicpogoda.APP_STARTED");
        sendBroadcast(intent);
    }


    private void fetchWeatherForecast7Days(String cityName) {
        Call<WeatherForecastResponse> call = service.getWeatherForecast7Days(API_KEY, cityName, 7);
        call.enqueue(new Callback<WeatherForecastResponse>() {
            @Override
            public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                if (response.isSuccessful()) {
                    WeatherForecastResponse weatherResponse = response.body();

                    List<WeatherForecastResponse.ForecastDay> forecastDays = weatherResponse.getForecast().getForecastDays();

                    forecastAdapter7Days.updateForecastData(forecastDays);
                } else {
                    Log.e("WeatherAPI", "Failed to get weather forecast for 7 days");
                }
            }

            @Override
            public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {
                Log.e("WeatherAPI", "Error fetching weather forecast for 7 days", t);
            }
        });
    }

    private void fetchWeatherForecast14Days(String cityName) {
        Call<WeatherForecastResponse> call = service.getWeatherForecast14Days(API_KEY, cityName, 14);
        call.enqueue(new Callback<WeatherForecastResponse>() {
            @Override
            public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                if (response.isSuccessful()) {
                    WeatherForecastResponse weatherResponse = response.body();

                    List<WeatherForecastResponse.ForecastDay> forecastDays = weatherResponse.getForecast().getForecastDays();

                    forecastAdapter14Days.updateForecastData(forecastDays);
                } else {
                    Log.e("WeatherAPI", "Failed to get weather forecast for 14 days");
                }
            }

            @Override
            public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {
                Log.e("WeatherAPI", "Error fetching weather forecast for 14 days", t);
            }
        });
    }


    private void openWeatherMap() {
        String url = "https://yandex.ru/pogoda/maps/nowcast";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
    private void openWeatherNews() {
        String url = "https://meteoinfo.ru/novosti";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void updateUI(WeatherResponse weatherResponse,SharedPreferences sharedPreferences) {
        CurrentWeather currentWeather = weatherResponse.getCurrentWeather();
        String temperatureUnit = sharedPreferences.getString("temperature_unit", "Celsius");
        String windSpeedUnit = sharedPreferences.getString("wind_speed_unit", "kmh");

        double temperatureValue;
        String temperatureStringFormat;
        if (temperatureUnit.equals("Celsius")) {
            temperatureValue = currentWeather.getTemperatureCelsius();
            temperatureStringFormat = getString(R.string.temperature);
        } else {
            temperatureValue = currentWeather.getTemperatureFahrenheit();
            temperatureStringFormat = getString(R.string.temperature_fahrenheit);
        }
        double windSpeedValue;
        String windSpeedStringFormat;
        if (windSpeedUnit.equals("kmh")) {
            windSpeedValue = currentWeather.getWindSpeedKph();
            windSpeedStringFormat = getString(R.string.wind_speed_kmh);
        } else {
            windSpeedValue = currentWeather.getWindSpeedMph();
            windSpeedStringFormat = getString(R.string.wind_speed_mph);
        }

        windSpeedTextView.setText(String.format(Locale.getDefault(), windSpeedStringFormat, windSpeedValue));
        temperatureTextView.setText(String.format(Locale.getDefault(), temperatureStringFormat, temperatureValue));

        weatherConditionTextView.setText(getString(R.string.weather_condition, currentWeather.getWeatherCondition().getConditionText()));
        humidityTextView.setText(getString(R.string.humidity, currentWeather.getHumidity()));



        Log.d("WeatherIcon", "Icon URL: " + currentWeather.getWeatherCondition().getIconUrl());

        Picasso.get().load("https:" + currentWeather.getWeatherCondition().getIconUrl()).into(weatherIconImageView);

    }
}
