package com.example.budgetfoods.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.Models.FoodModel;
import com.example.budgetfoods.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodOrderAdapter extends RecyclerView.Adapter<FoodOrderAdapter.FoodOrderViewHolder> {
    private Context context;
    private List<FoodModel> foodModelList;

    public FoodOrderAdapter(Context context, List<FoodModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
    }
    public void setFoodModelList(List<FoodModel> foodList) {
        this.foodModelList = foodList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FoodOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orderlist, parent, false);
        return new FoodOrderViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull FoodOrderViewHolder holder, int position) {
        FoodModel foodModel = foodModelList.get(position);

        holder.textViewName.setText(foodModel.getFName());
        holder.textViewDescription.setText(foodModel.getFDescription());
        holder.textViewPrice.setText(String.format("Price: $%s", foodModel.getFPrice()));
        holder.textViewQuantity.setText(String.format("Quantity: %d", foodModel.getFQuantity()));

        String imagePath = foodModel.getFImage();
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                Picasso.get().load(foodModel.getFImage()).into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.mipmap.ic_launcher);
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public static class FoodOrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewDescription;
        TextView textViewPrice;
        TextView textViewQuantity;

        public FoodOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView7);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
        }
    }
}

