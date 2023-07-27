package com.example.budgetfoods.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.budgetfoods.Interface.OnMoveToResDetsListener;
import com.example.budgetfoods.Models.Food;
import com.example.budgetfoods.Models.Restaurant;
import com.example.budgetfoods.NewAdapters.RestaurantAdapter;
import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.ActivityViewRestaurantsBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewRestaurants extends AppCompatActivity {
    ActivityViewRestaurantsBinding activityViewRestaurantsBinding;
    RecyclerView recyclerView;
    RestaurantAdapter restaurantAdapter;
    ArrayList<Restaurant> restaurantList;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityViewRestaurantsBinding = ActivityViewRestaurantsBinding.inflate(getLayoutInflater());
        setContentView(activityViewRestaurantsBinding.getRoot());

        initViews(activityViewRestaurantsBinding);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            uid1 = firebaseUser.getUid();
            retrieveRestaurants();
            //Toast.makeText(getApplicationContext(), "" + uid1, Toast.LENGTH_SHORT).show();
        }

        restaurantAdapter.setOnMoveToResDetsListener(new OnMoveToResDetsListener() {
            @Override
            public void onMoveToDets(Restaurant restaurant, int position) {
                Intent intent = new Intent(getApplicationContext(), AddFood.class);
                intent.putExtra("restaurantModel", restaurant);
                startActivity(intent);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    showConfirmationDialog(viewHolder);
                } else if (direction == ItemTouchHelper.LEFT) {
                    //update the food item
                } else {
                    restaurantAdapter.notifyDataSetChanged(); // Refresh the adapter to undo the swipe action
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void showConfirmationDialog(RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewRestaurants.this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this order?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteOrderItem(viewHolder);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        restaurantAdapter.notifyDataSetChanged(); // Refresh the adapter to undo the swipe action
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteOrderItem(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            Restaurant restaurant = restaurantList.get(position);

            DocumentReference restaurantRef = firestore.collection("users")
                    .document(uid1)
                    .collection("Restaurants")
                    .document(restaurant.getRId());

            // Delete the subcollections (food and orders) first
            Task<Void> deleteSubcollectionsTask = deleteSubcollection(restaurantRef.collection("Food"))
                    .continueWithTask(new Continuation<Void, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                            return deleteSubcollection(restaurantRef.collection("orders"));
                        }
                    });

            // After the subcollections are deleted (or if they don't exist), delete the restaurant document
            deleteSubcollectionsTask.continueWithTask(new Continuation<Void, Task<Void>>() {
                @Override
                public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                    return restaurantRef.delete();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ViewRestaurants.this, "Restaurant and Subcollections Deleted", Toast.LENGTH_SHORT).show();
                    restaurantList.remove(position);
                    restaurantAdapter.notifyItemRemoved(position);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ViewRestaurants.this, "Failed to delete restaurant: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Task<Void> deleteSubcollection(CollectionReference collectionRef) {
        return collectionRef.get().continueWithTask(new Continuation<QuerySnapshot, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<Task<Void>> deleteTasks = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    deleteTasks.add(documentSnapshot.getReference().delete());
                }
                return Tasks.whenAll(deleteTasks);
            }
        });
    }

    private void retrieveRestaurants() {
        CollectionReference medicineRef = firestore.collection("users")
                .document(uid1)
                .collection("Restaurants");
        medicineRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                            restaurantList.add(restaurant);
                        }

                        if (restaurantList.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No Restaurant available", Toast.LENGTH_SHORT).show();
                        }

                        restaurantAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to retrieve restaurants: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initViews(ActivityViewRestaurantsBinding activityViewRestaurantsBinding) {
        recyclerView = activityViewRestaurantsBinding.recyclerViewRestaurants;
        restaurantList = new ArrayList<>();
        restaurantAdapter = new RestaurantAdapter(this, restaurantList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(restaurantAdapter);
        restaurantAdapter.notifyDataSetChanged();
    }
}