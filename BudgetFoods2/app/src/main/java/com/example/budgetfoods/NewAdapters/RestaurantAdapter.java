package com.example.budgetfoods.NewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.Interface.OnMoveToResDetsListener;
import com.example.budgetfoods.models.Restaurant;
import com.example.budgetfoods.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private Context context;
    private OnMoveToResDetsListener onMoveToResDetsListener;
    private List<Restaurant> restaurantList;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    public void setOnMoveToResDetsListener(OnMoveToResDetsListener listener) {
        this.onMoveToResDetsListener = listener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adminrestaurantitem, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        holder.nameTextView.setText(restaurant.getRestaurantname());
        holder.restaurantIdTextView.setText(restaurant.getRId());

        if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
            Picasso.get().load(restaurant.getImage()).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView nameTextView, restaurantIdTextView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.Image);
            nameTextView = itemView.findViewById(R.id.Name);
            restaurantIdTextView = itemView.findViewById(R.id.restaurantId);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onMoveToResDetsListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Restaurant restaurant = restaurantList.get(position);
                    onMoveToResDetsListener.onMoveToDets(restaurant, position);
                }
            }
        }
    }
}
