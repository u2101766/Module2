package com.example.module2;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

// Combined database for both entities
@Database(entities = {FoodEmissionRecord.class, HomeEnergyEmissionRecord.class, TransportationEmissionRecord.class, WasteEmissionRecord.class, WaterEmissionRecord.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FoodEmissionRecordDao foodEmissionRecordDao();
    public abstract HomeEnergyEmissionRecordDao homeEnergyEmissionRecordDao();
    public abstract TransportationEmissionRecordDao transportationEmissionRecordDao();
    public abstract WasteEmissionRecordDao wasteEmissionRecordDao();
    public abstract WaterEmissionRecordDao waterEmissionRecordDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "emission_database" // This is the name of the database file
                            )
                            .fallbackToDestructiveMigration() // Clears data if schema changes
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}

// Separate database for FoodEmissionRecord only
@Database(entities = {FoodEmissionRecord.class}, version = 2)
abstract class FoodEmissionDatabase extends RoomDatabase {
    public abstract FoodEmissionRecordDao foodEmissionRecordDao();

    private static volatile FoodEmissionDatabase INSTANCE;

    public static FoodEmissionDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FoodEmissionDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                                    FoodEmissionDatabase.class,
                                    "food_emission_database"
                            )
                            .fallbackToDestructiveMigration() // Clears data if schema changes
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}

// Separate database for HomeEnergyEmissionRecord only
@Database(entities = {HomeEnergyEmissionRecord.class}, version = 2)
abstract class HomeEnergyDatabase extends RoomDatabase {
    public abstract HomeEnergyEmissionRecordDao homeEnergyEmissionRecordDao();

    private static volatile HomeEnergyDatabase INSTANCE;

    public static HomeEnergyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HomeEnergyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    HomeEnergyDatabase.class, "home_energy_database")
                            .fallbackToDestructiveMigration() // Clears data if schema changes
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

// Separate database for TransportationEmissionRecord only
@Database(entities = {TransportationEmissionRecord.class}, version = 2)
abstract class TransportationEnergyDatabase extends RoomDatabase {
    public abstract TransportationEmissionRecordDao transportationEmissionRecordDao();

    private static volatile TransportationEnergyDatabase INSTANCE;

    public static TransportationEnergyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TransportationEnergyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TransportationEnergyDatabase.class, "transportation_database")
                            .fallbackToDestructiveMigration() // Clears data if schema changes
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

// Separate database for WasteEmissionRecord only
@Database(entities = {WasteEmissionRecord.class}, version = 2)
abstract class WasteEnergyDatabase extends RoomDatabase {
    public abstract WasteEmissionRecordDao wasteEmissionRecordDao();

    private static volatile WasteEnergyDatabase INSTANCE;

    public static WasteEnergyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WasteEnergyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    WasteEnergyDatabase.class, "waste_database")
                            .fallbackToDestructiveMigration() // Clears data if schema changes
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

// Separate database for WaterEmissionRecord only
@Database(entities = {WaterEmissionRecord.class}, version = 2)
abstract class WaterEnergyDatabase extends RoomDatabase {
    public abstract WaterEmissionRecordDao waterEmissionRecordDao();

    private static volatile WaterEnergyDatabase INSTANCE;

    public static WaterEnergyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WaterEnergyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    WaterEnergyDatabase.class, "water_database")
                            .fallbackToDestructiveMigration() // Clears data if schema changes
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

