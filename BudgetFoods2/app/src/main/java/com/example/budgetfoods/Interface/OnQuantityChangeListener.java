package com.example.budgetfoods.Interface;

import com.example.budgetfoods.Models.Food;

public interface OnQuantityChangeListener {
    void onAddButtonClick(Food food, int position);
    void onRemoveButtonClick(Food food, int position);
}
