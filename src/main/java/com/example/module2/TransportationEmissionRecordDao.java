package com.example.module2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransportationEmissionRecordDao {
    @Insert
    void insert(TransportationEmissionRecord record);

    @Query("SELECT * FROM transportation_emission_records")
    List<TransportationEmissionRecord> getAllRecords();

    @Query("DELETE FROM transportation_emission_records")
    void deleteAllRecords();
}






