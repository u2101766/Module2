package com.example.module2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DailyGoalsAchievementFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_goals_achievement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the button
        Button afterCongratsButton = view.findViewById(R.id.afterCongratsButton);

        // Set OnClickListener for the button
        afterCongratsButton.setOnClickListener(v -> {
            // Navigate back to MainActivity
            Intent intent = new Intent(requireActivity(), DailyGoalsActivity.class);
            startActivity(intent);

            // Optional: Close the current activity
            requireActivity().finish();
        });
    }
}
