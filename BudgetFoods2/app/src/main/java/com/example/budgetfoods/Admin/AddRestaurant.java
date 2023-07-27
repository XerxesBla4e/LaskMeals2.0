package com.example.budgetfoods.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.budgetfoods.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.example.budgetfoods.databinding.AddRestaurantBinding;

import android.widget.Toast;

import com.example.budgetfoods.Authentication.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddRestaurant extends AppCompatActivity {

    AddRestaurantBinding activityAddFoodBinding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    Uri uri;
    Button addfoodbtn;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String name, description, restaurant;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    private static final int MEDICINE_ITEM_IMAGE_CODE = 440;
    FirebaseUser firebaseUser;
    ImageView imageView;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAddFoodBinding = AddRestaurantBinding.inflate(getLayoutInflater());
        setContentView(activityAddFoodBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        } else {
            startActivity(new Intent(AddRestaurant.this, LoginActivity.class));
            finish();
        }

        initViews();
        setListeners();

        addfoodbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateFields()) {
                    return;
                } else {
                    progressBar.setIndeterminate(true);
                    progressBar.setVisibility(View.VISIBLE);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (progressBar.getParent() != null) {
                        ((ViewGroup) progressBar.getParent()).removeView(progressBar);
                    }
                    linearLayout.addView(progressBar, layoutParams);

                    uploadFood();
                }
            }
        });
    }

    private void initViews() {
        addfoodbtn = activityAddFoodBinding.addFoodButton;
        imageView = activityAddFoodBinding.foodImageView;
        linearLayout = activityAddFoodBinding.linLayoutFood;
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void setListeners() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFoodImage();
            }
        });
    }

    private void pickFoodImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Restaurant Cover"), MEDICINE_ITEM_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == MEDICINE_ITEM_IMAGE_CODE && data != null && data.getData() != null) {
            uri = data.getData();
            imageView.setImageURI(uri);
        }
    }

    private void uploadFood() {
        final String timestamp = String.valueOf(System.currentTimeMillis());
        name = activityAddFoodBinding.fnameEditText.getText().toString();
        description = activityAddFoodBinding.nameEditDescription.getText().toString();
        restaurant = activityAddFoodBinding.nameEditLocation.getText().toString();

        if (uri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("restaurantname", name);
            hashMap.put("description", description);
            hashMap.put("university", restaurant);
            hashMap.put("RId", timestamp);
            hashMap.put("ratings", 0.0);
            hashMap.put("totalratings", 0);
            hashMap.put("timestamp", timestamp);
            hashMap.put("Uid", firebaseAuth.getUid());
            hashMap.put("image", "");

            DocumentReference userRef = firestore.collection("users").document(uid);
            userRef.collection("Restaurants").document(timestamp).set(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddRestaurant.this, "Restaurant Added...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddRestaurant.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            StorageReference filepath = storageReference.child("imagePost").child(timestamp);
            UploadTask uploadTask = filepath.putFile(uri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            String imageUrl = downloadUri.toString();
                            uploadFoodDataWithImage(timestamp, imageUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            handleUploadFailure(e);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    handleUploadFailure(e);
                }
            });
        }
    }

    private void uploadFoodDataWithImage(String timestamp, String imageUrl) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("restaurantname", name);
        hashMap.put("description", description);
        hashMap.put("university", restaurant);
        hashMap.put("RId", timestamp);
        hashMap.put("ratings", 0.0);
        hashMap.put("totalratings", 0);
        hashMap.put("timestamp", timestamp);
        hashMap.put("Uid", firebaseAuth.getUid());
        hashMap.put("image", imageUrl);

        DocumentReference userRef = firestore.collection("users").document(uid);
        userRef.collection("Restaurants").document(timestamp).set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddRestaurant.this, "Restaurant Added...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddRestaurant.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleUploadFailure(Exception e) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(AddRestaurant.this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public boolean validateFields() {
        name = activityAddFoodBinding.fnameEditText.getText().toString();
        description = activityAddFoodBinding.nameEditDescription.getText().toString();
        restaurant = activityAddFoodBinding.nameEditLocation.getText().toString();

        if (TextUtils.isEmpty(name)) {
            activityAddFoodBinding.fnameEditText.setError("Please insert restaurant name");
            return false;
        }
        if (TextUtils.isEmpty(description)) {
            activityAddFoodBinding.nameEditDescription.setError("Please description");
            return false;
        }
        if (TextUtils.isEmpty(restaurant)) {
            activityAddFoodBinding.nameEditLocation.setError("Please insert university closest");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AdminMain.class);
        startActivity(intent);
        finish();
    }
}
