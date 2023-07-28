package com.example.budgetfoods.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.Interface.OnQuantityChangeListener;
import com.example.budgetfoods.Models.Food;
import com.example.budgetfoods.R;
import com.squareup.picasso.Picasso;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_row, parent, false);
        return new FoodViewHolder(view);
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
        private ImageView imageView;
        private TextView nameTextView;
        private TextView descriptionTextView;
        private TextView locationTextView;
        private TextView priceTextView;
        private ImageButton addButton;
        private ImageButton removeButton;
        private TextView quantityTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView7);
            nameTextView = itemView.findViewById(R.id.textViewName);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            locationTextView = itemView.findViewById(R.id.textViewLocation);
            priceTextView = itemView.findViewById(R.id.textViewPrice);
            addButton = itemView.findViewById(R.id.imageButtonAdd);
            removeButton = itemView.findViewById(R.id.imageButtonRemove);
            quantityTextView = itemView.findViewById(R.id.textViewQuantity);

            addButton.setOnClickListener(this);
            removeButton.setOnClickListener(this);

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

            // Set the quantity and handle its click listeners
            quantity = food.getQuantity();
            quantityTextView.setText(String.valueOf(quantity));

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Food food = getItem(position);
                if (quantityChangeListener != null) {
                    if (view.getId() == R.id.imageButtonAdd) {
                        quantityChangeListener.onAddButtonClick(food,position);
                    } else if (view.getId() == R.id.imageButtonRemove) {
                        quantityChangeListener.onRemoveButtonClick(food,position);

                    }
                }
            }
        }
    }
}
