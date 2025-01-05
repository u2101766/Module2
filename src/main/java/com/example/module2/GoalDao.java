package com.example.module2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GoalDao {
    // Insert a new goal into the database
    @Insert
    void insert(Goal goal);

    // Retrieve all goals from the database
    @Query("SELECT * FROM daily_goals")
    List<Goal> getAllGoals();

    // Delete all goals from the database
    @Query("DELETE FROM daily_goals")
    void deleteAllGoals();
}