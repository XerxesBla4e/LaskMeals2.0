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
import com.example.budgetfoods.FCMSend;
import com.example.budgetfoods.Models.FoodModel;
import com.example.budgetfoods.Models.Order;
import com.example.budgetfoods.Models.UserDets;
import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.ActivityStudentDetailsBinding;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClientDetails1 extends AppCompatActivity {
    ActivityStudentDetailsBinding activityClientDetailsBinding;
    RecyclerView recyclerView;
    TextView studentname, location1, status1, totalprice;
    ImageView edit, delete;
    String restaurant54;
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

        activityClientDetailsBinding = ActivityStudentDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityClientDetailsBinding.getRoot());
        initViews(activityClientDetailsBinding);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            id = firebaseUser.getUid();
        }

        Intent intent = getIntent();

        if (intent.hasExtra("ordersModel")) {
            // Extract the Orders object from the intent's extra data
            ordersModel = intent.getParcelableExtra("ordersModel");
            OrderID = ordersModel.getOrderID();
            OrderBy = ordersModel.getOrderBy();
            status1.setText(ordersModel.getOrderStatus());
        }

        retrievePersonalDets(OrderBy);

        Toast.makeText(getApplicationContext(), "Order By"+OrderBy, Toast.LENGTH_SHORT).show();
        // Create a Firestore query to retrieve the food orders for the specific order and user
        CollectionReference medicineOrdersRef = FirebaseFirestore.getInstance().collection("users");
        // Modify the query to fetch orders from nested sub-collections
        CollectionReference ordersRef = medicineOrdersRef
                .document(id)
                .collection("Restaurants");

        // Perform the query
        ordersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        total = 0.0;
                        orderList.clear();

                        for (QueryDocumentSnapshot restaurantSnapshot : querySnapshot) {
                            String restaurantId = restaurantSnapshot.getId();

                            CollectionReference foodOrdersRef = ordersRef
                                    .document(restaurantId)
                                    .collection("orders")
                                    .document(OrderID)
                                    .collection("foodOrders");

                            // Perform the query to fetch food orders
                            foodOrdersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @SuppressLint("DefaultLocale")
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> foodOrderTask) {
                                    if (foodOrderTask.isSuccessful()) {
                                        QuerySnapshot foodOrderSnapshot = foodOrderTask.getResult();
                                        if (!foodOrderSnapshot.isEmpty()) {
                                            for (DocumentSnapshot document : foodOrderSnapshot.getDocuments()) {
                                                FoodModel foodOrder = document.toObject(FoodModel.class);
                                                if (foodOrder != null) {
                                                    String foodMPrice1 = String.valueOf(foodOrder.getFTotal());
                                                    //restaurant54 = foodOrder.getFRestaurant();
                                                    double mPrice = 0.0;

                                                    try {
                                                        mPrice = Double.parseDouble(foodMPrice1);
                                                        total += mPrice;
                                                    } catch (NumberFormatException e) {
                                                        // Handle the NumberFormatException, such as logging an error or displaying an error message
                                                        Log.e("ClientDetails1", "Error parsing food price: " + e.getMessage());
                                                    }

                                                    orderList.add(foodOrder);
                                                }
                                            }
                                            // Notify the adapter of data change
                                            OrdersAdapter.setFoodModelList(orderList);
                                            OrdersAdapter.notifyDataSetChanged();

                                            // Update the total price
                                            totalprice.setText(String.format("%.2f", total));
                                            totalprice.requestLayout();
                                        }
                                    } else {
                                        // Handle the error in fetching food orders
                                        Exception exception = foodOrderTask.getException();
                                        if (exception != null) {
                                            // Log or display the error message
                                        }
                                    }
                                }
                            });
                        }
                    }
                } else {
                    // Handle the error in fetching restaurant orders
                    Exception exception = task.getException();
                    if (exception != null) {
                        // Log or display the error message
                    }
                }
            }
        });

        if(status1.getText().equals("Cancelled")){
            status1.setTextColor(getBaseContext().getResources().getColor(R.color.red));
        }else if(status1.getText().equals("In Progress")){
            status1.setTextColor(getBaseContext().getResources().getColor(R.color.lightGreen));
        } else if (status1.getText().equals("Confirmed")) {
            status1.setTextColor(getBaseContext().getResources().getColor(R.color.teal_700));
        }

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
                    status1.setText(Message);
                    status1.setTextColor(getBaseContext().getResources().getColor(R.color.lightGreen));
                } else if (xer == 1) {
                    String Message = "Confirmed";
                    updateOrderStatus(Message);
                    status1.setText(Message);
                    status1.setTextColor(getBaseContext().getResources().getColor(R.color.teal_700));
                } else if (xer == 2) {
                    String Message = "Cancelled";
                    updateOrderStatus(Message);
                    status1.setText(Message);
                    status1.setTextColor(getBaseContext().getResources().getColor(R.color.red));
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mBuilder.show();
    }


    private void initViews(ActivityStudentDetailsBinding activityClientDetailsBinding) {
        recyclerView = activityClientDetailsBinding.studrec;
        studentname = activityClientDetailsBinding.patientname;
        location1 = activityClientDetailsBinding.patientlocation;
        status1 = activityClientDetailsBinding.orderStatus;
        totalprice = activityClientDetailsBinding.totalprice;
        edit = activityClientDetailsBinding.editstatus;
        delete = activityClientDetailsBinding.editstatus;
        orderList = new ArrayList<>();
        OrdersAdapter = new FoodOrderAdapter(getApplicationContext(), orderList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(OrdersAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void updateOrderStatus(String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a reference to the restaurants collection for the user
        CollectionReference restaurantsRef = db.collection("users")
                .document(id)
                .collection("Restaurants");

        // Query the restaurants collection for the user
        restaurantsRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Loop through each restaurant document to find the correct restaurantId
                        for (QueryDocumentSnapshot restaurantDoc : queryDocumentSnapshots) {
                            String restaurantId = restaurantDoc.getId();

                            // Create a reference to the specific order document
                            DocumentReference orderRef = db.collection("users")
                                    .document(id)
                                    .collection("Restaurants")
                                    .document(restaurantId)
                                    .collection("orders")
                                    .document(OrderID);

                            // Update the orderStatus field with the new message
                            orderRef.update("orderStatus", message)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Order status updated successfully for this restaurant
                                            Toast.makeText(getApplicationContext(), "Order is now " + message + " for restaurant: " + restaurantDoc.getString("restaurantname"), Toast.LENGTH_SHORT).show();
                                            prepareNotificationMessage(message);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to update order status for this restaurant
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
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

    private void retrievePersonalDets(String orderBy) {
        if (orderBy != null && !orderBy.isEmpty()) {
            CollectionReference usersCollectionRef = firestore.collection("users");

            usersCollectionRef.whereEqualTo("uid", orderBy).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            UserDets user = document.toObject(UserDets.class);

                            if (user != null) {
                                String name = user.getName();
                                studentname.setText(name);
                                String location = user.getLocation();
                                location1.setText(location);
                                notstudenttoken = user.getToken();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error: Unable to retrieve user data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Student Doesn't Have Personal Info", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Exception exception = task.getException();
                     //   Toast.makeText(getApplicationContext(), "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        // Log the error for further investigation
                        Log.e("FirestoreError", "Error retrieving personal data", exception);
                    }
                }
            });
        } else {
         //   Toast.makeText(getApplicationContext(), "Error: Invalid OrderBy value", Toast.LENGTH_SHORT).show();
            // Log an error message for debugging
            Log.e("FirestoreError", "Invalid OrderBy value: " + OrderBy);
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