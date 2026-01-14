package com.example.smartcampusapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalGetImage;

public class AttendanceMain extends AppCompatActivity {

    Button btnScan,btnClassEvent;

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_main);

        btnScan = findViewById(R.id.btnScan);
        btnClassEvent = findViewById(R.id.btnClassEvent);

        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(AttendanceMain.this, QRScannerActivity.class);
            startActivity(intent);
        });
        btnClassEvent.setOnClickListener(v -> {
            startActivity(new Intent(AttendanceMain.this, AttendanceActivity.class));
        });

    }
}