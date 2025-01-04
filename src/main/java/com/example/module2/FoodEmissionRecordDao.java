package com.example.module2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FoodEmissionRecordDao {
    @Insert
    void insert(FoodEmissionRecord record);

    @Query("SELECT * FROM food_emission_records")
    List<FoodEmissionRecord> getAllRecords();

    @Query("DELETE FROM food_emission_records")
    void deleteAllRecords();
}

