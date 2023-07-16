package com.example.budgetfoods.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.Models.Food;
import com.example.budgetfoods.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ViewFoodAdapter extends RecyclerView.Adapter<ViewFoodAdapter.ViewHolder> {

    private Context context;
    private List<Food> foodList;

    public ViewFoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_food_my, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.bind(food);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodImage;
        private TextView foodName;
        private TextView foodDescription;
        private TextView foodLocation;
        private TextView price;
        private TextView newprice;
        private TextView discountPercentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.img_food);
            foodName = itemView.findViewById(R.id.food_name);
            foodDescription = itemView.findViewById(R.id.descp);
            foodLocation = itemView.findViewById(R.id.location);
            price = itemView.findViewById(R.id.prices);
            newprice = itemView.findViewById(R.id.new_price);
            discountPercentage = itemView.findViewById(R.id.discount_percentage);
        }

        public void bind(Food food) {
            foodName.setText(food.getFoodname());
            foodDescription.setText(food.getDescription());
            foodLocation.setText(food.getRestaurant());
            price.setText(String.format("Price: %s", food.getPrice()));

            if (food.getDiscount() != null && !food.getDiscount().isEmpty() && food.getDiscountdescription() != null && !food.getDiscountdescription().isEmpty()) {
                int discount = Integer.parseInt(food.getDiscount());
                if (discount > 0 && food.getDiscountdescription().contains("%")) {
                    double newPrice = Double.parseDouble(food.getPrice()) * (1 - discount / 100.0);
                    newprice.setVisibility(View.VISIBLE);
                    newprice.setText(String.format(Locale.getDefault(), "Price: Shs %.2f", newPrice));
                    // Add crossline through old price
                    price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    // Remove crossline if discount condition is not met
                    price.setPaintFlags(price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    newprice.setVisibility(View.GONE);
                }
            } else {
                // Remove crossline and clear new price if discount conditions are not met
                price.setPaintFlags(price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                newprice.setVisibility(View.GONE);
            }

            String imagePath = food.getFoodimage();
            try {
                if (imagePath != null && !imagePath.isEmpty()) {
                    Picasso.get().load(food.getFoodimage()).into(foodImage);
                } else {
                    foodImage.setImageResource(R.mipmap.ic_launcher);
                }
            } catch (Exception e) {
                e.printStackTrace();
                foodImage.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
}