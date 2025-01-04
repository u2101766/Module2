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

public class TrannsportationCal extends Fragment {

    public static TrannsportationCal newInstance() {
        return new TrannsportationCal();
    }

    private TrannsportationCalViewModel mViewModel;
    private TransportationEmissionRecordDao transportationEmissionRecordDao; // Declare as an instance field

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trannsportation_cal, container, false);
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

        //TransportationEnergyDatabase
        TransportationEnergyDatabase transportationEmissionDatabase = Room.databaseBuilder(
                        requireContext(),
                        TransportationEnergyDatabase.class,
                        "transportation_database"
                )
                .fallbackToDestructiveMigration() // Clears data if schema changes
                .build();

        TransportationEmissionRecordDao transportationOnlyDao = transportationEmissionDatabase.transportationEmissionRecordDao();

        // Correct way to get the DAO
        transportationEmissionRecordDao = appDatabase.transportationEmissionRecordDao();

        //        <---------------------SPINNER GRAPH--------------->
        // Fetch records and show the default graph (e.g., weekly)
        Executors.newSingleThreadExecutor().execute(() -> {
            List<TransportationEmissionRecord> records = transportationEmissionRecordDao.getAllRecords();
            Log.d("TransportationCalculator", "Records fetched: " + records.size());
            requireActivity().runOnUiThread(() -> showWeeklyGraph(view, records));
        });

        // Initialize the spinner
        Spinner transportationSpinner = view.findViewById(R.id.transportation_graph_spinner);

        // Create an ArrayAdapter using a string array resource and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.transportation_graph_option, // Define this array in strings.xml
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        transportationSpinner.setAdapter(adapter);

//        Spinner foodSpinner = view.findViewById(R.id.food_graph_spinner);
        transportationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                Executors.newSingleThreadExecutor().execute(() -> {
                    List<TransportationEmissionRecord> records = transportationEmissionRecordDao.getAllRecords();
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
            List<TransportationEmissionRecord> records = transportationEmissionRecordDao.getAllRecords();
            requireActivity().runOnUiThread(() -> showWeeklyGraph(view, records));
        });
        //        <---------------------END OF SPINNER GRAPH OPTIONS--------------->


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

        //        <-------------------DATE PICKER------------------>
        // Initialize the Date Picker Button
        Button datePickerButton = view.findViewById(R.id.date_picker_transportation);

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
        Spinner transportationOptionSpinner = view.findViewById(R.id.transportation_options);
        EditText quantityInput = view.findViewById(R.id.transportation_mileage); // Update your EditText ID
        TextView co2ResultText = view.findViewById(R.id.transportation_output); // Update your TextView ID
        Button addButton = view.findViewById(R.id.add_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Set up the spinner
        ArrayAdapter<CharSequence> transportationAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.transportation_options, // Define this array in strings.xml
                android.R.layout.simple_spinner_item
        );
        transportationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transportationOptionSpinner.setAdapter(transportationAdapter);
        // Food emission factors
        final double[] transportationEmissionFactors = {
                2.68, // diesel
                0.5, // electric
                2.31, // gasoline
                0.1, // public transport
        };

        // Add button logic
        addButton.setOnClickListener(v -> {
            String quantityStr = quantityInput.getText().toString();
            int selectedTransportationIndex = transportationOptionSpinner.getSelectedItemPosition();
            String selectedDateValue = selectedDate[0];

            if (quantityStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a quantity.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTransportationIndex < 0 || selectedTransportationIndex >= transportationEmissionFactors.length) {
                Toast.makeText(requireContext(), "Please select a transportation type.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the selected date (ensure the date is not empty)
            if (selectedDateValue.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a date.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform calculation
            double quantity = Double.parseDouble(quantityStr);
            double emissionFactor = transportationEmissionFactors[selectedTransportationIndex];
            double co2e = quantity * emissionFactor;

            // Display result in the TextView
            co2ResultText.setText(String.format("%.2f kg CO₂e", co2e));

            // Save to Room database
            String selectedTransportation = transportationOptionSpinner.getSelectedItem().toString();

            TransportationEmissionRecord record = new TransportationEmissionRecord(selectedTransportation, quantity, co2e, selectedDateValue);

            // Save to both databases
            Executors.newSingleThreadExecutor().execute(() -> {
                if (transportationEmissionRecordDao != null) {
                    transportationEmissionRecordDao.insert(record);
                    List<TransportationEmissionRecord> records = transportationEmissionRecordDao.getAllRecords();
                    for (TransportationEmissionRecord insertedRecord : records) {
                        Log.d("TransportationCalculator", "Inserted into emission_database: " + insertedRecord.toString());
                    }
                }

                if (transportationOnlyDao != null) {
                    transportationOnlyDao.insert(record);
                    List<TransportationEmissionRecord> transportationRecords = transportationOnlyDao.getAllRecords();
                    for (TransportationEmissionRecord insertedRecord : transportationRecords) {
                        Log.d("TransportationCalculator", "Inserted into transportation_database: " + insertedRecord.toString());
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
            transportationOptionSpinner.setSelection(0);
            Toast.makeText(requireContext(), "Input cleared.", Toast.LENGTH_SHORT).show();
        });
        //        <---------------------END OF SPINNER CALCULATOR OPTIONS--------------->


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TrannsportationCalViewModel.class);
        // TODO: Use the ViewModel
    }

    private void showWeeklyGraph(View view, List<TransportationEmissionRecord> records) {
        Log.d("TransportationCalculator", "showWeeklyGraph called with " + records.size() + " records.");

        // Assuming you have the chart and data prepared
        BarChart barChart = view.findViewById(R.id.barchart_transportation); // your BarChart instance
        if (barChart == null) {
            Log.e("TransportationCalculator", "BarChart view not found. Check layout XML.");
            return;
        }
        if (records.isEmpty()) {
            Toast.makeText(requireContext(), "No weekly data available.", Toast.LENGTH_SHORT).show();
            barChart.clear();
            return;
        }
        // Get weekly emissions for food (replace this with actual data fetching logic)
        ArrayList<Float> weeklyTransportationEmissions = ChartHelper.calculateWeeklyEmissionsForTransportation(records);

        // Set up the BarChart with the weekly emissions
        String[] weeklyLabels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        ChartHelper.setUpChart(barChart, weeklyTransportationEmissions, weeklyLabels);
        Log.d("TransportationCalculator", "Chart setup complete.");

    }


    private void showMonthlyGraph(View rootView, List<TransportationEmissionRecord> records) {
        if (rootView == null) {
            Log.e("TransportationCalculator", "Root view is null. Cannot find BarChart.");
            return;
        }
        // Find the BarChart instance within the root view
        BarChart barChart = rootView.findViewById(R.id.barchart_transportation); // Replace with the correct ID from your layout
        if (barChart == null) {
            Log.e("TransportationCalculator", "BarChart view not found. Check layout XML.");
            return;
        }

        if (records.isEmpty()) {
            Toast.makeText(requireContext(), "No weekly data available.", Toast.LENGTH_SHORT).show();
            barChart.clear();
            return;
        }
        // Get monthly emissions for food
        ArrayList<Float> monthlyTransportationEmissions = ChartHelper.calculateMonthlyEmissionsForTransportation(records);
        // Set up the BarChart with the monthly emissions
        String[] monthlyLabels = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        ChartHelper.setUpChart(barChart, monthlyTransportationEmissions, monthlyLabels);
    }


}