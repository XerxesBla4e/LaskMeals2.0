package com.example.budgetfoods.Student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.budgetfoods.Adapter.OrderAdapter;
import com.example.budgetfoods.Interface.OnMoveToDetsListener;
import com.example.budgetfoods.Models.Order;
import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.FragmentCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    private FragmentCartBinding fragmentCartBinding;
    private RecyclerView recyclerView;
    private List<Order> orderList;
    private OrderAdapter orderAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentCartBinding = FragmentCartBinding.inflate(getLayoutInflater());
        setContentView(fragmentCartBinding.getRoot());

        initViews();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        fetchOrders();

        orderAdapter.setOnMoveToDetsListener(new OnMoveToDetsListener() {
            @Override
            public void onMoveToDets(Order order) {
                Intent intent = new Intent(getApplicationContext(), ClientDetailsActivity.class);
                intent.putExtra("ordersModel", order);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        recyclerView = fragmentCartBinding.recyclerview11;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getApplicationContext(), orderList);
        recyclerView.setAdapter(orderAdapter);

    }

    private void fetchOrders() {
        CollectionReference usersCollectionRef = firestore.collection("Users");

        usersCollectionRef.whereEqualTo("accountType", "Admin")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot userSnapshot : queryDocumentSnapshots) {
                        CollectionReference ordersCollectionRef = userSnapshot.getReference()
                                .collection("Orders");
                        ordersCollectionRef.whereEqualTo("orderBy", firebaseAuth.getUid())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    if (!queryDocumentSnapshots1.isEmpty()) {
                                        for (QueryDocumentSnapshot orderSnapshot : queryDocumentSnapshots1) {
                                            Order order = orderSnapshot.toObject(Order.class);
                                            orderList.add(order);
                                        }
                                        orderAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
