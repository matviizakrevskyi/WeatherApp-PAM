package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private EditText inputCityName;
    private TextView errorStatusView;
    private TextView cityNameView;
    private TextView mainTempView;
    private TextView descriptionView;
    private TextView minMaxView;
    private TextView windSpeedView;
    private TextView humidityView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        inputCityName = findViewById(R.id.inputCityName);

        errorStatusView = findViewById(R.id.textView8);
        cityNameView = findViewById(R.id.cityName);
        mainTempView = findViewById(R.id.tempNow);
        descriptionView = findViewById(R.id.description);
        minMaxView = findViewById(R.id.minMaxTemp);
        windSpeedView = findViewById(R.id.windSpeed);
        humidityView = findViewById(R.id.humidity);
    }



    public void onClick(View v) {
        String cityName = inputCityName.getText().toString();
        FetchWeatherTask task = new FetchWeatherTask();
        task.execute(cityName); // Replace "London" with the desired city
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(String... params) {
            String city = params[0];
            try {
                return OpenWeatherMapAPI.getWeatherData(city);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Map<String, String> weatherData) {
            if (weatherData != null) {
                String weatherInfo = "Temperature: " + weatherData.get("temperature") + "°C\n" +
                        "Min Temperature: " + weatherData.get("minTemperature") + "°C\n" +
                        "Max Temperature: " + weatherData.get("maxTemperature") + "°C\n" +
                        "Description: " + weatherData.get("description") + "\n" +
                        "Main: " + weatherData.get("main") + "\n" +
                        "Humidity: " + weatherData.get("humidity") + "%\n" +
                        "City: " + weatherData.get("cityName") + "\n" +
                        "Wind Speed: " + weatherData.get("windSpeed") + " m/s";

                System.out.println(weatherInfo);
                errorStatusView.setText("");
                cityNameView.setText(weatherData.get("cityName"));
                mainTempView.setText(weatherData.get("temperature") + "°C");
                descriptionView.setText(weatherData.get("description"));
                minMaxView.setText("Max: " + weatherData.get("maxTemperature") + "°C   Min: " + weatherData.get("minTemperature") + "°C");
                windSpeedView.setText("Wind Speed: " + weatherData.get("windSpeed") + " m/s");
                humidityView.setText("Humidity: " + weatherData.get("humidity") + "%");
            } else {
                errorStatusView.setText("Failed to fetch weather data");
            }
        }
    }

    public void backToMainActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}