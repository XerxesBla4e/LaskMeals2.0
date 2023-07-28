package com.example.budgetfoods.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.budgetfoods.Adapter.OrderAdapter;
import com.example.budgetfoods.Authentication.LoginActivity;
import com.example.budgetfoods.Authentication.UpdateProfile;
import com.example.budgetfoods.Interface.OnMoveToDetsListener;
import com.example.budgetfoods.Models.Order;
import com.example.budgetfoods.R;
import com.example.budgetfoods.Student.MainActivity;
import com.example.budgetfoods.databinding.ActivityAdminMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMain extends AppCompatActivity implements OnMoveToDetsListener {
    ActivityAdminMainBinding activityAdminMainBinding;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;

    private List<Order> orderList;
    private OrderAdapter orderAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    Order orders;
    String uid1;
    RecyclerView recyclerView;
    private LocationManager locationManager;

    private static final String TAG = "Location";
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAdminMainBinding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(activityAdminMainBinding.getRoot());

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
        initViews(activityAdminMainBinding);

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid1 = user.getUid();
                    requestLocationUpdates();
                    fetchOrders();
                } else {
                    startActivity(new Intent(AdminMain.this, LoginActivity.class));
                    finish();
                }
            }
        });

        initBottomNavView();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x0 = new Intent(getApplicationContext(), AddRestaurant.class);
                x0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(x0);
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
                    showConfirmationDialog(viewHolder);
                } else {
                    orderAdapter.notifyDataSetChanged(); // Refresh the adapter to undo the swipe action
                }
            }
        }).attachToRecyclerView(recyclerView);

        orderAdapter.setOnMoveToDetsListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void showConfirmationDialog(RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMain.this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this order?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteOrderItem(viewHolder);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        orderAdapter.notifyDataSetChanged(); // Refresh the adapter to undo the swipe action
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteOrderItem(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            Order order = orderList.get(position);

            String restaurantId = order.getOrderTo(); // Get the restaurantId from the order

            CollectionReference restaurantRef = firestore.collection("users")
                    .document(uid1)
                    .collection("Restaurant");

            DocumentReference orderRef = restaurantRef
                    .document(restaurantId)
                    .collection("orders")
                    .document(order.getOrderID());

            orderRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AdminMain.this, "Order deleted", Toast.LENGTH_SHORT).show();
                            orderList.remove(position);
                            orderAdapter.notifyItemRemoved(position);

                            // Delete the associated food items subcollection
                            CollectionReference foodOrdersRef = orderRef.collection("foodOrders");
                            deleteFoodItems(foodOrdersRef);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminMain.this, "Failed to delete order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Helper method to delete the food items subcollection
    private void deleteFoodItems(CollectionReference foodOrdersRef) {
        foodOrdersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<Task<Void>> deleteTasks = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    deleteTasks.add(documentSnapshot.getReference().delete());
                }

                Tasks.whenAll(deleteTasks)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AdminMain.this, "Associated food items deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminMain.this, "Failed to delete associated food items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }



    @SuppressLint("NotifyDataSetChanged")
    private void initViews(ActivityAdminMainBinding activityAdminMainBinding) {
        bottomNavigationView = activityAdminMainBinding.bottomNavgation;
        floatingActionButton = activityAdminMainBinding.fab;
        recyclerView = activityAdminMainBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList,getApplicationContext());
        recyclerView.setAdapter(orderAdapter);
        orderAdapter.notifyDataSetChanged();
        // Initialize the locationManager here before using it.
    }


    private void fetchOrders() {
        // Assuming the "users" collection contains a document with the current user's ID as the document ID
        DocumentReference currentUserRef = firestore.collection("users").document(uid1);

        currentUserRef.collection("Restaurants") // Assuming "Restaurants" is the subcollection containing restaurant documents
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot restaurantSnapshot : queryDocumentSnapshots) {
                        String restaurantId = restaurantSnapshot.getId(); // Get the restaurantId from the restaurant document

                        CollectionReference ordersCollectionRef = restaurantSnapshot.getReference().collection("orders");
                        ordersCollectionRef.whereEqualTo("orderTo", firebaseAuth.getUid())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    if (!queryDocumentSnapshots1.isEmpty()) {
                                        for (QueryDocumentSnapshot orderSnapshot : queryDocumentSnapshots1) {
                                            Order order = orderSnapshot.toObject(Order.class);
                                            orderList.add(order);
                                        //    Toast.makeText(getApplicationContext(),""+order.getOrderTo(),Toast.LENGTH_SHORT).show();
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

    private void initBottomNavView() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Intent x = new Intent(getApplicationContext(), AdminMain.class);
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x);
                } else if (item.getItemId() == R.id.nav_food) {
                    Intent x6 = new Intent(getApplicationContext(), ViewRestaurants.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
                } else if (item.getItemId() == R.id.nav_logout) {
                    makeOffline();
                    firebaseAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else if (item.getItemId() == R.id.nav_prof) {
                    Intent x6 = new Intent(getApplicationContext(), UpdateProfile.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
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
    public void onMoveToDets(Order order) {
        Intent intent = new Intent(getApplicationContext(), ClientDetails1.class);
        intent.putExtra("ordersModel", order);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

}
