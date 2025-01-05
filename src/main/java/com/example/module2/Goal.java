package com.example.module2;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String goal1; // Represents the first goal
    public String goal2; // Represents the second goal
    public String goal3; // Represents the third goal
}