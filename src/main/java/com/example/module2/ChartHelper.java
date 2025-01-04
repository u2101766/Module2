package com.example.module2;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

public class ChartHelper {

    // Method to calculate total emissions from different records
    public static ArrayList<Float> calculateTotalEmissions(
            List<FoodEmissionRecord> foodRecords,
            List<HomeEnergyEmissionRecord> homeEnergyRecords,
            List<TransportationEmissionRecord> transportationRecords,
            List<WasteEmissionRecord> wasteRecords,
            List<WaterEmissionRecord> waterRecords
    ) {
        ArrayList<Float> totalEmissions = new ArrayList<>();

        // Assuming data is for 5 days, change if your data is for a different period
        for (int i = 0; i < 5; i++) {
            float totalEmissionForDay = 0;

            // Calculate the total emissions for each day across all emission types
            for (FoodEmissionRecord record : foodRecords) {
                if (record.getDay() == i) {
                    totalEmissionForDay += record.getEmissionAmount();
                }
            }

            for (HomeEnergyEmissionRecord record : homeEnergyRecords) {
                if (record.getDay() == i) {
                    totalEmissionForDay += record.getEmissionAmount();
                }
            }

            for (TransportationEmissionRecord record : transportationRecords) {
                if (record.getDay() == i) {
                    totalEmissionForDay += record.getEmissionAmount();
                }
            }

            for (WasteEmissionRecord record : wasteRecords) {
                if (record.getDay() == i) {
                    totalEmissionForDay += record.getEmissionAmount();
                }
            }

            for (WaterEmissionRecord record : waterRecords) {
                if (record.getDay() == i) {
                    totalEmissionForDay += record.getEmissionAmount();
                }
            }

            // Add the total emissions for this day to the list
            totalEmissions.add(totalEmissionForDay);
        }

        return totalEmissions;
    }

    // Method to calculate total emissions for each category
    public static ArrayList<Float> calculateTotalEmissionsByCategory(
            List<FoodEmissionRecord> foodRecords,
            List<HomeEnergyEmissionRecord> homeEnergyRecords,
            List<TransportationEmissionRecord> transportationRecords,
            List<WasteEmissionRecord> wasteRecords,
            List<WaterEmissionRecord> waterRecords
    ) {
        float foodEmission = 0f, homeEnergyEmission = 0f, transportationEmission = 0f;
        float wasteEmission = 0f, waterEmission = 0f;

        // Loop through records and accumulate emissions for each category
        for (FoodEmissionRecord record : foodRecords) {
            foodEmission += record.getEmissionAmount();
        }
        for (HomeEnergyEmissionRecord record : homeEnergyRecords) {
            homeEnergyEmission += record.getEmissionAmount();
        }
        for (TransportationEmissionRecord record : transportationRecords) {
            transportationEmission += record.getEmissionAmount();
        }
        for (WasteEmissionRecord record : wasteRecords) {
            wasteEmission += record.getEmissionAmount();
        }
        for (WaterEmissionRecord record : waterRecords) {
            waterEmission += record.getEmissionAmount();
        }

        // Add total emissions by category to the list
        ArrayList<Float> totalEmissionsByCategory = new ArrayList<>();
        totalEmissionsByCategory.add(foodEmission);
        totalEmissionsByCategory.add(homeEnergyEmission);
        totalEmissionsByCategory.add(transportationEmission);
        totalEmissionsByCategory.add(wasteEmission);
        totalEmissionsByCategory.add(waterEmission);

        return totalEmissionsByCategory;
    }

    // Method to set up the BarChart with total emission data
    public static void setUpChart(BarChart barChart, ArrayList<Float> totalEmissions, String[] labels) {
        // Check if labels and emissions sizes match
        if (labels.length < totalEmissions.size()) {
            throw new IllegalArgumentException("Labels array size must match or exceed the size of total emissions data.");
        }

        // Create a list of BarEntries for the chart
        ArrayList<BarEntry> entries = new ArrayList<>();

        // Loop through total emissions and create an entry for each data point
        for (int i = 0; i < totalEmissions.size(); i++) {
            entries.add(new BarEntry(i, totalEmissions.get(i)));
        }

        // Create a BarDataSet with the entries
        BarDataSet barDataSet = new BarDataSet(entries, "Total CO2 Emissions");
        barDataSet.setColor(Color.rgb(9, 48, 48)); // Set color for the bars
        barDataSet.setValueTextColor(Color.BLACK); // Set color for the values

        // Create BarData and set it to the BarChart
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();  // Refresh the chart

        // Format the X-axis
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                // Ensure the index is within bounds
                if (index < 0 || index >= labels.length) {
                    return ""; // Return an empty string for out-of-bounds values
                }
                return labels[index];
            }
        });

        // Additional customization (optional)
        barChart.getXAxis().setGranularity(1f); // Ensure each label corresponds to one data point
        barChart.getXAxis().setGranularityEnabled(true);
    }


    // Method to set up the PieChart with total emission data
    public static void setUpPieChart(PieChart pieChart, ArrayList<Float> totalEmissionsByCategory) {
        // Create a list of PieEntries for the chart
        ArrayList<PieEntry> entries = new ArrayList<>();

        // Categories for the PieChart
        String[] categories = {"Food", "Home Energy", "Transportation", "Waste", "Water"};

        for (int i = 0; i < totalEmissionsByCategory.size(); i++) {
            entries.add(new PieEntry(totalEmissionsByCategory.get(i), categories[i]));
        }

        // Create a PieDataSet with the entries
        PieDataSet pieDataSet = new PieDataSet(entries, "Emissions by Category");
        pieDataSet.setColors(Color.rgb(255, 99, 132), Color.rgb(54, 162, 235), Color.rgb(255, 205, 86),
                Color.rgb(75, 192, 192), Color.rgb(153, 102, 255)); // Set colors for each category
        pieDataSet.setValueTextColor(Color.BLACK); // Set color for the values

        // Create PieData and set it to the PieChart
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();  // Refresh the chart

        // Disable the drawing of the entry labels (white text on the slices)
        pieChart.setDrawEntryLabels(false);
    }

    // Method to calculate total weekly emissions for one category
    public static ArrayList<Float> calculateWeeklyEmissionsForCategory(List<FoodEmissionRecord> foodRecords) {
        ArrayList<Float> weeklyEmissions = new ArrayList<>(7); // 7 days of the week

        // Initialize all values to 0 for each day of the week
        for (int i = 0; i < 7; i++) {
            weeklyEmissions.add(0f);
        }

        // Loop through the records and accumulate emissions for each day of the week
        for (FoodEmissionRecord record : foodRecords) {
            int dayOfWeek = record.getDay(); // Assuming `getDay()` returns an integer 0-6 for Sun-Sat
            if (dayOfWeek >= 0 && dayOfWeek < 7) {
                weeklyEmissions.set(dayOfWeek, (float) (weeklyEmissions.get(dayOfWeek) + record.getEmissionAmount()));
            }
        }

        return weeklyEmissions;
    }

    // Method to calculate total monthly emissions for one category
    public static ArrayList<Float> calculateMonthlyEmissionsForCategory(List<FoodEmissionRecord> foodRecords) {
        ArrayList<Float> monthlyEmissions = new ArrayList<>(12); // 12 months in a year

        // Initialize all values to 0 for each month
        for (int i = 0; i < 12; i++) {
            monthlyEmissions.add(0f);
        }

        // Loop through the records and accumulate emissions for each month
        for (FoodEmissionRecord record : foodRecords) {
            int month = record.getMonth(); // Assuming `getMonth()` returns an integer 0-11 for Jan-Dec
            if (month >= 0 && month < 12) {
                monthlyEmissions.set(month, (float) (monthlyEmissions.get(month) + record.getEmissionAmount()));
            }
        }

        return monthlyEmissions;
    }

    // Calculate weekly emissions for Home Energy
    public static ArrayList<Float> calculateWeeklyEmissionsForHomeEnergy(List<HomeEnergyEmissionRecord> homeEnergyRecords) {
        ArrayList<Float> weeklyEmissions = new ArrayList<>(7);

        for (int i = 0; i < 7; i++) {
            weeklyEmissions.add(0f);
        }

        for (HomeEnergyEmissionRecord record : homeEnergyRecords) {
            int dayOfWeek = record.getDay();
            if (dayOfWeek >= 0 && dayOfWeek < 7) {
                weeklyEmissions.set(dayOfWeek, (float) (weeklyEmissions.get(dayOfWeek) + record.getEmissionAmount()));
            }
        }

        return weeklyEmissions;
    }

    // Calculate monthly emissions for Home Energy
    public static ArrayList<Float> calculateMonthlyEmissionsForHomeEnergy(List<HomeEnergyEmissionRecord> homeEnergyRecords) {
        ArrayList<Float> monthlyEmissions = new ArrayList<>(12);

        for (int i = 0; i < 12; i++) {
            monthlyEmissions.add(0f);
        }

        for (HomeEnergyEmissionRecord record : homeEnergyRecords) {
            int month = record.getMonth();
            if (month >= 0 && month < 12) {
                monthlyEmissions.set(month, (float) (monthlyEmissions.get(month) + record.getEmissionAmount()));
            }
        }

        return monthlyEmissions;
    }

    // Calculate weekly emissions for Transportation
    public static ArrayList<Float> calculateWeeklyEmissionsForTransportation(List<TransportationEmissionRecord> transportationRecords) {
        ArrayList<Float> weeklyEmissions = new ArrayList<>(7);

        for (int i = 0; i < 7; i++) {
            weeklyEmissions.add(0f);
        }

        for (TransportationEmissionRecord record : transportationRecords) {
            int dayOfWeek = record.getDay();
            if (dayOfWeek >= 0 && dayOfWeek < 7) {
                weeklyEmissions.set(dayOfWeek, (float) (weeklyEmissions.get(dayOfWeek) + record.getEmissionAmount()));
            }
        }

        return weeklyEmissions;
    }

    // Calculate monthly emissions for Transportation
    public static ArrayList<Float> calculateMonthlyEmissionsForTransportation(List<TransportationEmissionRecord> transportationRecords) {
        ArrayList<Float> monthlyEmissions = new ArrayList<>(12);

        for (int i = 0; i < 12; i++) {
            monthlyEmissions.add(0f);
        }

        for (TransportationEmissionRecord record : transportationRecords) {
            int month = record.getMonth();
            if (month >= 0 && month < 12) {
                monthlyEmissions.set(month, (float) (monthlyEmissions.get(month) + record.getEmissionAmount()));
            }
        }

        return monthlyEmissions;
    }

    // Calculate weekly emissions for Waste
    public static ArrayList<Float> calculateWeeklyEmissionsForWaste(List<WasteEmissionRecord> wasteRecords) {
        ArrayList<Float> weeklyEmissions = new ArrayList<>(7);

        for (int i = 0; i < 7; i++) {
            weeklyEmissions.add(0f);
        }

        for (WasteEmissionRecord record : wasteRecords) {
            int dayOfWeek = record.getDay();
            if (dayOfWeek >= 0 && dayOfWeek < 7) {
                weeklyEmissions.set(dayOfWeek, (float) (weeklyEmissions.get(dayOfWeek) + record.getEmissionAmount()));
            }
        }

        return weeklyEmissions;
    }

    // Calculate monthly emissions for Waste
    public static ArrayList<Float> calculateMonthlyEmissionsForWaste(List<WasteEmissionRecord> wasteRecords) {
        ArrayList<Float> monthlyEmissions = new ArrayList<>(12);

        for (int i = 0; i < 12; i++) {
            monthlyEmissions.add(0f);
        }

        for (WasteEmissionRecord record : wasteRecords) {
            int month = record.getMonth();
            if (month >= 0 && month < 12) {
                monthlyEmissions.set(month, (float) (monthlyEmissions.get(month) + record.getEmissionAmount()));
            }
        }

        return monthlyEmissions;
    }

    // Calculate weekly emissions for Water
    public static ArrayList<Float> calculateWeeklyEmissionsForWater(List<WaterEmissionRecord> waterRecords) {
        ArrayList<Float> weeklyEmissions = new ArrayList<>(7);

        for (int i = 0; i < 7; i++) {
            weeklyEmissions.add(0f);
        }

        for (WaterEmissionRecord record : waterRecords) {
            int dayOfWeek = record.getDay();
            if (dayOfWeek >= 0 && dayOfWeek < 7) {
                weeklyEmissions.set(dayOfWeek, (float) (weeklyEmissions.get(dayOfWeek) + record.getEmissionAmount()));
            }
        }

        return weeklyEmissions;
    }

    // Calculate monthly emissions for Water
    public static ArrayList<Float> calculateMonthlyEmissionsForWater(List<WaterEmissionRecord> waterRecords) {
        ArrayList<Float> monthlyEmissions = new ArrayList<>(12);

        for (int i = 0; i < 12; i++) {
            monthlyEmissions.add(0f);
        }

        for (WaterEmissionRecord record : waterRecords) {
            int month = record.getMonth();
            if (month >= 0 && month < 12) {
                monthlyEmissions.set(month, (float) (monthlyEmissions.get(month) + record.getEmissionAmount()));
            }
        }

        return monthlyEmissions;
    }
}
