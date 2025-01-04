package com.example.module2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HomeEnergyEmissionRecordDao {
    @Insert
    void insert(HomeEnergyEmissionRecord record);

    @Query("SELECT * FROM home_energy_emission_records")
    List<HomeEnergyEmissionRecord> getAllRecords();

    @Query("DELETE FROM home_energy_emission_records")
    void deleteAllRecords();
}