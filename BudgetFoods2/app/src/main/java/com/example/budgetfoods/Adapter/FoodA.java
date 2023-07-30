package com.example.budgetfoods.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.Interface.OnQuantityChangeListener;
import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.MyCartRowBinding;
import com.example.budgetfoods.models.Food;

public class FoodA extends ListAdapter<Food, FoodA.FoodViewHolder> {
    int quantity;
    private OnQuantityChangeListener quantityChangeListener;

    public FoodA() {
        super(CALLBACK);
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }

    private static final DiffUtil.ItemCallback<Food> CALLBACK = new DiffUtil.ItemCallback<Food>() {
        @Override
        public boolean areItemsTheSame(@NonNull Food oldItem, @NonNull Food newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Food oldItem, @NonNull Food newItem) {
            return oldItem.getFoodname().equals(newItem.getFoodname())
                    && oldItem.getDescription().equals(newItem.getDescription())
                    && oldItem.getRestaurant().equals(newItem.getRestaurant())
                    && oldItem.getPrice().equals(newItem.getPrice())
                    && oldItem.getDiscount().equals(newItem.getDiscount())
                    && oldItem.getDiscountdescription().equals(newItem.getDiscountdescription())
                    && oldItem.getFoodimage().equals(newItem.getFoodimage())
                    && oldItem.getQuantity() == newItem.getQuantity()
                    && oldItem.getTotal() == newItem.getTotal();
        }
    };

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use DataBindingUtil to inflate the layout
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        MyCartRowBinding binding = MyCartRowBinding.inflate(layoutInflater, parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = getItem(position);
        holder.bind(food);
    }

    public Food getFood(int position) {
        return getItem(position);
    }

    public void clearCart() {
        submitList(null);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MyCartRowBinding binding;

        public FoodViewHolder(@NonNull MyCartRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Set click listeners for the buttons (if needed)
            binding.imageButtonAdd.setOnClickListener(this);
            binding.imageButtonRemove.setOnClickListener(this);
        }

        public void bind(Food food) {
            // Bind the `food` object to the Data Binding layout variable
           binding.setFood(food);
            // This is necessary to update the view with the data
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Food food = getItem(position);
                if (quantityChangeListener != null) {
                    if (view.getId() == R.id.imageButtonAdd) {
                        quantityChangeListener.onAddButtonClick(food, position);
                    } else if (view.getId() == R.id.imageButtonRemove) {
                        quantityChangeListener.onRemoveButtonClick(food, position);
                    }
                }
            }
        }
    }
}

