package com.example.practicpogoda;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.os.Handler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherWidgetProvider extends AppWidgetProvider {
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";
    private static final String API_KEY = "e238b3fbb5734f208e7151055240302";
    private static final int UPDATE_INTERVAL = 10 * 1000;
    private Handler handler = new Handler();
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals("com.example.practicpogoda.APP_CLOSED")) {
            // Приложение закрыто, отключаем и снова включаем виджет
            disableAndEnableWidget(context);
        }
        if (intent.getAction().equals(MainActivity.ACTION_UPDATE_WIDGET)) {
            String cityName = intent.getStringExtra("cityName");
            if (cityName != null) {
                updateWidgetCity(context, cityName); // Обновляем виджет с новым городом
            }
        }
        if (intent.getAction().equals("com.example.practicpogoda.APP_CLOSED")) {
            // Приложение закрыто, принудительно обновляем виджет
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
        // Обновляем виджет, если получено широковещательное сообщение о запуске приложения
        if (intent.getAction().equals("com.example.practicpogoda.APP_STARTED")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
    public static void updateWidgetCity(Context context, String cityName) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Обновляем текстовое поле с названием города
        views.setTextViewText(R.id.forecastCityTextView, cityName);

        // Обновляем виджет
        ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);
        appWidgetManager.updateAppWidget(thisWidget, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        fetchWeatherData(context);
        setAlarm(context);
        this.context = context;

        Intent intent = new Intent(context, WeatherWidgetProvider.class);
        intent.setAction("com.example.practicpogoda.ACTION_WIDGET_CLICKED");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 10 * 1000, pendingIntent); // Запуск обновления каждые 10 секунд

        // Устанавливаем обработчик нажатия на виджет
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setOnClickPendingIntent(R.id.forecastTemperatureTextView, pendingIntent);

        views.setOnClickPendingIntent(R.id.forecastCityTextView, pendingIntent);
        views.setOnClickPendingIntent(R.id.forecastDateTextView, pendingIntent);
        views.setOnClickPendingIntent(R.id.forecastWeatherIconImageView, pendingIntent);


        // Обновляем виджет
        ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);
        appWidgetManager.updateAppWidget(thisWidget, views);
        handler.postDelayed(updateWeatherRunnable, 10 * 1000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchWeatherData(context);
                handler.postDelayed(this, 10 * 1000); // Запускаем обновление погоды через 10 секунд
            }
        }, 10 * 1000);
    }

    private Runnable updateWeatherRunnable = new Runnable() {
        @Override
        public void run() {
            fetchWeatherData(context); // Вызываем метод для обновления погоды
            handler.postDelayed(this, 10 * 1000); // Планируем повторное выполнение через 10 секунд
        }
    };

    private void fetchWeatherData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String cityName = preferences.getString("cityName", "Novosibirsk"); // Получаем выбранный город из SharedPreferences

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService service = retrofit.create(WeatherApiService.class);

        Call<WeatherResponse> call = service.getWeather(API_KEY, cityName);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    CurrentWeather currentWeather = weatherResponse.getCurrentWeather();
                    if (currentWeather != null) {
                        currentWeather.setCityName(cityName); // Устанавливаем значение cityName в объекте CurrentWeather
                        updateAppWidget(context, weatherResponse);
                    } else {
                        Log.e("WeatherAPI", "Current weather data is null");
                    }
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
    private void disableAndEnableWidget(Context context) {
        // Отключаем виджет
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.updateAppWidget(thisWidget, new RemoteViews(context.getPackageName(), R.layout.widget_layout));

        // Подождите некоторое время перед включением виджета
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Снова включаем виджет
        appWidgetManager.updateAppWidget(thisWidget, new RemoteViews(context.getPackageName(), R.layout.widget_layout));
    }
    private void updateAppWidget(Context context, WeatherResponse weatherResponse) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        CurrentWeather currentWeather = weatherResponse.getCurrentWeather();
        if (currentWeather != null) {
            // Обновляем текстовое поле с температурой
            double temperature = currentWeather.getTemperatureCelsius();
            views.setTextViewText(R.id.forecastTemperatureTextView, "Temperature: " + temperature + "°C");

            // Обновляем текстовое поле с названием города
            String cityName = currentWeather.getCityName();
            views.setTextViewText(R.id.forecastCityTextView, "City: " + cityName); // Обновляем текстовое поле с названием города

            // Получаем текущую дату и обновляем текстовое поле с датой
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());
            views.setTextViewText(R.id.forecastDateTextView, "Date: " + currentDate);

            // Обновляем виджет
            ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);
            appWidgetManager.updateAppWidget(thisWidget, views);
        } else {
            Log.e("WeatherWidget", "Current weather data is null");
        }
    }




    private void setAlarm(Context context) {
        Intent intent = new Intent(context, WeatherWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), UPDATE_INTERVAL, pendingIntent);
        }
    }
}
