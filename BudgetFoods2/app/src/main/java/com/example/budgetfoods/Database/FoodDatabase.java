package com.example.budgetfoods.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.budgetfoods.DAO.FoodDAO;
import com.example.budgetfoods.models.Food;


@Database(entities = Food.class, version = 1)
public abstract class FoodDatabase extends RoomDatabase {
    private static FoodDatabase instance;

    public abstract FoodDAO foodDAO();

    public static synchronized FoodDatabase getInstance(Context context) {
        if (null == instance) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            FoodDatabase.class, "food_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
