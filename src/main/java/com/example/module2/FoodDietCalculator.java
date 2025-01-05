package com.example.module2;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import android.app.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.room.Room;

import com.github.mikephil.charting.charts.BarChart;

import java.util.List;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FoodDietCalculator extends Fragment {

    private FoodDietCalculatorViewModel mViewModel;
    private CalculatorMainActivity mainActivity;
    private FoodEmissionRecordDao foodEmissionRecordDao; // Declare as an instance field

    public static FoodDietCalculator newInstance() {
        return new FoodDietCalculator();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Make sure MainActivity implements the interface
        if (context instanceof CalculatorMainActivity) {
            mainActivity = (CalculatorMainActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_diet_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Room database
        //AppDatabase
        AppDatabase appDatabase = Room.databaseBuilder(
                requireContext(),
                AppDatabase.class,
                "emission_database"
        )
                .fallbackToDestructiveMigration() // Clears data if schema changes
                .build();

        //FoodEmissionDatabase
        FoodEmissionDatabase foodEmissionDatabase = Room.databaseBuilder(
                requireContext(),
                FoodEmissionDatabase.class,
                "food_emission_database"
        )
                .fallbackToDestructiveMigration() // Clears data if schema changes
                .build();

        FoodEmissionRecordDao foodOnlyDao = foodEmissionDatabase.foodEmissionRecordDao();

        // Correct way to get the DAO
        foodEmissionRecordDao = appDatabase.foodEmissionRecordDao();

        //        <---------------------SPINNER GRAPH--------------->
        // Fetch records and show the default graph (e.g., weekly)
        Executors.newSingleThreadExecutor().execute(() -> {
            List<FoodEmissionRecord> records = foodEmissionRecordDao.getAllRecords();
            Log.d("FoodDietCalculator", "Records fetched: " + records.size());
            requireActivity().runOnUiThread(() -> showWeeklyGraph(view, records));
        });

        // Initialize the spinner
        Spinner foodSpinner = view.findViewById(R.id.food_graph_spinner);

        // Create an ArrayAdapter using a string array resource and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.food_graph_option, // Define this array in strings.xml
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        foodSpinner.setAdapter(adapter);

//        Spinner foodSpinner = view.findViewById(R.id.food_graph_spinner);
foodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();

        Executors.newSingleThreadExecutor().execute(() -> {
            List<FoodEmissionRecord> records = foodEmissionRecordDao.getAllRecords();
            requireActivity().runOnUiThread(() -> {
                if (selectedItem.equals("Daily")) {
                    showWeeklyGraph(requireView(), records);
                } else if (selectedItem.equals("Monthly")) {
                    showMonthlyGraph(requireView(), records);
                }
            });
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle no selection case if needed
    }
});

// Fetch records and show the default graph (Weekly)
        Executors.newSingleThreadExecutor().execute(() -> {
            List<FoodEmissionRecord> records = foodEmissionRecordDao.getAllRecords();
            requireActivity().runOnUiThread(() -> showWeeklyGraph(view, records));
        });
        //        <---------------------END OF SPINNER GRAPH OPTIONS--------------->

        //        <-------------------DATE PICKER------------------>
        // Initialize the Date Picker Button
        Button datePickerButton = view.findViewById(R.id.date_picker_food);

// Store the selected date
        final String[] selectedDate = {""};

        datePickerButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show the DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        // Use SimpleDateFormat to format the selected date
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        selectedDate[0] = sdf.format(selectedCalendar.getTime());

                        // Update the button text to show the selected date
                        datePickerButton.setText(selectedDate[0]);
                    },
                    year, month, day);

            datePickerDialog.show();
        });

        //        <-------------------END OF DATE PICKER------------------>

        //        <---------------------SPINNER CALCULATOR OPTIONS--------------->

        // Find views from the layout
        Spinner foodOptionSpinner = view.findViewById(R.id.food_options);
        EditText quantityInput = view.findViewById(R.id.food_quantity_input); // Update your EditText ID
        TextView co2ResultText = view.findViewById(R.id.food_output); // Update your TextView ID
        Button addButton = view.findViewById(R.id.add_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Set up the spinner
        ArrayAdapter<CharSequence> foodAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.food_options, // Define this array in strings.xml
                android.R.layout.simple_spinner_item
        );
        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodOptionSpinner.setAdapter(foodAdapter);

        // Food emission factors
        final double[] foodEmissionFactors = {
                6.0, // chicken
                60.0, // beef
                3.5, // fish
                1.5, // shellfish
                4.5, // rice
                2.5, // noodles
                1.2, // bread
                12.0, // coffee
                3.5, // tea
                2.0, // vegetables
                2.5  // fruits
        };

        addButton.setOnClickListener(v -> {
            String quantityStr = quantityInput.getText().toString();
            int selectedFoodIndex = foodOptionSpinner.getSelectedItemPosition();
            String selectedDateValue = selectedDate[0];

            if (quantityStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a quantity.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedFoodIndex < 0 || selectedFoodIndex >= foodEmissionFactors.length) {
                Toast.makeText(requireContext(), "Please select a food type.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedDateValue.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a date.", Toast.LENGTH_SHORT).show();
                return;
            }

            double quantity = Double.parseDouble(quantityStr);
            double emissionFactor = foodEmissionFactors[selectedFoodIndex];
            double co2e = quantity * emissionFactor;

            co2ResultText.setText(String.format("%.2f kg CO₂e", co2e));

            // Submit the food emission data to MainActivity
            submitFoodEmissionData((float) co2e);  // Cast to float if needed

            String selectedFood = foodOptionSpinner.getSelectedItem().toString();

            FoodEmissionRecord record = new FoodEmissionRecord(selectedFood, quantity, co2e, selectedDateValue);

            // Save to both databases
            Executors.newSingleThreadExecutor().execute(() -> {
                if (foodEmissionRecordDao != null) {
                    foodEmissionRecordDao.insert(record);
                    List<FoodEmissionRecord> records = foodEmissionRecordDao.getAllRecords();
                    for (FoodEmissionRecord insertedRecord : records) {
                        Log.d("FoodDietCalculator", "Inserted into emission_database: " + insertedRecord.toString());
                    }
                }

                if (foodOnlyDao != null) {
                    foodOnlyDao.insert(record);
                    List<FoodEmissionRecord> foodRecords = foodOnlyDao.getAllRecords();
                    for (FoodEmissionRecord insertedRecord : foodRecords) {
                        Log.d("FoodDietCalculator", "Inserted into food_emission_database: " + insertedRecord.toString());
                    }
                }


                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Data added to both databases successfully!", Toast.LENGTH_SHORT).show()
                );
            });
        });

        // Cancel button logic
        cancelButton.setOnClickListener(v -> {
            // Clear all input fields
            quantityInput.setText("");
            co2ResultText.setText("");
            foodOptionSpinner.setSelection(0);
            Toast.makeText(requireContext(), "Input cleared.", Toast.LENGTH_SHORT).show();
        });
        //        <---------------------END OF SPINNER CALCULATOR OPTIONS--------------->

//        <-------------------BACK BUTTON------------------>
        // Find the back button in the fragment's layout
        ImageView backButton = view.findViewById(R.id.backbutton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back
                requireActivity().onBackPressed();
            }
        });  //        <-------------------END OF BACK BUTTON------------------>
    }


    // Method to submit the calculated food emission data to MainActivity
    private void submitFoodEmissionData(float emission) {
        if (mainActivity != null) {
            mainActivity.updateEmissionForToday(emission);  // Update emission total in MainActivity
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FoodDietCalculatorViewModel.class);
        // TODO: Use the ViewModel
    }

    private void showWeeklyGraph(View view, List<FoodEmissionRecord> records) {
        Log.d("FoodDietCalculator", "showWeeklyGraph called with " + records.size() + " records.");

        // Assuming you have the chart and data prepared
        BarChart barChart = view.findViewById(R.id.barchart_food); // your BarChart instance
        if (barChart == null) {
            Log.e("FoodDietCalculator", "BarChart view not found. Check layout XML.");
            return;
        }
        if (records.isEmpty()) {
            Toast.makeText(requireContext(), "No weekly data available.", Toast.LENGTH_SHORT).show();
            barChart.clear();
            return;
        }
        // Get weekly emissions for food (replace this with actual data fetching logic)
        ArrayList<Float> weeklyFoodEmissions = ChartHelper.calculateWeeklyEmissionsForCategory(records);

        // Set up the BarChart with the weekly emissions
        String[] weeklyLabels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        ChartHelper.setUpChart(barChart, weeklyFoodEmissions, weeklyLabels);
        Log.d("FoodDietCalculator", "Chart setup complete.");

    }


    private void showMonthlyGraph(View rootView, List<FoodEmissionRecord> records) {
        if (rootView == null) {
            Log.e("FoodDietCalculator", "Root view is null. Cannot find BarChart.");
            return;
        }
        // Find the BarChart instance within the root view
        BarChart barChart = rootView.findViewById(R.id.barchart_food); // Replace with the correct ID from your layout
        if (barChart == null) {
            Log.e("FoodDietCalculator", "BarChart view not found. Check layout XML.");
            return;
        }

        if (records.isEmpty()) {
            Toast.makeText(requireContext(), "No weekly data available.", Toast.LENGTH_SHORT).show();
            barChart.clear();
            return;
        }
        // Get monthly emissions for food
        ArrayList<Float> monthlyFoodEmissions = ChartHelper.calculateMonthlyEmissionsForCategory(records);
        // Set up the BarChart with the monthly emissions
        String[] monthlyLabels = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        ChartHelper.setUpChart(barChart, monthlyFoodEmissions, monthlyLabels);
    }



}