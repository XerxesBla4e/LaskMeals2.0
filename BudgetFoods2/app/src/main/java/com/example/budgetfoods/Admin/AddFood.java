package com.example.budgetfoods.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.budgetfoods.Admin.AdminMain;
import com.example.budgetfoods.Authentication.LoginActivity;
import com.example.budgetfoods.databinding.AddFoodactivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddFood extends AppCompatActivity {

    AddFoodactivityBinding activityAddFoodBinding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    Uri uri;
    Button addfoodbtn;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String name, description, restaurant, price, discount, discountpercent;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    private static final int MEDICINE_ITEM_IMAGE_CODE = 440;
    FirebaseUser firebaseUser;
    ImageView imageView;
    Switch discswitch;
    boolean discavailable;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAddFoodBinding = AddFoodactivityBinding.inflate(getLayoutInflater());
        setContentView(activityAddFoodBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        } else {
            startActivity(new Intent(AddFood.this, LoginActivity.class));
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
        discswitch = activityAddFoodBinding.discountSwitch;
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

        discswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                discavailable = isChecked;
                if (discavailable) {
                    activityAddFoodBinding.discountPriceEditText.setVisibility(View.VISIBLE);
                    activityAddFoodBinding.discountDescriptionEditText.setVisibility(View.VISIBLE);
                } else {
                    activityAddFoodBinding.discountPriceEditText.setVisibility(View.GONE);
                    activityAddFoodBinding.discountDescriptionEditText.setVisibility(View.GONE);
                    discount = "0";
                    discountpercent = "0%";
                }
            }
        });
    }

    private void pickFoodImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Food Image"), MEDICINE_ITEM_IMAGE_CODE);
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
        price = activityAddFoodBinding.nameEditPrice.getText().toString();

        if (discavailable) {
            discount = activityAddFoodBinding.discountPriceEditText.getText().toString();
            discountpercent = activityAddFoodBinding.discountDescriptionEditText.getText().toString();
        } else {
            discount = "0";
            discountpercent = "0";
        }

        if (uri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("foodname", name);
            hashMap.put("description", description);
            hashMap.put("restaurant", restaurant);
            hashMap.put("price", price);
            hashMap.put("fId", timestamp);
            hashMap.put("timestamp", timestamp);
            hashMap.put("Uid", firebaseAuth.getUid());
            hashMap.put("discount", discount);
            hashMap.put("discountdescription", discountpercent);
            hashMap.put("foodimage", "");

            DocumentReference userRef = firestore.collection("users").document(uid);
            userRef.collection("Food").document(timestamp).set(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddFood.this, "Food Added...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddFood.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        hashMap.put("foodname", name);
        hashMap.put("description", description);
        hashMap.put("restaurant", restaurant);
        hashMap.put("price", price);
        hashMap.put("fId", timestamp);
        hashMap.put("timestamp", timestamp);
        hashMap.put("Uid", firebaseAuth.getUid());
        hashMap.put("discount", discount);
        hashMap.put("discountdescription", discountpercent);
        hashMap.put("foodimage", imageUrl);

        DocumentReference userRef = firestore.collection("users").document(uid);
        userRef.collection("Food").document(timestamp).set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddFood.this, "Food Added...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddFood.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleUploadFailure(Exception e) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(AddFood.this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public boolean validateFields() {
        name = activityAddFoodBinding.fnameEditText.getText().toString();
        description = activityAddFoodBinding.nameEditDescription.getText().toString();
        restaurant = activityAddFoodBinding.nameEditLocation.getText().toString();
        price = activityAddFoodBinding.nameEditPrice.getText().toString();

        if (TextUtils.isEmpty(name)) {
            activityAddFoodBinding.fnameEditText.setError("Please insert food name");
            return false;
        }
        if (TextUtils.isEmpty(description)) {
            activityAddFoodBinding.nameEditDescription.setError("Please insert food description");
            return false;
        }
        if (TextUtils.isEmpty(restaurant)) {
            activityAddFoodBinding.nameEditLocation.setError("Please insert restaurant/location");
            return false;
        }
        if (TextUtils.isEmpty(price)) {
            activityAddFoodBinding.nameEditPrice.setError("Please insert food price");
            return false;
        }

        if (discavailable) {
            discount = activityAddFoodBinding.discountPriceEditText.getText().toString();
            discountpercent = activityAddFoodBinding.discountDescriptionEditText.getText().toString();

            if (TextUtils.isEmpty(discount)) {
                activityAddFoodBinding.discountPriceEditText.setError("Please insert Discount Amount");
                return false;
            }

            if (TextUtils.isEmpty(discountpercent)) {
                activityAddFoodBinding.discountDescriptionEditText.setError("Please insert discount description/percentage");
                return false;
            }

            // Validate discountpercent to ensure it contains a percentage symbol (%)
            if (!discountpercent.contains("%")) {
                activityAddFoodBinding.discountDescriptionEditText.setError("Please insert a discount description with a percentage symbol eg. (20%)");
                return false;
            }
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
