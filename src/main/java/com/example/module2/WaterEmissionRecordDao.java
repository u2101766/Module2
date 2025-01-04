package com.example.module2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WaterEmissionRecordDao {
    @Insert
    void insert(WaterEmissionRecord record);

    @Query("SELECT * FROM water_emission_records")
    List<WaterEmissionRecord> getAllRecords();

    @Query("DELETE FROM water_emission_records")
    void deleteAllRecords();
}


