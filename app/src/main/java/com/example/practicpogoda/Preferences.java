package com.example.practicpogoda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Preferences extends AppCompatActivity {
    private AppSettings appSettings;
    private CheckBox notificationSwitch;
    private Button saveSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        appSettings = new AppSettings(this);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);

        notificationSwitch.setChecked(appSettings.isNotificationEnabled());

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            appSettings.setNotificationEnabled(isChecked);
            // Здесь вы можете выполнить дополнительные действия в зависимости от состояния чекбокса
        });

        saveSettingsButton.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        // Здесь вы можете выполнить дополнительные действия при сохранении настроек
        Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show();
    }
}
