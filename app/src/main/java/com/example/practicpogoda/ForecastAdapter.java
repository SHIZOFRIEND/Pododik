package com.example.practicpogoda;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<WeatherForecastResponse.ForecastDay> forecastDays;

    public ForecastAdapter(List<WeatherForecastResponse.ForecastDay> forecastDays) {
        this.forecastDays = forecastDays;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast_card, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        WeatherForecastResponse.ForecastDay forecastDay = forecastDays.get(position);
        holder.forecastDateTextView.setText(forecastDay.getDate());
        holder.forecastTemperatureTextView.setText(String.valueOf(forecastDay.getDay().getMaxTempCelsius()));

        // Загружаем и отображаем иконку погоды с помощью Picasso
        Picasso.get().load("https:" + forecastDay.getDay().getWeatherCondition().getIconUrl()).into(holder.forecastWeatherIconImageView);
    }
    public void updateForecastData(List<WeatherForecastResponse.ForecastDay> forecastDays) {
        this.forecastDays = forecastDays;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return forecastDays.size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {

        TextView forecastDateTextView;
        TextView forecastTemperatureTextView;
        ImageView forecastWeatherIconImageView;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            forecastDateTextView = itemView.findViewById(R.id.forecastDateTextView);
            forecastTemperatureTextView = itemView.findViewById(R.id.forecastTemperatureTextView);
            forecastWeatherIconImageView = itemView.findViewById(R.id.forecastWeatherIconImageView);
        }
    }
}
