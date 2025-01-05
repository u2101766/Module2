package com.example.module2;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.concurrent.Executors;

public class DisplayDailyGoalsFragment extends Fragment {

    private DGDatabase dailyGoalsdb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_daily_goals, container, false);
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

        // Initialize TextViews
        TextView goalDisplay1 = view.findViewById(R.id.goalDisplay1);
        TextView goalDisplay2 = view.findViewById(R.id.goalDisplay2);
        TextView goalDisplay3 = view.findViewById(R.id.goalDisplay3);

        // Initialize CheckBoxes
        CheckBox checkBoxGoal1 = view.findViewById(R.id.checkBoxGoal1);
        CheckBox checkBoxGoal2 = view.findViewById(R.id.checkBoxGoal2);
        CheckBox checkBoxGoal3 = view.findViewById(R.id.checkBoxGoal3);

        // Fetch the latest goals from the database
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Goal> goals = dailyGoalsdb.goalDao().getAllGoals();
            if (!goals.isEmpty()) {
                Goal latestGoal = goals.get(goals.size() - 1); // Fetch the latest entry
                requireActivity().runOnUiThread(() -> {
                    goalDisplay1.setText(latestGoal.goal1);
                    goalDisplay2.setText(latestGoal.goal2);
                    goalDisplay3.setText(latestGoal.goal3);
                });
            } else {
                requireActivity().runOnUiThread(() -> {
                    goalDisplay1.setText("No goals to display");
                    goalDisplay2.setText("No goals to display");
                    goalDisplay3.setText("No goals to display");
                });
            }
        });

        // Listener for checkboxes
        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            if (checkBoxGoal1.isChecked() && checkBoxGoal2.isChecked() && checkBoxGoal3.isChecked()) {
                ((DailyGoalsActivity) requireActivity()).loadFragment(new DailyGoalsAchievementFragment());

            }
        };

        checkBoxGoal1.setOnCheckedChangeListener(listener);
        checkBoxGoal2.setOnCheckedChangeListener(listener);
        checkBoxGoal3.setOnCheckedChangeListener(listener);

        // Initialize "Clear All" Button
        Button clearButton = view.findViewById(R.id.DGclearButton);

        // Set OnClickListener for "Clear All" Button
        clearButton.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                dailyGoalsdb.goalDao().deleteAllGoals(); // Clear database
                requireActivity().runOnUiThread(() -> {
                    // Navigate back to InputGoalsFragment
                    ((DailyGoalsActivity) requireActivity()).loadFragment(new InputGoalsFragment());
                });
            });
        });
    }
}