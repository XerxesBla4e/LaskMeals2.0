package com.example.budgetfoods.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.Interface.OnAddToCartListener;
import com.example.budgetfoods.Models.Food;
import com.example.budgetfoods.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends ListAdapter<Food, FoodAdapter.FoodViewHolder> {

    private OnAddToCartListener onAddToCartClickListener;

    public FoodAdapter() {
        super(CALLBACK);
    }

    public void setOnAddToCartClickListener(OnAddToCartListener listener) {
        onAddToCartClickListener = listener;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home_details, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = getItem(position);
        holder.bind(food);
    }

    public void updateFoodList(List<Food> foodList) {
        submitList(foodList);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView nameTextView;
        private TextView descriptionTextView;
        private TextView locationTextView;
        private TextView priceTextView;
        private Button addToCartButton;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_food);
            nameTextView = itemView.findViewById(R.id.food_name);
            descriptionTextView = itemView.findViewById(R.id.descp);
            locationTextView = itemView.findViewById(R.id.location);
            priceTextView = itemView.findViewById(R.id.prices);
            addToCartButton = itemView.findViewById(R.id.addfood);
            addToCartButton.setOnClickListener(this);
        }

        public void bind(Food food) {
            nameTextView.setText(food.getFoodname());
            descriptionTextView.setText(food.getDescription());
            locationTextView.setText(food.getRestaurant());
            priceTextView.setText(String.format("Price: Shs %s", food.getPrice()));

            String imagePath = food.getFoodimage();
            try {
                if (imagePath != null && !imagePath.isEmpty()) {
                    Picasso.get().load(food.getFoodimage()).into(imageView);
                } else {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageResource(R.mipmap.ic_launcher);
            }

        }


        @Override
        public void onClick(View view) {
            if (onAddToCartClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Food food = getItem(position);
                    onAddToCartClickListener.onAddToCartClick(food,position);
                }
            }
        }
    }
}