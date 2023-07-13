package com.example.budgetfoods.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetfoods.Interface.OnMoveToDetsListener;
import com.example.budgetfoods.Models.Order;
import com.example.budgetfoods.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private OnMoveToDetsListener onMoveToDetsListener;
    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOnMoveToDetsListener(OnMoveToDetsListener listener) {
        this.onMoveToDetsListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set the data to the views
        holder.orderIdTextView.setText(order.getOrderID());
        holder.studentNameTextView.setText(order.getOrderBy());
        // You can set the image for the ImageView here if you have an image for the order.

        // Add any additional view setup or event handling if needed
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView studentNameTextView;
        private TextView orderIdTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.studentName);
            orderIdTextView = itemView.findViewById(R.id.orderId);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onMoveToDetsListener != null) {
                Order order = orderList.get(position);
                onMoveToDetsListener.onMoveToDets(order);
            }
        }
    }
}
