package com.example.smartcampusapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StudentSafety extends AppCompatActivity {

    Button btnEmergency, btnSteps, btnHistory,btnStepHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_safety);

        btnEmergency = findViewById(R.id.btnEmergency);
        btnSteps = findViewById(R.id.btnSteps);
        btnHistory = findViewById(R.id.btnHistory);
        btnStepHistory = findViewById(R.id.btnStepHistory);

        // Go to Emergency Screen
        btnEmergency.setOnClickListener(v -> {
            Intent intent = new Intent(StudentSafety.this, EmergencyActivity.class);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(StudentSafety.this, EmergencyHistoryActivity.class));
        });


        // Go to Step Counter Screen
        btnSteps.setOnClickListener(v -> {
            Intent intent = new Intent(StudentSafety.this, StepCounterActivity.class);
            startActivity(intent);
        });

        btnStepHistory.setOnClickListener(v -> {
            startActivity(new Intent(StudentSafety.this, StepHistoryActivity.class));
        });

    }
}
