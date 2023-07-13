package com.example.budgetfoods.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetfoods.Admin.AdminMain;
import com.example.budgetfoods.Models.Users;
import com.example.budgetfoods.R;
import com.example.budgetfoods.Student.MainActivity;
import com.example.budgetfoods.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding activityLoginBinding;
    Button btnlogin;
    TextView forgotpass, signup;
    private FirebaseAuth mAuth;
    private static final String TAG = "LOGIN";
    FirebaseFirestore db;
    DocumentReference userRef;
    ProgressBar progressBar;

    LinearLayout linearLayout;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        initViews(activityLoginBinding);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(getApplicationContext(), StudentSignup.class);
                x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(x);
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(getApplicationContext(), RecoverPassword.class);
                x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(x);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();

                email = activityLoginBinding.username.getText().toString();
                password = activityLoginBinding.password.getText().toString();

                if (!validateFields()) {
                    // Handle validation errors
                    return;
                }
                //start of progress bar code
                progressBar.setIndeterminate(true);
                progressBar.setVisibility(View.VISIBLE);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (progressBar.getParent() != null) {
                    ((ViewGroup) progressBar.getParent()).removeView(progressBar);
                }

                linearLayout.addView(progressBar, layoutParams);
                //end of progress bar code

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String uid = mAuth.getCurrentUser().getUid();
                                makeOnline(uid);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void initViews(ActivityLoginBinding activityLoginBinding) {
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleLarge);
        btnlogin = activityLoginBinding.loginbtn;
        forgotpass = activityLoginBinding.textView2;
        signup = activityLoginBinding.textView3;
        linearLayout = activityLoginBinding.loginlinlayout;
    }

    private boolean validateFields() {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        if (TextUtils.isEmpty(email)) {
            activityLoginBinding.username.setError("Email field can't be empty");
            return false;
        } else if (!email.matches(emailRegex)) {
            activityLoginBinding.username.setError("Invalid email format");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            activityLoginBinding.password.setError("Password field can't be empty");
            return false;
        } else if (!password.matches(passwordRegex)) {
            activityLoginBinding.password.setError("Password must contain at least 8 characters including one uppercase letter, one lowercase letter, one digit, and one special character");
            return false;
        }

        return true;
    }

    private void makeOnline(String uid) {
        userRef = db.collection("users").document(uid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        userRef.update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkUserType(uid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        Users userProfile = snapshot.toObject(Users.class);
                        if (userProfile != null) {
                            String accountType = userProfile.getAccounttype();

                            if (accountType.equals("Student")) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                 startActivity(new Intent(getApplicationContext(), AdminMain.class));
                            }
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
