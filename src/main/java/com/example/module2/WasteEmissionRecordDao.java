package com.example.module2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WasteEmissionRecordDao {
    @Insert
    void insert(WasteEmissionRecord record);

    @Query("SELECT * FROM waste_emission_records")
    List<WasteEmissionRecord> getAllRecords();

    @Query("DELETE FROM waste_emission_records")
    void deleteAllRecords();
}


