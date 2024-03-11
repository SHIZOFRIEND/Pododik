package com.example.practicpogoda;
import android.content.Context;
import android.content.SharedPreferences;
public class AppSettings {
    private static final String PREFS_NAME = "AppSettings";
    private static final String NOTIFICATION_ENABLED_KEY = "NotificationEnabled";
    private SharedPreferences sharedPreferences;
    public AppSettings(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public boolean isNotificationEnabled() {
        return sharedPreferences.getBoolean(NOTIFICATION_ENABLED_KEY, false);
    }
    public void setNotificationEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(NOTIFICATION_ENABLED_KEY, enabled).apply();
    }
}

