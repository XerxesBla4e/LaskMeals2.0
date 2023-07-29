package com.example.budgetfoods.Adapter;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.Interface.OnAddToCartListener;
import com.example.budgetfoods.Interface.OnQuantityChangeListener;
import com.example.budgetfoods.Models.Food;
import com.example.budgetfoods.R;
import com.example.budgetfoods.ViewModel.FoodViewModel;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class FoodAdapter extends ListAdapter<Food, FoodAdapter.FoodViewHolder> {
    private OnAddToCartListener onAddToCartClickListener;
    private TextView name, price2, newprice2, description, totalamount, quantitytextview;
    private ImageButton addQty, reduceQty;
    private RatingBar ratingBar;
    private Button addToCartBtn;
    private FoodViewModel foodViewModel; // The ViewModel instance

    public FoodAdapter(FoodViewModel viewModel) {
        super(CALLBACK);
        this.foodViewModel = viewModel;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.specificrestrecyclerview, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = getItem(holder.getAdapterPosition());
        holder.bind(food);

        // Set the button click listener here in onBindViewHolder
        holder.imageButton.setOnClickListener(v -> {
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
            quantitytextview.setText(String.valueOf(food.getQuantity()));
            addToCartBtn = dialogView.findViewById(R.id.button2);
            price2 = dialogView.findViewById(R.id.amountTextView);
            price2.setText(food.getPrice());
            newprice2 = dialogView.findViewById(R.id.discountAmountTextView);
            totalamount = dialogView.findViewById(R.id.totalAmountTextView);
            totalamount.setText(String.valueOf(food.getTotal()));
            ratingBar = dialogView.findViewById(R.id.ratingBar2);

            ratingBar.setRating(4.5f);

            computePriceDiscount(food, price2, newprice2);

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
                    foodViewModel.insert(food);
                    Toast.makeText(view.getContext(), "ADDED TO CART", Toast.LENGTH_SHORT).show();
                    //onAddToCartClickListener.onAddToCartClick(food, holder.getAdapterPosition());
                }
            });

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


    public void updateFoodList(List<Food> foodList) {
        submitList(foodList);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTextView;
        private TextView priceTextView;
        private TextView newprice; // New TextView for displaying discounted price
        private ImageButton imageButton;
        RatingBar ratingBar;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_img);
            nameTextView = itemView.findViewById(R.id.textView33);
            priceTextView = itemView.findViewById(R.id.textView34);
            newprice = itemView.findViewById(R.id.new_price); // Initialize the new price TextView
            imageButton = itemView.findViewById(R.id.imageButton6);
            ratingBar = itemView.findViewById(R.id.rating_bar22);
        }

        public void bind(Food food) {
            nameTextView.setText(food.getFoodname());
            priceTextView.setText(String.format("Price: Shs %s", food.getPrice()));

            ratingBar.setRating(4.5f);

            if (food.getDiscount() != null && !food.getDiscount().isEmpty() && food.getDiscountdescription() != null && !food.getDiscountdescription().isEmpty()) {
                int discount = Integer.parseInt(food.getDiscount());
                if (discount > 0 && food.getDiscountdescription().contains("%")) {
                    double newPrice = Double.parseDouble(food.getPrice()) * (1 - discount / 100.0);
                    newprice.setVisibility(View.VISIBLE);
                    newprice.setText(String.format(Locale.getDefault(), "Price: Shs %.2f", newPrice));
                    // Add crossline through old price
                    priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    // Remove crossline if discount condition is not met
                    priceTextView.setPaintFlags(priceTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    newprice.setVisibility(View.GONE);
                }
            } else {
                // Remove crossline and clear new price if discount conditions are not met
                priceTextView.setPaintFlags(priceTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                newprice.setVisibility(View.GONE);
            }

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

    }
}
