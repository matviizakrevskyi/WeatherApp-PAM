package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class SavedActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private LinearLayout buttonContainer;
    private TextView pageNumberTextView;
    private int currentPage = 1;
    private int citiesPerPage = 6;
    private List<String> allCities;

    private View normalView;
    private View newView;

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
                // Handle button click event
                // Implement your logic here for when a city button is clicked
            }
        });

        buttonContainer.addView(cityButton);
    }

    public void onPreviousClick(View view) {
        currentPage--;
        loadCities();
    }

    public void onNextClick(View view) {
        currentPage++;
        loadCities();
    }

    private void updatePageNumber() {
        int totalPages = (int) Math.ceil((double) allCities.size() / citiesPerPage);
        pageNumberTextView.setText("Page " + currentPage + " / " + totalPages);
    }

    public void backToMainActivity(View view) {
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
}
