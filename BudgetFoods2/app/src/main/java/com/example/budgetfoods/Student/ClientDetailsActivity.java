package com.example.budgetfoods.Student;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.ActivityClientDetailsBinding;

public class ClientDetailsActivity extends AppCompatActivity {
    ActivityClientDetailsBinding activityClientDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityClientDetailsBinding = ActivityClientDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityClientDetailsBinding.getRoot());
    }
}