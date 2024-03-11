package com.example.practicpogoda;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
public class Preferences extends AppCompatActivity {
    private AppSettings appSettings;
    private CheckBox notificationSwitch;
    private RadioButton celsiusRadioButton;
    private RadioButton fahrenheitRadioButton;
    private RadioButton msRadioButton;
    private RadioButton kmhRadioButton;
    private Button saveSettingsButton;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        appSettings = new AppSettings(this);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        msRadioButton = findViewById(R.id.msRadioButton);
        kmhRadioButton = findViewById(R.id. kmhRadioButton);
        Button developerinf = findViewById(R.id.developerinf);
        developerinf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Preferences.this, Developer.class);
                startActivity(intent);
            }
        });
        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Button supportButton = findViewById(R.id.dev);
        supportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Preferences.this, SupportActivity.class);
                startActivity(intent);
            }
        });
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String windSpeedUnit = sharedPreferences.getString("wind_speed_unit", "kmh");
        if (windSpeedUnit.equals("kmh")) {
            kmhRadioButton.setChecked(true);
        } else {
            msRadioButton.setChecked(true);
        }
        celsiusRadioButton = findViewById(R.id.celsiusRadioButton);
        fahrenheitRadioButton = findViewById(R.id.fahrenheitRadioButton);
        RadioGroup windSpeedUnitRadioGroup = findViewById(R.id.windSpeedUnitRadioGroup);
        windSpeedUnitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (checkedId == R.id.kmhRadioButton) {
                editor.putString("wind_speed_unit", "kmh");
            } else if (checkedId == R.id.msRadioButton) {
                editor.putString("wind_speed_unit", "ms");
            }
            editor.apply();
        });
        RadioGroup temperatureUnitRadioGroup = findViewById(R.id.temperatureUnitRadioGroup);
        temperatureUnitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (checkedId == R.id.celsiusRadioButton) {
                editor.putString("temperature_unit", "Celsius");
            } else if (checkedId == R.id.fahrenheitRadioButton) {
                editor.putString("temperature_unit", "Fahrenheit");
            }
            editor.apply();
        });
        String temperatureUnit = sharedPreferences.getString("temperature_unit", "Celsius");
        if (temperatureUnit.equals("Celsius")) {
            celsiusRadioButton.setChecked(true);
        } else {
            fahrenheitRadioButton.setChecked(true);
        }
        notificationSwitch.setChecked(appSettings.isNotificationEnabled());

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            appSettings.setNotificationEnabled(isChecked);
        });
        saveSettingsButton.setOnClickListener(v -> saveSettings());
    }
    private void saveSettings() {
        Toast.makeText(this, "Settings have been saved", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    }