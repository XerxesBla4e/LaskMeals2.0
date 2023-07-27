package com.example.budgetfoods.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.budgetfoods.Authentication.LoginActivity;
import com.example.budgetfoods.Authentication.UpdateProfile;
import com.example.budgetfoods.Interface.OnMoveToResDetsListener;
import com.example.budgetfoods.Models.Restaurant;
import com.example.budgetfoods.NewAdapters.RestaurantAdapter1;
import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityHomeBinding activityHomeBinding;
    // private FoodViewModel foodViewModel;
    RecyclerView recyclerView;
    RestaurantAdapter1 restaurantAdapter1;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    List<Restaurant> restaurantList;
    private LocationManager locationManager;
    private LocationListener locationListener;
    BottomNavigationView bottomNavigationView;
    FirebaseUser firebaseUser;
    String uid1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        initViews(activityHomeBinding);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid1 = user.getUid();
                    retrieveMenuRestaurants();
                    requestLocationUpdates();
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

        initBottomNavView();

        restaurantAdapter1.setOnMoveToResDetsListener(new OnMoveToResDetsListener() {
            @Override
            public void onMoveToDets(Restaurant restaurant, int position) {
                Intent intent = new Intent(getApplicationContext(), RestaurantFoods.class);
                intent.putExtra("restaurantModel", restaurant);
                startActivity(intent);
            }
        });
      /*  foodViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance((Application) this.getApplicationContext())).get(FoodViewModel.class);
        adapter.setOnAddToCartClickListener(new OnAddToCartListener() {
            @Override
            public void onAddToCartClick(Food food, int position) {
                AddToCart(food);
            }
        });*/


    }

    private void requestLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateUserLocation(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

      /*  popularFoodadapter.setOnAddToCartClickListener(new OnAddToCartListener() {
            @Override
            public void onAddToCartClick(Food food, int position) {
                Food food2 = new Food(food.getFoodname(), food.getDescription(), food.getRestaurant(),
                        food.getPrice(), food.getFId(), food.getTimestamp(), food.getUid(), food.getDiscount(), food.getDescription(),
                        food.getFoodimage());
                foodViewModel.insert(food2);
            }
        });*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void updateUserLocation(double latitude, double longitude) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentRef = firestore.collection("users").document(uid1);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("latitude", "" + latitude);
        updateData.put("longitude", "" + longitude);

        documentRef.update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update successful
                        // Toast.makeText(getApplicationContext(), "Location updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //  Log.d(TAG, "" + e);
                        // Handle any errors
                        // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initViews(ActivityHomeBinding activityHomeBinding) {
        // Set Menu Recycler
        restaurantList = new ArrayList<>();
        restaurantAdapter1 = new RestaurantAdapter1(getApplicationContext(), restaurantList);
        recyclerView = activityHomeBinding.recyclerViewpopv;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(restaurantAdapter1);
        // Use GridLayoutManager for SetMenuRecycler with 2 columns
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        // Notify the adapter after any changes in the data
        restaurantAdapter1.notifyDataSetChanged();

        bottomNavigationView = activityHomeBinding.bottomNavgation;
    }

    private void initBottomNavView() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {
                if (item.getItemId() == R.id.nav_cart) {
                    Intent x = new Intent(MainActivity.this, CartActivity.class);
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x);
                } else if (item.getItemId() == R.id.nav_prof) {
                    Intent x6 = new Intent(MainActivity.this, UpdateProfile.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
                } else if (item.getItemId() == R.id.nav_logout) {
                    makeOffline();
                    firebaseAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else if (item.getItemId() == R.id.nav_orders) {
                    Intent x4 = new Intent(MainActivity.this, OrdersActivity.class);
                    x4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x4);
                }
            }
        });
    }

    private void retrieveMenuRestaurants() {
        String desiredAccountType = "Admin";

        CollectionReference usersRef = firestore.collection("users");

        usersRef.whereEqualTo("accounttype", desiredAccountType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            restaurantList = new ArrayList<>(); // New list to hold the retrieved restaurants

                            for (QueryDocumentSnapshot userDocument : task.getResult()) {
                                String userId = userDocument.getId();

                                CollectionReference userRestaurantsRef = usersRef.document(userId).collection("Restaurants");

                                userRestaurantsRef
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot restaurantDocument : task.getResult()) {
                                                        // Retrieve the restaurant data from the document
                                                        Restaurant restaurant = restaurantDocument.toObject(Restaurant.class);

                                                        // Add the Restaurant object to the list
                                                        restaurantList.add(restaurant);
                                                    }

                                                    // Update the adapter with the retrieved restaurant list
                                                    restaurantAdapter1.updateRestaurantList(restaurantList);
                                                } else {
                                                    Log.d("TAG", "Error getting menu restaurants: " + task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d("TAG", "Error getting users: " + task.getException());
                        }
                    }
                });
    }


    private void makeOffline() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentRef = firestore.collection("users").document(uid1);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("online", "false");

        documentRef.update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update successful
                        //Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }
}
