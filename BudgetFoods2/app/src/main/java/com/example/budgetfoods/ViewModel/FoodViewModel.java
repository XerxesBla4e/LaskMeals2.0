package com.example.budgetfoods.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.budgetfoods.Repository.FoodRepo;
import com.example.budgetfoods.models.Food;

import java.util.List;

public class FoodViewModel extends AndroidViewModel {
    private FoodRepo foodRepo;
    private LiveData<List<Food>> foodList;

    public FoodViewModel(Application application) {
        super(application);
        foodRepo = new FoodRepo(application);
        foodList = foodRepo.getAllData();
    }

    public void insert(Food food) {
        foodRepo.insertData(food);
    }

    public void delete(Food food) {
        foodRepo.deleteData(food);
    }

    public void update(Food food) {
        foodRepo.updateData(food);
    }

    public LiveData<List<Food>> getAllFoods() {
        return foodList;
    }

    public void deleteAllFoods(List<Food> foods) {
        for (Food food : foods) {
            foodRepo.deleteData(food);
        }
    }
}
