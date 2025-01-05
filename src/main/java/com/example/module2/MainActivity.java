package com.example.module2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to the button
        ImageButton dailyGoalButton = findViewById(R.id.daily_goal_main_activity_button);

        // Set onClick listener
        dailyGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the DailyGoalsActivity
                Intent intent = new Intent(MainActivity.this, DailyGoalsActivity.class);
                startActivity(intent);
            }
        });
    }
}