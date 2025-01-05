package com.example.module2;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
@Database(entities = {Goal.class}, version = 1, exportSchema = false)
public abstract class DGDatabase extends RoomDatabase {
    public abstract GoalDao goalDao();

    private static volatile DGDatabase INSTANCE;

    public static DGDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DGDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DGDatabase.class, "goal_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
