package com.example.budgetfoods.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;


import com.example.budgetfoods.DAO.FoodDAO;
import com.example.budgetfoods.Database.FoodDatabase;
import com.example.budgetfoods.Models.Food;

import java.util.List;

public class FoodRepo {
    private FoodDAO foodDAO;
    private LiveData<List<Food>> foodlist;

    public FoodRepo(Application application) {
        FoodDatabase medicineDatabase = FoodDatabase.getInstance(application);
        foodDAO = medicineDatabase.foodDAO();
        foodlist = foodDAO.getAllData();
    }

    public void insertData(Food food) {
        new InsertTask(foodDAO).execute(food);
    }

    public void updateData(Food food) {

        new UpdateTask(foodDAO).execute(food);
    }

    public void deleteData(Food food) {
        new DeleteTask(foodDAO).execute(food);
    }

    public LiveData<List<Food>> getAllData() {
        return foodlist;
    }

    private static class InsertTask extends AsyncTask<Food, Void, Void> {
        private FoodDAO foodDAO;

        public InsertTask(FoodDAO foodDAO) {
            this.foodDAO = foodDAO;
        }

        @Override
        protected Void doInBackground(Food... foods) {
            foodDAO.insert(foods[0]);
            return null;
        }
    }

    private static class UpdateTask extends AsyncTask<Food, Void, Void> {

        private FoodDAO foodDAO;


        public UpdateTask(FoodDAO foodDAO) {
            this.foodDAO = foodDAO;
        }

        @Override
        protected Void doInBackground(Food... foods) {
            foodDAO.update(foods[0]);
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Food, Void, Void> {
        private FoodDAO foodDAO;

        public DeleteTask(FoodDAO foodDAO) {
            this.foodDAO = foodDAO;
        }


        @Override
        protected Void doInBackground(Food... foods) {
            foodDAO.delete(foods[0]);
            return null;
        }
    }
}
