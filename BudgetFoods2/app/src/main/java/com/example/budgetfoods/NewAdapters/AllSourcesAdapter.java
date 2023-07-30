package com.example.budgetfoods.NewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.models.AllSourcesModel;
import com.example.budgetfoods.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllSourcesAdapter extends RecyclerView.Adapter<AllSourcesAdapter.AllSourcesViewHolder> {
    private Context context;
    private List<AllSourcesModel> sourceList;

    public AllSourcesAdapter(Context context, List<AllSourcesModel> sourceList) {
        this.context = context;
        this.sourceList = sourceList;
    }

    @NonNull
    @Override
    public AllSourcesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_cat, parent, false);
        return new AllSourcesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllSourcesViewHolder holder, int position) {
        AllSourcesModel source = sourceList.get(position);

        // Load the image into the ImageView using Picasso
        Picasso.get().load(source.getSourceimage()).into(holder.imageView);
        // Set the text for Text 1
        holder.textView.setText(source.getSourcename());
    }

    @Override
    public int getItemCount() {
        return sourceList.size();
    }

    public static class AllSourcesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public AllSourcesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image2);
            textView = itemView.findViewById(R.id.textview2);
        }
    }
}
