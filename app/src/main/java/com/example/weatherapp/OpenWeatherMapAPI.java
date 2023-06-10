package com.example.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class OpenWeatherMapAPI {
    private static final String API_KEY = "3edbcf6293a9ff2c367754b19c19dfad";

    public static Map<String, String> getWeatherData(String city) throws IOException, JSONException {
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");



            System.out.println(apiUrl);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                return parseWeatherData(jsonResponse);
            } else {
                throw new IOException("Failed to fetch weather data. Response code: " + responseCode);
            }
        } catch (IOException | org.json.JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, String> parseWeatherData(JSONObject jsonResponse) {
        try {
            Map<String, String> weatherData = new HashMap<>();

            // Extract required information from the JSON response
            JSONObject main = jsonResponse.getJSONObject("main");
            JSONArray weatherArray = jsonResponse.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);

            double temperature = main.getDouble("temp") - 273.15; // Convert temperature from Kelvin to Celsius
            double minTemperature = main.getDouble("temp_min") - 273.15;
            double maxTemperature = main.getDouble("temp_max") - 273.15;
            String description = weather.getString("description");
            String mainWeather = weather.getString("main");
            int humidity = main.getInt("humidity");
            String cityName = jsonResponse.getString("name");
            double windSpeed = jsonResponse.getJSONObject("wind").getDouble("speed");

            // Store the extracted data in the weatherData map
            weatherData.put("temperature", String.format("%.2f", temperature));
            weatherData.put("minTemperature", String.format("%.2f", minTemperature));
            weatherData.put("maxTemperature", String.format("%.2f", maxTemperature));
            weatherData.put("description", description);
            weatherData.put("main", mainWeather);
            weatherData.put("humidity", String.valueOf(humidity));
            weatherData.put("cityName", cityName);
            weatherData.put("windSpeed", String.valueOf(windSpeed));

            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if there is an error parsing the JSON response
        }
    }
}
