package com.example.budgetfoods.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding activityCartBinding;
    FoodA adapter;
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

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize foodViewModel before observing the LiveData
        foodViewModel = new ViewModelProvider(this).get(FoodViewModel.class);
        adapter = new FoodA();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        foodViewModel.getAllFoods().observe(this, new Observer<List<Food>>() {
            @Override
            public void onChanged(List<Food> foods) {
                if (foods.isEmpty()) {
                    Log.d(TAG, "No Items in the Food list");
                    Toast.makeText(getApplicationContext(), "No Items Food", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Items Present: " + foods.size());
                    Toast.makeText(getApplicationContext(), "Items Present: " + foods.size(), Toast.LENGTH_SHORT).show();
                }
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

        // Inside onClick method of CartActivity
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sphone = "0778612930";
                double totalp = computeTotalPrice();
                if ((totalp <= 0) && TextUtils.isEmpty(sphone)) {
                    Toast.makeText(getApplicationContext(), "No items to charge", Toast.LENGTH_SHORT).show();
                } else {
                    // Show the payment options dialog
                    showPaymentOptionsDialog(totalp, sphone);
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
        recyclerView = activityCartBinding.recyclerview11;
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
                textView.setText(String.format("TOTAL COST:UGX %s", totalPrice1));
                textView.requestLayout();
            }
        }
        return totalPrice1;
    }


    private void showPaymentOptionsDialog(double totalp, String sphone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment_options, null);
        builder.setView(dialogView);

        // RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        RadioButton radioPayOnDelivery = dialogView.findViewById(R.id.radioPayOnDelivery);
        RadioButton radioPayOnline = dialogView.findViewById(R.id.radioPayOnline);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            if (radioPayOnDelivery.isChecked()) {
                // Handle "Pay on Delivery" option
                processCarOrder();
            } else if (radioPayOnline.isChecked()) {
                // Handle "Pay Online" option
                processPayment(totalp, sphone);
            } else {
                // Handle the case where no option is selected
                Toast.makeText(getApplicationContext(), "Please select a payment option", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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

                                            // Create a new order for the admin
                                            RetrieveAdminToken(userId);

                                            CollectionReference restaurantRef = firestore.collection("users")
                                                    .document(userId)
                                                    .collection("Restaurants");

                                            restaurantRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot restaurantSnapshot : task.getResult()) {
                                                            // Get the restaurant ID
                                                            String restaurantId = restaurantSnapshot.getId();

                                                            // Check if the restaurant has the desired food item
                                                            DocumentReference foodDocRef = restaurantRef.document(restaurantId)
                                                                    .collection("Food")
                                                                    .document(desiredFoodId);

                                                            foodDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> foodTask) {
                                                                    if (foodTask.isSuccessful()) {
                                                                        DocumentSnapshot foodSnapshot = foodTask.getResult();
                                                                        if (foodSnapshot.exists()) {

                                                                            // Create the order data
                                                                            final String timestamp = "" + System.currentTimeMillis();
                                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                                            hashMap.put("orderID", "" + timestamp);
                                                                            hashMap.put("orderTime", "" + timestamp);
                                                                            hashMap.put("orderStatus", "In Progress");
                                                                            hashMap.put("orderTo", "" + userId);
                                                                            hashMap.put("orderBy", "" + firebaseAuth.getUid());

                                                                            // Create a new order for the restaurant (assuming "orders" is the collection name)
                                                                            CollectionReference ordersRef = restaurantRef
                                                                                    .document(restaurantId)
                                                                                    .collection("orders");
                                                                            ordersRef.document(timestamp).set(hashMap)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {

                                                                                            CollectionReference foodOrdersRef = ordersRef.document(timestamp)
                                                                                                    .collection("foodOrders");

                                                                                            // Create a HashMap to store the details of the medicine
                                                                                            HashMap<String, Object> foodDetails = new HashMap<>();
                                                                                            foodDetails.put("fId", food.getFId());
                                                                                            foodDetails.put("fName", food.getFoodname());
                                                                                            foodDetails.put("fDescription", food.getDescription());
                                                                                            foodDetails.put("fRestaurant", food.getRestaurant());
                                                                                            foodDetails.put("fPrice", food.getPrice());
                                                                                            foodDetails.put("fQuantity", food.getQuantity());
                                                                                            foodDetails.put("fTotal", food.getTotal());
                                                                                            foodDetails.put("fDiscount", food.getDiscount());
                                                                                            foodDetails.put("fDiscountDesc", food.getDiscountdescription());
                                                                                            foodDetails.put("fTimestamp", food.getTimestamp());
                                                                                            foodDetails.put("fUid", food.getUid());
                                                                                            foodDetails.put("fImage", food.getFoodimage());

                                                                                            foodOrdersRef.add(foodDetails)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(DocumentReference documentReference) {
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
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {

                                                                                        }
                                                                                    });

                                                                        } else {
                                                                            // The restaurant does not have the desired food item
                                                                            // Handle the case when the desired food is not found in this restaurant
                                                                        }
                                                                    } else {
                                                                        // Handle exceptions that may occur while fetching the food document
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    } else {
                                                        // Handle exceptions that may occur while fetching the restaurants collection
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
            textView.setText(String.format("TOTAL COST:UGX %s", totalPrice));
            textView.requestLayout();
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