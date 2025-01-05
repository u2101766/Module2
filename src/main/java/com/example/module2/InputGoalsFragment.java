package com.example.module2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.concurrent.Executors;

public class InputGoalsFragment extends Fragment {

    private DGDatabase dailyGoalsdb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input_goals, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("FragmentLifecycle", "Current Fragment: " + getClass().getSimpleName());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the database
        dailyGoalsdb = DGDatabase.getDatabase(requireContext());

        // Initialize UI components
        EditText goal1EditText = view.findViewById(R.id.goal1);
        EditText goal2EditText = view.findViewById(R.id.goal2);
        EditText goal3EditText = view.findViewById(R.id.goal3);
        Button saveGoalButton = view.findViewById(R.id.saveGoalButton);

        // Set click listener for the Save Goal button
        saveGoalButton.setOnClickListener(v -> {
            String goal1 = goal1EditText.getText().toString();
            String goal2 = goal2EditText.getText().toString();
            String goal3 = goal3EditText.getText().toString();

            if (goal1.isEmpty() || goal2.isEmpty() || goal3.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all goals!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save goals to the database
            Executors.newSingleThreadExecutor().execute(() -> {
                Goal goal = new Goal();
                goal.goal1 = goal1;
                goal.goal2 = goal2;
                goal.goal3 = goal3;
                dailyGoalsdb.goalDao().insert(goal);

                // Navigate to DisplayDailyGoalsFragment
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Goals saved successfully!", Toast.LENGTH_SHORT).show();
                    ((DailyGoalsActivity) requireActivity()).loadFragment(new DisplayDailyGoalsFragment());
                });
            });
        });
    }
}