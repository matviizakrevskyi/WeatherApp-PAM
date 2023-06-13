package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SavedActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private LinearLayout buttonContainer;
    private TextView pageNumberTextView;
    private int currentPage = 1;
    private int citiesPerPage = 10;
    private List<String> allCities;

    private View normalView;
    private View newView;

    //For newView (temp data)
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
        setContentView(R.layout.activity_saved);

        databaseHelper = new DatabaseHelper(this);
        buttonContainer = findViewById(R.id.buttonContainer);
        pageNumberTextView = findViewById(R.id.pageNumberTextView);

        normalView = findViewById(R.id.normalView);
        newView = findViewById(R.id.newView);
        showNormalView();

        loadCities();

        mainImageView = findViewById(R.id.mainImage);
        cityNameView = findViewById(R.id.cityName);
        mainTempView = findViewById(R.id.tempNow);
        descriptionView = findViewById(R.id.description);
        minMaxView = findViewById(R.id.minMaxTemp);
        windSpeedView = findViewById(R.id.windSpeed);
        humidityView = findViewById(R.id.humidity);
    }

    private void loadCities() {
        allCities = databaseHelper.getAllCities();

        int totalCities = allCities.size();
        int totalPages = (int) Math.ceil((double) totalCities / citiesPerPage);

        if (currentPage < 1) {
            currentPage = 1;
        } else if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int start = (currentPage - 1) * citiesPerPage;
        int end = Math.min(start + citiesPerPage, totalCities);

        buttonContainer.removeAllViews();

        for (int i = start; i < end; i++) {
            String cityName = allCities.get(i);
            addButton(cityName);
        }

        updatePageNumber();
    }

    private void addButton(String cityName) {
        Button cityButton = new Button(this);
        cityButton.setText(cityName);
        cityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewView(); // Show the newView
                FetchWeatherTask task = new FetchWeatherTask();
                task.execute(cityName);
            }
        });

        buttonContainer.addView(cityButton);
    }


    //Navigation
    public void backToMainActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onPreviousClick(View v) {
        currentPage--;
        loadCities();
    }

    public void onNextClick(View v) {
        currentPage++;
        loadCities();
    }

    private void updatePageNumber() {
        int totalPages = (int) Math.ceil((double) allCities.size() / citiesPerPage);
        pageNumberTextView.setText("Page " + currentPage + " / " + totalPages);
    }

    public void backToMainView(View view) {
        showNormalView();
    }

    private void showNormalView() {
        normalView.setVisibility(View.VISIBLE);
        newView.setVisibility(View.GONE);
    }

    private void showNewView() {
        normalView.setVisibility(View.GONE);
        newView.setVisibility(View.VISIBLE);
    }


    //Temp info
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

                cityNameView.setText(weatherData.get("cityName"));
                mainTempView.setText(weatherData.get("temperature") + "°C");
                descriptionView.setText(weatherData.get("description"));
                minMaxView.setText("Max: " + weatherData.get("maxTemperature") + "°C   Min: " + weatherData.get("minTemperature") + "°C");
                windSpeedView.setText("Wind Speed: " + weatherData.get("windSpeed") + " m/s");
                humidityView.setText("Humidity: " + weatherData.get("humidity") + "%");
            } else {

            }
        }
    }

    public void deleteCity(View v) {
        TextView cityNameTextView = findViewById(R.id.cityName);
        String cityName = cityNameTextView.getText().toString().trim();

        if (!cityName.isEmpty()) {
            databaseHelper.deleteCity(cityName);
            Toast.makeText(this, "City deleted successfully", Toast.LENGTH_SHORT).show();
            showNormalView();
        } else {
            Toast.makeText(this, "Something goes wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
