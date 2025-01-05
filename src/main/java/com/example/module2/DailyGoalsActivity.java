package com.example.module2;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class DailyGoalsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);

        // Load the InputGoalsFragment by default
        loadFragment(new InputGoalsFragment());
    }

    // Method to load fragments
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment); // Replace current fragment
        transaction.addToBackStack(null); // Add to back stack to prevent overlap
        transaction.commit();

    }

}
