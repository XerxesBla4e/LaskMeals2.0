package com.example.budgetfoods.NewAdapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.Interface.OnAddToCartListener;
import com.example.budgetfoods.Models.Food;
import com.example.budgetfoods.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PopularFood extends RecyclerView.Adapter<PopularFood.PopularFoodViewHolder>{
    private OnAddToCartListener onAddToCartClickListener;
    private TextView name, price2, newprice2, description, totalamount, quantitytextview;
    private ImageButton addQty, reduceQty;

    private RatingBar ratingBar;
    //  private OnQuantityChangeListener quantityChangeListener;
    private Button addToCartBtn;
    private Context context;
    private List<Food> foodList;

    public PopularFood(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    public void setOnAddToCartClickListener(OnAddToCartListener listener) {
        onAddToCartClickListener = listener;
    }

    @NonNull
    @Override
    public PopularFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.popularitems, parent, false);
        return new PopularFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularFoodViewHolder holder, int position) {
        Food food = foodList.get(position);

        // Populate the views with data from the Food object
        holder.imageView.setImageResource(R.drawable.burgers); // Use your own logic to set the image resource
        holder.ratingBar.setRating(4.5f); // Use your own logic to set the rating
        holder.foodNameTextView.setText(food.getFoodname());
        holder.priceTextView.setText(String.format("Price: USh %s", food.getPrice()));
        holder.inStockTextView.setVisibility(View.VISIBLE); // Assuming all foods are in stock

        holder.quickViewButton.setOnClickListener(v -> {
            DialogPlus dialogPlus = DialogPlus.newDialog(holder.itemView.getContext())
                    .setContentHolder(new ViewHolder(R.layout.popupmenu))
                    .setExpanded(true, 1100)
                    .setGravity(Gravity.BOTTOM) // Set the dialog to appear from the bottom
                    .create();
            View dialogView = dialogPlus.getHolderView();

            ImageView imageView1 = dialogView.findViewById(R.id.imageView0);
            addQty = dialogView.findViewById(R.id.imageButtonAdd);
            reduceQty = dialogView.findViewById(R.id.imageButtonRemove);
            name = dialogView.findViewById(R.id.foodNameTextView);
            name.setText(food.getFoodname());
            description = dialogView.findViewById(R.id.descriptionTextView);
            description.setText(food.getDescription());
            quantitytextview = dialogView.findViewById(R.id.textViewQuantity);
            addToCartBtn = dialogView.findViewById(R.id.button2);
            price2 = dialogView.findViewById(R.id.amountTextView);
            newprice2 = dialogView.findViewById(R.id.discountAmountTextView);
            totalamount = dialogView.findViewById(R.id.totalAmountTextView);
            ratingBar = dialogView.findViewById(R.id.ratingBar2);

            ratingBar.setRating(4.5f);

            addQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = food.getQuantity();
                    quantity++; // Increment the quantity

                    // Update the quantity and total in the food object
                    food.setQuantity(quantity);
                    quantitytextview.setText(String.valueOf(food.getQuantity()));
                    totalamount.setText(String.valueOf(food.getTotal()));
                }
            });

            reduceQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = food.getQuantity();
                    if (quantity > 1) {
                        quantity--; // Decrement the quantity

                        // Update the quantity and total in the food object
                        food.setQuantity(quantity);
                        quantitytextview.setText(String.valueOf(food.getQuantity()));
                        totalamount.setText(String.valueOf(food.getTotal()));
                    }
                }
            });

            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onAddToCartClickListener != null) {
                        onAddToCartClickListener.onAddToCartClick(food, holder.getAdapterPosition());
                    }
                }
            });
            computePriceDiscount(food, price2, newprice2);

            String imagePath = food.getFoodimage();
            try {
                if (imagePath != null && !imagePath.isEmpty()) {
                    Picasso.get().load(food.getFoodimage()).into(imageView1);
                } else {
                    imageView1.setImageResource(R.mipmap.ic_launcher);
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageView1.setImageResource(R.mipmap.ic_launcher);
            }
            // Set the quantity and handle its click listeners
            quantitytextview.setText(String.valueOf(food.getQuantity()));
            dialogPlus.show();
        });
    }

    private void computePriceDiscount(Food food, TextView price2, TextView newprice2) {
        if (food.getDiscount() != null && !food.getDiscount().isEmpty() && food.getDiscountdescription() != null && !food.getDiscountdescription().isEmpty()) {
            int discount = Integer.parseInt(food.getDiscount());
            if (discount > 0 && food.getDiscountdescription().contains("%")) {
                double newPrice = Double.parseDouble(food.getPrice()) * (1 - discount / 100.0);
                newprice2.setVisibility(View.VISIBLE);
                newprice2.setText(String.format(Locale.getDefault(), "Price: Shs %.2f", newPrice));
                // Add crossline through old price
                price2.setPaintFlags(price2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                // Remove crossline if discount condition is not met
                price2.setPaintFlags(price2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                newprice2.setVisibility(View.GONE);
            }
        } else {
            // Remove crossline and clear new price if discount conditions are not met
            price2.setPaintFlags(price2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            newprice2.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateFoodList1(ArrayList<Food> foodList) {
        this.foodList = foodList;
        notifyDataSetChanged();
    }

    public class PopularFoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        RatingBar ratingBar;
        TextView foodNameTextView;
        TextView priceTextView;
        Button quickViewButton;
        TextView inStockTextView;

        public PopularFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_img);
            ratingBar = itemView.findViewById(R.id.ratingBar2);
            foodNameTextView = itemView.findViewById(R.id.textView33);
            priceTextView = itemView.findViewById(R.id.textView34);
            quickViewButton = itemView.findViewById(R.id.button2);
            inStockTextView = itemView.findViewById(R.id.textView35);

        }
    }

}

