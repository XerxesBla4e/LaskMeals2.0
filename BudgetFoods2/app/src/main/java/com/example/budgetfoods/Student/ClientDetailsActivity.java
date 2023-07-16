package com.example.budgetfoods.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetfoods.Adapter.FoodOrderAdapter;
import com.example.budgetfoods.Admin.AdminMain;
import com.example.budgetfoods.Models.FoodModel;
import com.example.budgetfoods.Models.Order;
import com.example.budgetfoods.Models.UserDets;
import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.ActivityStudentDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ClientDetailsActivity extends AppCompatActivity {
    ActivityStudentDetailsBinding activityClientDetailsBinding;
    RecyclerView recyclerView;
    TextView studentname, location1, status1, totalprice;
    ImageView edit, delete;

    Order ordersModel;
    List<FoodModel> orderList;
    FoodOrderAdapter OrdersAdapter;
    FirebaseFirestore firestore;
    double total = 0.0;
    FirebaseAuth firebaseAuth;
    String OrderID, OrderBy, OrderTo;
    FirebaseUser firebaseUser;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityClientDetailsBinding = ActivityStudentDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityClientDetailsBinding.getRoot());
        initViews(activityClientDetailsBinding);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            id = firebaseUser.getUid();
        }

        RetrievePersonalDets();

        Intent intent = getIntent();

        if (intent.hasExtra("ordersModel")) {
            // Extract the Orders object from the intent's extra data
            ordersModel = intent.getParcelableExtra("ordersModel");
            OrderID = ordersModel.getOrderID();
            OrderBy = ordersModel.getOrderBy();
            OrderTo = ordersModel.getOrderTo();
            status1.setText(ordersModel.getOrderStatus());
        }
        // Create a Firestore query to retrieve the food orders for the specific order and user
        CollectionReference medicineOrdersRef = FirebaseFirestore.getInstance().collection("users")
                .document(OrderTo)
                .collection("orders")
                .document(OrderID)
                .collection("foodOrders");

        // Perform the query
        medicineOrdersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            FoodModel foodOrder = document.toObject(FoodModel.class);
                            String foodMPrice1 = foodOrder != null ? foodOrder.getFPrice() : null;
                            double mPrice = 0.0;

                            try {
                                if (foodMPrice1 != null) {
                                    mPrice = Double.parseDouble(foodMPrice1);
                                    total += mPrice;
                                }
                            } catch (NumberFormatException e) {
                                // Handle the NumberFormatException, such as logging an error or displaying an error message
                                Log.e("ClientDetails1", "Error parsing food price: " + e.getMessage());
                            }

                            orderList.add(foodOrder);

                        }
                        OrdersAdapter.setFoodModelList(orderList);
                        OrdersAdapter.notifyDataSetChanged();
                        totalprice.setText(String.format("%.2f", total));
                        totalprice.requestLayout();
                    }
                } else {
                    // Handle the error
                    Exception exception = task.getException();
                    if (exception != null) {
                        // Log or display the error message
                    }
                }
            }
        });

    }

    private void RetrievePersonalDets() {
        if (OrderBy != null) {
            DocumentReference userRef = firestore.collection("users").document(OrderBy);

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // User document exists, retrieve the data
                            UserDets user = document.toObject(UserDets.class);

                            String location = user.getLocation();
                            location1.setText(location);
                            //notstudenttoken = user.getToken();

                        } else {
                            // User document does not exist
                            // Handle accordingly
                            //  Toast.makeText(getApplicationContext(), "Client Doesn't Have Personal Info", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // An error occurred
                        Exception exception = task.getException();
                        // Handle the error
                    }
                }
            });
        } else {
            //Toast.makeText(getApplicationContext(), "Order Doesn't Exist", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(ActivityStudentDetailsBinding activityClientDetailsBinding) {
        recyclerView = activityClientDetailsBinding.studrec;
        studentname = activityClientDetailsBinding.patientname;
        location1 = activityClientDetailsBinding.patientlocation;
        status1 = activityClientDetailsBinding.orderStatus;
        totalprice = activityClientDetailsBinding.totalprice;
        edit = activityClientDetailsBinding.editstatus;
        delete = activityClientDetailsBinding.adminlocation;
        orderList = new ArrayList<>();
        OrdersAdapter = new FoodOrderAdapter(getApplicationContext(), orderList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(OrdersAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AdminMain.class);
        startActivity(intent);
        // super.onBackPressed();
        // Finish the current activity
        finish();
    }
}