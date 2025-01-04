package com.example.module2;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

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

import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Calendar;

public class HomeEnergyCalculator extends Fragment {

    private HomeEnergyCalculatorViewModel mViewModel;
    private HomeEnergyEmissionRecordDao homeEnergyEmissionRecordDao; // Declare as an instance field

    public static HomeEnergyCalculator newInstance() {
        return new HomeEnergyCalculator();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_energy_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Room database
        //    private DatabaseReference databaseReference;
        // Declare as an instance field
        AppDatabase appDatabase = Room.databaseBuilder(
                requireContext(),
                AppDatabase.class,
                "emission_database"
        )
                .fallbackToDestructiveMigration() // Clears data if schema changes
                .build();

        //HomeEnergyDatabase
        HomeEnergyDatabase homeEmissionDatabase = Room.databaseBuilder(
                        requireContext(),
                        HomeEnergyDatabase.class,
                        "home_energy_database"
                )
                .fallbackToDestructiveMigration() // Clears data if schema changes
                .build();

        HomeEnergyEmissionRecordDao homeEnergyOnlyDao = homeEmissionDatabase.homeEnergyEmissionRecordDao();

        // Correct way to get the DAO
        homeEnergyEmissionRecordDao = appDatabase.homeEnergyEmissionRecordDao();

        //        <---------------------SPINNER GRAPH--------------->
        // Fetch records and show the default graph (e.g., weekly)
        Executors.newSingleThreadExecutor().execute(() -> {
            List<HomeEnergyEmissionRecord> records = homeEnergyEmissionRecordDao.getAllRecords();
            Log.d("HomeEnergyCalculator", "Records fetched: " + records.size());
            requireActivity().runOnUiThread(() -> showWeeklyGraph(view, records));
        });

        // Initialize the spinner
        Spinner homeEnergySpinner = view.findViewById(R.id.home_energy_spinner);

        // Create an ArrayAdapter using a string array resource and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.home_energy_option, // Define this array in strings.xml
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        homeEnergySpinner.setAdapter(adapter);

//        Spinner foodSpinner = view.findViewById(R.id.food_graph_spinner);
        homeEnergySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                Executors.newSingleThreadExecutor().execute(() -> {
                    List<HomeEnergyEmissionRecord> records = homeEnergyEmissionRecordDao.getAllRecords();
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
            List<HomeEnergyEmissionRecord> records = homeEnergyEmissionRecordDao.getAllRecords();
            requireActivity().runOnUiThread(() -> showWeeklyGraph(view, records));
        });
        //        <---------------------END OF SPINNER GRAPH OPTIONS--------------->


        //        <-------------------DATE PICKER------------------>
        // Initialize the Date Picker Button
        Button datePickerButton = view.findViewById(R.id.date_picker_home_energy);

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

        //        <---------------------CALCULATOR--------------->

        // Find views from the layout
//        Spinner foodOptionSpinner = view.findViewById(R.id.food_options);
        EditText quantityInput = view.findViewById(R.id.home_energy_quantity_input); // Update your EditText ID
        TextView co2ResultText = view.findViewById(R.id.home_energy_output); // Update your TextView ID
        Button addButton = view.findViewById(R.id.add_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Add button logic
        addButton.setOnClickListener(v -> {
            String quantityStr = quantityInput.getText().toString();
//            int selectedFoodIndex = foodOptionSpinner.getSelectedItemPosition();
            String selectedDateValue = selectedDate[0];

            if (quantityStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a quantity.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the selected date (ensure the date is not empty)
            if (selectedDateValue.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a date.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform calculation
            double quantity = Double.parseDouble(quantityStr);
            double emissionFactor = 0.5;
            double co2e = quantity * emissionFactor;

            // Display result in the TextView
            co2ResultText.setText(String.format("%.2f kg CO₂e", co2e));
//
//            // Save to Room database
//            String selectedFood = foodOptionSpinner.getSelectedItem().toString();

            HomeEnergyEmissionRecord record = new HomeEnergyEmissionRecord(quantity, co2e, selectedDateValue);

            // Save to both databases
            Executors.newSingleThreadExecutor().execute(() -> {
                if (homeEnergyEmissionRecordDao != null) {
                    homeEnergyEmissionRecordDao.insert(record);
                    List<HomeEnergyEmissionRecord> records = homeEnergyEmissionRecordDao.getAllRecords();
                    for (HomeEnergyEmissionRecord insertedRecord : records) {
                        Log.d("HomeEnergyCalculator", "Inserted into emission_database: " + insertedRecord.toString());
                    }
                }

                if (homeEnergyOnlyDao != null) {
                    homeEnergyOnlyDao.insert(record);
                    List<HomeEnergyEmissionRecord> homeEnergyRecords = homeEnergyOnlyDao.getAllRecords();
                    for (HomeEnergyEmissionRecord insertedRecord : homeEnergyRecords) {
                        Log.d("HomeEnergyCalculator", "Inserted into home_energy_database: " + insertedRecord.toString());
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
//            foodOptionSpinner.setSelection(0);
            Toast.makeText(requireContext(), "Input cleared.", Toast.LENGTH_SHORT).show();
        });
        //        <---------------------END OF CALCULATOR--------------->



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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeEnergyCalculatorViewModel.class);
        // TODO: Use the ViewModel
    }

    private void showWeeklyGraph(View view, List<HomeEnergyEmissionRecord> records) {
        Log.d("HomeEnergyCalculator", "showWeeklyGraph called with " + records.size() + " records.");

        // Assuming you have the chart and data prepared
        BarChart barChart = view.findViewById(R.id.barchart_home_energy); // your BarChart instance
        if (barChart == null) {
            Log.e("HomeEnergyCalculator", "BarChart view not found. Check layout XML.");
            return;
        }
        if (records.isEmpty()) {
            Toast.makeText(requireContext(), "No weekly data available.", Toast.LENGTH_SHORT).show();
            barChart.clear();
            return;
        }
        // Get weekly emissions for food (replace this with actual data fetching logic)
        ArrayList<Float> weeklyHomeEnergyEmissions = ChartHelper.calculateWeeklyEmissionsForHomeEnergy(records);

        // Set up the BarChart with the weekly emissions
        String[] weeklyLabels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        ChartHelper.setUpChart(barChart, weeklyHomeEnergyEmissions, weeklyLabels);
        Log.d("FoodDietCalculator", "Chart setup complete.");

    }


    private void showMonthlyGraph(View rootView, List<HomeEnergyEmissionRecord> records) {
        if (rootView == null) {
            Log.e("HomeEnergyCalculator", "Root view is null. Cannot find BarChart.");
            return;
        }
        // Find the BarChart instance within the root view
        BarChart barChart = rootView.findViewById(R.id.barchart_home_energy); // Replace with the correct ID from your layout
        if (barChart == null) {
            Log.e("HomeEnergyCalculator", "BarChart view not found. Check layout XML.");
            return;
        }

        if (records.isEmpty()) {
            Toast.makeText(requireContext(), "No weekly data available.", Toast.LENGTH_SHORT).show();
            barChart.clear();
            return;
        }
        // Get monthly emissions for food
        ArrayList<Float> monthlyHomeEnergyEmissions = ChartHelper.calculateMonthlyEmissionsForHomeEnergy(records);
        // Set up the BarChart with the monthly emissions
        String[] monthlyLabels = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        ChartHelper.setUpChart(barChart, monthlyHomeEnergyEmissions, monthlyLabels);
    }


}