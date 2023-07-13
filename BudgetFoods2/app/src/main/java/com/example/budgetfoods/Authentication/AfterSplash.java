package com.example.budgetfoods.Authentication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.ActivityAuthenticationBinding;

public class AfterSplash extends AppCompatActivity {
    ActivityAuthenticationBinding authenticationBinding;
    Button login, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticationBinding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(authenticationBinding.getRoot());

        initViews(authenticationBinding);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x1 = new Intent(getApplicationContext(), LoginActivity.class);
                x1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(x1);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignupSelectionDialog();
            }
        });
    }

    private void initViews(ActivityAuthenticationBinding authenticationBinding) {
        login = authenticationBinding.btnLogin;
        signup = authenticationBinding.btnSign;
    }

    private void showSignupSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Signup Selection");
        String[] options = {"Admin Signup", "Student Signup"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case 0:
                        // Admin Signup
                        Intent x6 = new Intent(getApplicationContext(), AdminSignup.class);
                        x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(x6);
                        break;
                    case 1:
                        // Student Signup
                        Intent x = new Intent(getApplicationContext(), StudentSignup.class);
                        x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(x);
                        break;
                }
            }
        });
        builder.show();
    }

}