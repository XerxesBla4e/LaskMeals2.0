package com.example.budgetfoods.NewAdapters;

// RestaurantAdapter1.java

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.Interface.OnMoveToResDetsListener;
import com.example.budgetfoods.models.Restaurant;
import com.example.budgetfoods.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantAdapter1 extends RecyclerView.Adapter<RestaurantAdapter1.RestaurantViewHolder> {

    private Context context;
    private OnMoveToResDetsListener onMoveToResDetsListener;
    private List<Restaurant> restaurantList;

    public RestaurantAdapter1(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    public void setOnMoveToResDetsListener(OnMoveToResDetsListener listener) {
        this.onMoveToResDetsListener = listener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_home_details, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        holder.nameTextView.setText(restaurant.getRestaurantname());
        holder.foodDescriptionTextView.setText(restaurant.getDescription());

        // Check if the rating is greater than zero before setting it in the RatingBar
        if (restaurant.getRatings() > 0) {
            holder.ratingBar.setRating(restaurant.getRatings());
        } else {
            holder.ratingBar.setRating(0.0f);
        }

        if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
            try {
                Picasso.get().load(restaurant.getImage()).into(holder.imageView);
            } catch (Exception e) {
                e.printStackTrace();
                holder.imageView.setImageResource(R.drawable.food2);
            }
        } else {
            holder.imageView.setImageResource(R.drawable.food2);
        }
    }

    @Override
    public int getItemCount() {
        // Check if the restaurantList is null, return 0 if null
        return restaurantList != null ? restaurantList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        notifyDataSetChanged();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView nameTextView, foodDescriptionTextView;
        RatingBar ratingBar;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_img22);
            nameTextView = itemView.findViewById(R.id.restaurant_name);
            foodDescriptionTextView = itemView.findViewById(R.id.food_description);
            ratingBar = itemView.findViewById(R.id.rating_bar22);
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

