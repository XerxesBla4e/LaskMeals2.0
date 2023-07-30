package com.example.budgetfoods.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.budgetfoods.models.Food;

import java.util.List;

@Dao
public interface FoodDAO {
    @Insert
    public void insert(Food food);

    @Update
    public void update(Food food);

    @Delete
    public void delete(Food food);


    @Query("SELECT * FROM my_cart")
    public LiveData<List<Food>> getAllData();
}
