package com.example.budgetfoods.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetfoods.Adapter.FoodOrderAdapter;
import com.example.budgetfoods.Adapter.OrderAdapter;
import com.example.budgetfoods.FCMSend;
import com.example.budgetfoods.Models.FoodModel;
import com.example.budgetfoods.Models.Order;
import com.example.budgetfoods.Models.UserDets;
import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.ActivityStudentAdminBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ClientDetails1 extends AppCompatActivity {
    ActivityStudentAdminBinding activityClientDetailsBinding;
    RecyclerView recyclerView;
    TextView studentname, location1, status1, totalprice;
    ImageView edit, delete;

    Order ordersModel;
    List<FoodModel> orderList;
    String notstudenttoken;

    FoodOrderAdapter OrdersAdapter;
    FirebaseFirestore firestore;
    double total = 0.0;
    FirebaseAuth firebaseAuth;
    String OrderID, OrderBy;
    FirebaseUser firebaseUser;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityClientDetailsBinding = ActivityStudentAdminBinding.inflate(getLayoutInflater());
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
            status1.setText(ordersModel.getOrderStatus());
        }
        // Create a Firestore query to retrieve the food orders for the specific order and user
        CollectionReference medicineOrdersRef = FirebaseFirestore.getInstance().collection("users")
                .document(id)
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


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrderStatusDialog();
            }
        });
    }

    private void updateOrderStatusDialog() {
        final String[] status3 = {"In Progress", "Confirmed", "Cancelled"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ClientDetails1.this);
        mBuilder.setTitle("Update Order Status");
        mBuilder.setSingleChoiceItems(status3, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int xer) {
                if (xer == 0) {
                    String Message = "In Progress";
                    updateOrderStatus(Message);
                    status1.setTextColor(getBaseContext().getResources().getColor(R.color.lightGreen));
                } else if (xer == 1) {
                    String Message = "Confirmed";
                    updateOrderStatus(Message);
                    status1.setTextColor(getBaseContext().getResources().getColor(R.color.teal_700));
                } else if (xer == 2) {
                    String Message = "Cancelled";
                    updateOrderStatus(Message);
                    status1.setTextColor(getBaseContext().getResources().getColor(R.color.red));
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mBuilder.show();
    }


    private void initViews(ActivityStudentAdminBinding activityClientDetailsBinding) {
        recyclerView = activityClientDetailsBinding.recyclerView65;
        studentname = activityClientDetailsBinding.studentname;
        location1 = activityClientDetailsBinding.studentlocation;
        status1 = activityClientDetailsBinding.orderStatus;
        totalprice = activityClientDetailsBinding.totalprice;
        edit = activityClientDetailsBinding.editstatus;
        delete = activityClientDetailsBinding.studentlocation1;
        orderList = new ArrayList<>();
        OrdersAdapter = new FoodOrderAdapter(getApplicationContext(), orderList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(OrdersAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void updateOrderStatus(String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a reference to the specific order document
        DocumentReference orderRef = db.collection("users")
                .document(id)
                .collection("orders")
                .document(OrderID);

        // Update the orderStatus field with the new message
        orderRef.update("orderStatus", message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Order is now " + message, Toast.LENGTH_SHORT).show();
                        prepareNotificationMessage(message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void prepareNotificationMessage(String message) {
        if (notstudenttoken != null) {
            FCMSend.pushNotification(
                    ClientDetails1.this,
                    notstudenttoken,
                    "Status Update",
                    message
            );
        }
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
                            notstudenttoken = user.getToken();

                        } else {
                            // User document does not exist
                            // Handle accordingly
                            Toast.makeText(getApplicationContext(), "Client Doesn't Have Personal Info", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AdminMain.class);
        startActivity(intent);
        // super.onBackPressed();
        // Finish the current activity
        finish();
    }
}