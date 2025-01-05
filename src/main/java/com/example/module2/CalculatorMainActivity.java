package com.example.module2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CalculatorMainActivity extends AppCompatActivity {

    private AppDatabase appDatabase; // AppDatabase to access all emission records
    private SharedPreferences sharedPreferences; // SharedPreferences to store the emission data
    private TextView totalEmissionTextView; // TextView to display the total emission for today
    private float dailyEmissionTotal = 0; // Variable to store today's total emissions


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Edge to Edge UI support
        setContentView(R.layout.activity_main);  // Set content view only once

        // Initialize SharedPreferences for storing emission data
        sharedPreferences = getSharedPreferences("EmissionData", MODE_PRIVATE);
        totalEmissionTextView = findViewById(R.id.textView14);

        // Handling window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = new Intent(this, DailyGoalsActivity.class);
        startActivity(intent);

        // Initialize the AppDatabase (which holds all emission records)
        appDatabase = AppDatabase.getDatabase(this);  // Use 'this' instead of 'requireContext()'

        // Get the BarChart view
        BarChart barChart = findViewById(R.id.carbon_calculator_barchart); // Correcting the view ID

        // Fetch total emissions from all the emission records
        Executors.newSingleThreadExecutor().execute(() -> {
            // Get all records for each emission type
            List<FoodEmissionRecord> foodRecords = appDatabase.foodEmissionRecordDao().getAllRecords();
            List<HomeEnergyEmissionRecord> homeEnergyRecords = appDatabase.homeEnergyEmissionRecordDao().getAllRecords();
            List<TransportationEmissionRecord> transportationRecords = appDatabase.transportationEmissionRecordDao().getAllRecords();
            List<WasteEmissionRecord> wasteRecords = appDatabase.wasteEmissionRecordDao().getAllRecords();
            List<WaterEmissionRecord> waterRecords = appDatabase.waterEmissionRecordDao().getAllRecords();

            // Calculate the total emissions for each day
            ArrayList<Float> totalEmissions = ChartHelper.calculateTotalEmissions(
                    foodRecords, homeEnergyRecords, transportationRecords, wasteRecords, waterRecords
            );

            // Update the chart with the total emissions data
            runOnUiThread(() -> {
                String[] weeklyLabels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                ChartHelper.setUpChart(barChart, totalEmissions, weeklyLabels);
//                ChartHelper.setUpChart(barChart, totalEmissions);
            });
        });

        // Get the PieChart view
        PieChart pieChart = findViewById(R.id.carbon_calculator_piechart);

        // Fetch total emissions by category
        Executors.newSingleThreadExecutor().execute(() -> {
            // Get all records for each emission type
            List<FoodEmissionRecord> foodRecords = appDatabase.foodEmissionRecordDao().getAllRecords();
            List<HomeEnergyEmissionRecord> homeEnergyRecords = appDatabase.homeEnergyEmissionRecordDao().getAllRecords();
            List<TransportationEmissionRecord> transportationRecords = appDatabase.transportationEmissionRecordDao().getAllRecords();
            List<WasteEmissionRecord> wasteRecords = appDatabase.wasteEmissionRecordDao().getAllRecords();
            List<WaterEmissionRecord> waterRecords = appDatabase.waterEmissionRecordDao().getAllRecords();

            // Calculate total emissions by category
            ArrayList<Float> totalEmissionsByCategory = ChartHelper.calculateTotalEmissionsByCategory(
                    foodRecords, homeEnergyRecords, transportationRecords, wasteRecords, waterRecords
            );

            // Update the PieChart with the total emissions data
            runOnUiThread(() -> {
                ChartHelper.setUpPieChart(pieChart, totalEmissionsByCategory);
            });
        });

        // Check if a new day has started, and reset emissions if necessary
        checkIfNewDayAndResetEmissions();

        // Set the emission total for today
        displayDailyEmissions();

        //<-------------------FOOD DIET FRAGMENT--------------------->
        ImageButton navigateButton = findViewById(R.id.food_diet_button);
        navigateButton.setOnClickListener(v -> {
            Fragment fragment = new FoodDietCalculator();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main, new FoodDietCalculator());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        //<-------------------HOME ENERGY FRAGMENT--------------------->
        ImageButton navigateButton1 = findViewById(R.id.home_energy_button);
        navigateButton1.setOnClickListener(v -> {
            Log.d("MainActivity", "HomeEnergyCalculator button clicked!");
            Fragment fragment = new HomeEnergyCalculator();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main, new HomeEnergyCalculator());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        //<-------------------TRANSPORTATION FRAGMENT--------------------->
        ImageButton navigateButton2 = findViewById(R.id.transportation_button);
        navigateButton2.setOnClickListener(v -> {
            Fragment fragment = new TrannsportationCal();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main, new TrannsportationCal());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        //<-------------------WASTE FRAGMENT--------------------->
        ImageButton navigateButton3 = findViewById(R.id.waste_button);
        navigateButton3.setOnClickListener(v -> {
            Fragment fragment = new WasteCalculator();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main, new WasteCalculator());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        //<-------------------WATER FRAGMENT--------------------->
        ImageButton navigateButton4 = findViewById(R.id.water_button);
        navigateButton4.setOnClickListener(v -> {
            Fragment fragment = new WaterCalculator();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main, new WaterCalculator());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


    }

    // Method to check if a new day has started
    private void checkIfNewDayAndResetEmissions() {
        // Get the current date (day, month, year)
        int currentDay = (int) (System.currentTimeMillis() / (1000 * 60 * 60 * 24)) % 365; // Use day of year or timestamp
        int lastSavedDay = sharedPreferences.getInt("lastSavedDay", -1);

        // If the day has changed, reset emissions for today
        if (currentDay != lastSavedDay) {
            resetEmissionsForNewDay();
        }
    }

    // Method to reset emissions for a new day
    private void resetEmissionsForNewDay() {
        // Reset emissions and store the new day
        dailyEmissionTotal = 0;
        sharedPreferences.edit().putInt("lastSavedDay", (int) (System.currentTimeMillis() / (1000 * 60 * 60 * 24)) % 365).apply();
        displayDailyEmissions();
    }

    // Method to display the daily emissions total in the TextView
    private void displayDailyEmissions() {
        // Retrieve stored emissions for today
        dailyEmissionTotal = sharedPreferences.getFloat("dailyEmissionTotal", 0);
        totalEmissionTextView.setText(String.format("%.2f", dailyEmissionTotal));
    }

    // Method to update the total emissions for today (this will be called from fragments)
    public void updateEmissionForToday(float emission) {
        // Add the emission input to today's total
        dailyEmissionTotal += emission;

        // Store the updated total
        sharedPreferences.edit().putFloat("dailyEmissionTotal", dailyEmissionTotal).apply();

        // Update the TextView
        displayDailyEmissions();
    }
}
