package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText inputCityName;
    private TextView errorStatusView;
    private ImageView mainImageView;
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

        databaseHelper = new DatabaseHelper(this);

        inputCityName = findViewById(R.id.inputCityName);

        errorStatusView = findViewById(R.id.textView8);
        mainImageView = findViewById(R.id.mainImage);
        cityNameView = findViewById(R.id.cityName);
        mainTempView = findViewById(R.id.tempNow);
        descriptionView = findViewById(R.id.description);
        minMaxView = findViewById(R.id.minMaxTemp);
        windSpeedView = findViewById(R.id.windSpeed);
        humidityView = findViewById(R.id.humidity);
    }



    public void searchButtonClick(View v) {
        String cityName = inputCityName.getText().toString();
        FetchWeatherTask task = new FetchWeatherTask();
        task.execute(cityName);
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

                if (weatherData.get("main").equals("Clear")){ mainImageView.setImageResource(R.drawable.clear);
                } else if (weatherData.get("main").equals("Clouds")){ mainImageView.setImageResource(R.drawable.clouds);
                } else if (weatherData.get("main").equals("Rain")){ mainImageView.setImageResource(R.drawable.rain);
                } else if (weatherData.get("main").equals("Snow")){ mainImageView.setImageResource(R.drawable.snow);
                } else if (weatherData.get("main").equals("Thunderstorm")){ mainImageView.setImageResource(R.drawable.thunderstorm);}

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

    public void addCity(View v) {
        TextView cityNameTextView = findViewById(R.id.cityName);
        String cityName = cityNameTextView.getText().toString().trim();

        System.out.println(cityName);

        if (!cityName.isEmpty() && !cityNameTextView.getText().equals("City Not Found")) {
            long id = databaseHelper.addCity(cityName);
            if (id != -1) {
                Toast.makeText(SearchActivity.this, "City added successfully", Toast.LENGTH_SHORT).show();
                System.out.println("City added successfully");
                errorStatusView.setText("City added successfully");
            } else if (id == -2) {
                Toast.makeText(SearchActivity.this, "City is already added", Toast.LENGTH_SHORT).show();
                System.out.println("City is already added");
                errorStatusView.setText("City is already added");
            } else {
                Toast.makeText(SearchActivity.this, "Failed to add city", Toast.LENGTH_SHORT).show();
                System.out.println("Failed to add city");
                errorStatusView.setText("Failed to add city");
            }
        } else {
            Toast.makeText(SearchActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            System.out.println("Please enter a city name");
            errorStatusView.setText("Please enter a city name");
        }

    }

    public void deleteCity(View v) {
        TextView cityNameTextView = findViewById(R.id.cityName);
        String cityName = cityNameTextView.getText().toString().trim();

        if (!cityName.isEmpty()) {
            databaseHelper.deleteCity(cityName);
            Toast.makeText(SearchActivity.this, "City deleted successfully", Toast.LENGTH_SHORT).show();
            errorStatusView.setText("City deleted successfully");
        } else {
            Toast.makeText(SearchActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            errorStatusView.setText("Please enter a city name");
        }
    }
}