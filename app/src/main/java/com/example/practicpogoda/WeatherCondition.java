package com.example.practicpogoda;
import com.google.gson.annotations.SerializedName;
public class WeatherCondition {
    @SerializedName("text")
    private String conditionText;
    @SerializedName("icon")
    private String iconUrl;
    public String getConditionText() {
        return conditionText;
    }
    public String getIconUrl() {
        return iconUrl;
    }
}
