package com.example.budgetfoods.Interface;

import com.example.budgetfoods.Models.Food;

public interface OnAddToCartListener {
    void onAddToCartClick(Food food, int position);
}
