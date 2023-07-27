package com.example.budgetfoods.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.example.budgetfoods.Adapter.ViewFoodAdapter;
import com.example.budgetfoods.Models.Food;
import com.example.budgetfoods.Models.Order;
import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.ActivityViewMyFoodsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewMyFoods extends AppCompatActivity {
    ActivityViewMyFoodsBinding activityViewMyFoodsBinding;
    RecyclerView recyclerView;
    ViewFoodAdapter viewFoodAdapter;
    List<Food> foodList;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityViewMyFoodsBinding = ActivityViewMyFoodsBinding.inflate(getLayoutInflater());
        setContentView(activityViewMyFoodsBinding.getRoot());

        initViews(activityViewMyFoodsBinding);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            uid1 = firebaseUser.getUid();
            retrieveFoods();
            //Toast.makeText(getApplicationContext(), "" + uid1, Toast.LENGTH_SHORT).show();
        }

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
                    viewFoodAdapter.notifyDataSetChanged(); // Refresh the adapter to undo the swipe action
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void showConfirmationDialog(RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewMyFoods.this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this order?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteOrderItem(viewHolder);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        viewFoodAdapter.notifyDataSetChanged(); // Refresh the adapter to undo the swipe action
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteOrderItem(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            Food order = foodList.get(position);

            DocumentReference medicineRef = firestore.collection("users")
                    .document(uid1)
                    .collection("Food")
                    .document(order.getFId());

            medicineRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ViewMyFoods.this, "Food Item Deleted", Toast.LENGTH_SHORT).show();
                            foodList.remove(position);
                            viewFoodAdapter.notifyItemRemoved(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ViewMyFoods.this, "Failed to delete Food Item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void initViews(ActivityViewMyFoodsBinding activityViewMyFoodsBinding) {
        recyclerView = activityViewMyFoodsBinding.recyclerView;
        foodList = new ArrayList<>();
        viewFoodAdapter = new ViewFoodAdapter(getApplicationContext(), foodList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(viewFoodAdapter);
    }

    private void retrieveFoods() {
        CollectionReference medicineRef = firestore.collection("users")
                .document(uid1)
                .collection("Food");
        medicineRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Food medicine = documentSnapshot.toObject(Food.class);
                            foodList.add(medicine);
                        }

                        if (foodList.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No Food available", Toast.LENGTH_SHORT).show();
                        }

                        viewFoodAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to retrieve food items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}