package com.example.budgetfoods.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetfoods.Adapter.FoodA;
import com.example.budgetfoods.Constants;
import com.example.budgetfoods.FCMSend;
import com.example.budgetfoods.Interface.OnQuantityChangeListener;
import com.example.budgetfoods.Models.Food;
import com.example.budgetfoods.Models.UserDets;
import com.example.budgetfoods.R;
import com.example.budgetfoods.ViewModel.FoodViewModel;
import com.example.budgetfoods.databinding.ActivityCartBinding;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding activityCartBinding;
    FoodA adapter;
    List<Food> foodList;
    RecyclerView recyclerView;
    Button button;
    FoodViewModel foodViewModel;

    TextView textView;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    private static final String TAG = "error";
    String notadmintoken;
    String email1 = "Mugabilenny@gmail.com";
    String fName = "Mugabi";
    String lName = "Lenny";
    String narration = "payment for food";
    String txRef;
    String country = "UG";
    String currency = "UGX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityCartBinding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(activityCartBinding.getRoot());

        initViews(activityCartBinding);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        foodViewModel.getAllFoods().observe(this, new Observer<List<Food>>() {
            @Override
            public void onChanged(List<Food> foods) {
                adapter.submitList(foods);
            }
        });
        adapter.setOnQuantityChangeListener(new OnQuantityChangeListener() {
            @Override
            public void onAddButtonClick(Food food, int position) {
                int quantity = food.getQuantity();
                quantity++; // Increment the quantity

                // Update the quantity and total in the food object
                food.setQuantity(quantity);

                foodViewModel.update(food);

                // Notify the adapter of the data change for the specific item
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onRemoveButtonClick(Food food, int position) {
                int quantity = food.getQuantity();
                if (quantity > 1) {
                    quantity--; // Decrement the quantity

                    // Update the quantity and total in the food object
                    food.setQuantity(quantity);

                    foodViewModel.update(food);

                    // Notify the adapter of the data change for the specific item
                    adapter.notifyItemChanged(position);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sphone = "0778612930";
                double totalp = computeTotalPrice();
                if ((totalp <= 0) && TextUtils.isEmpty(sphone)) {
                    Toast.makeText(getApplicationContext(), "No items to charge", Toast.LENGTH_SHORT).show();
                } else {
                    processPayment(totalp, sphone);
                }
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    foodViewModel.delete(adapter.getFood(viewHolder.getAdapterPosition()));
                    Toast.makeText(CartActivity.this, "Cart Item deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(activityCartBinding.recyclerview11);
    }

    private void initViews(ActivityCartBinding activityCartBinding) {
        textView = activityCartBinding.textView8;
        button = activityCartBinding.button6;
        foodList = new ArrayList<>();
        recyclerView = activityCartBinding.recyclerview11;
        adapter = new FoodA();
    }

    public double computeTotalPrice() {
        List<Food> foodcarts = adapter.getCurrentList();
        double totalPrice1 = 0.0;
        if (foodcarts.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
        } else {
            for (Food food : foodcarts) {
                String foodPrice = food.getPrice();
                // Remove any non-numeric characters from the string
                String priceWithoutCurrency = foodPrice.replaceAll("[^\\d.]", "");
                // Parse the price as a double
                double mPrice3 = Double.parseDouble(priceWithoutCurrency);
                totalPrice1 += mPrice3;
            }
        }
        return totalPrice1;
    }

    @SuppressLint("DefaultLocale")
    private void processCarOrder() {
        List<Food> foodcarts = adapter.getCurrentList();
        if (foodcarts.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
        } else {
            double totalPrice = 0.0;
            for (Food food : foodcarts) {
                String desiredAccountType = "Admin";
                String desiredFoodId = food.getFId();
                String medicineMPrice1 = food.getPrice();
                // Remove any non-numeric characters from the string
                String priceWithoutCurrency = medicineMPrice1.replaceAll("[^\\d.]", "");
                // Parse the price as a double
                double mPrice2 = Double.parseDouble(priceWithoutCurrency);
                totalPrice += mPrice2;

                CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");

                usersRef.whereEqualTo("accounttype", desiredAccountType)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null) {
                                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                            String userId = document.getId();
                                            // Check if the admin has the desired medicine
                                            CollectionReference medicineRef = FirebaseFirestore.getInstance().collection("users")
                                                    .document(userId)
                                                    .collection("Food");
                                            medicineRef.whereEqualTo("fId", desiredFoodId)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                QuerySnapshot medicineQuerySnapshot = task.getResult();
                                                                if (medicineQuerySnapshot != null && !medicineQuerySnapshot.isEmpty()) {
                                                                    // The admin has the desired medicine
                                                                    // Create a new order for the admin
                                                                    RetrieveAdminToken(userId);

                                                                    // Create the order data
                                                                    final String timestamp = "" + System.currentTimeMillis();
                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("orderID", "" + timestamp);
                                                                    hashMap.put("orderTime", "" + timestamp);
                                                                    hashMap.put("orderStatus", "In Progress");
                                                                    hashMap.put("orderTo", "" + userId);
                                                                    hashMap.put("orderBy", "" + firebaseAuth.getUid());
                                                                    //add orderto

                                                                    CollectionReference ordersRef = FirebaseFirestore.getInstance().collection("users")
                                                                            .document(userId)
                                                                            .collection("orders");

                                                                    ordersRef.document(timestamp).set(hashMap)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    // Order added successfully
                                                                                    List<Food> foodscarts = adapter.getCurrentList();
                                                                                    for (Food food : foodscarts) {
                                                                                        // Check if the medicine id falls under the admin's medicines
                                                                                        if (food.getFId().equals(desiredFoodId)) {
                                                                                            // Create a new order for the medicine item within the admin's order
                                                                                            CollectionReference medicineOrdersRef = ordersRef.document(timestamp)
                                                                                                    .collection("foodOrders");

                                                                                            // Create a HashMap to store the details of the medicine
                                                                                            HashMap<String, Object> medicineDetails = new HashMap<>();
                                                                                            medicineDetails.put("fId", food.getFId());
                                                                                            medicineDetails.put("fName", food.getFoodname());
                                                                                            medicineDetails.put("fDescription", food.getDescription());
                                                                                            medicineDetails.put("fPrice", food.getPrice());
                                                                                            medicineDetails.put("fDiscount", food.getDiscount());
                                                                                            medicineDetails.put("fDiscountDesc", food.getDiscountdescription());
                                                                                            medicineDetails.put("fTimestamp", food.getTimestamp());
                                                                                            medicineDetails.put("fUid", food.getUid());
                                                                                            medicineDetails.put("fImage", food.getFoodimage());

                                                                                            medicineOrdersRef.add(medicineDetails)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                                                            // Medicine order added successfully
                                                                                                            Toast.makeText(getApplicationContext(), "Food Order Placed Successfully", Toast.LENGTH_SHORT).show();
                                                                                                            adapter.clearCart();
                                                                                                            prepareNotificationMessage("New Food Order: ID" + timestamp);
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            // Error adding medicine order
                                                                                                            Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    // Error adding order
                                                                                    Toast.makeText(getApplicationContext(), "Failed to process order " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            } else {
                                                                // Handle the error
                                                                Exception exception = task.getException();
                                                                if (exception != null) {
                                                                    Log.d(TAG, exception + "");
                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                } else {
                                    // Handle the error
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        Log.d(TAG, exception + "");
                                    }
                                }
                            }
                        });
            }
            textView.setText(String.format("TOTAL COST:UG X %s", totalPrice));
        }
    }

    private void RetrieveAdminToken(String userId) {
        DocumentReference userRef = firestore.collection("users").document(userId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // User document exists, retrieve the data
                        UserDets user = document.toObject(UserDets.class);

                        if (user.getToken() != null) {
                            notadmintoken = user.getToken();
                        }

                    } else {
                        // User document does not exist
                        // Handle accordingly
                        Toast.makeText(getApplicationContext(), "Admin Doesn't Have Personal Info", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // An error occurred
                    Exception exception = task.getException();
                    exception.printStackTrace();
                    // Handle the error
                }
            }
        });
    }

    private void prepareNotificationMessage(String message) {
        if (notadmintoken != null) {
            FCMSend.pushNotification(
                    CartActivity.this,
                    notadmintoken,
                    "New Order",
                    message
            );
        }
    }

    private void processPayment(double samount, String sphone) {
        txRef = email1 + " " + UUID.randomUUID().toString();

        new RaveUiManager(this).setAmount(samount)
                .setCurrency(currency)
                .setCountry(country)
                .setEmail(email1)
                .setfName(fName)
                .setlName(lName)
                .setNarration(narration)
                .setPublicKey(Constants.PUBLIC_KEY)
                .setEncryptionKey(Constants.ENCRYPTION_KEY)
                .setTxRef(txRef)
                .setPhoneNumber(sphone, true)
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptUgMobileMoneyPayments(true)
                .acceptBankTransferPayments(true)
                .acceptUssdPayments(true)
                .onStagingEnv(false)
                .isPreAuth(true)
                .shouldDisplayFee(true)
                .showStagingLabel(false)
                .withTheme(R.style.Theme_MomoTest)
                .initialize();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "SUCCESS ", Toast.LENGTH_SHORT).show();
                processCarOrder();
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR ", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED ", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}