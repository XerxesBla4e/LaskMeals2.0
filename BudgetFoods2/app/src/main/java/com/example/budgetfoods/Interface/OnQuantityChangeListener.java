package com.example.budgetfoods.Interface;

import com.example.budgetfoods.models.Food;

public interface OnQuantityChangeListener {
    void onAddButtonClick(Food food, int position);
    void onRemoveButtonClick(Food food, int position);
}
