package com.example.smartcampusapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        CardView cardNavigation = findViewById(R.id.cardNavigation);
        cardNavigation.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BuildingsActivity.class);
            startActivity(intent);
        });

        CardView attendanceNavigation = findViewById(R.id.AttendanceNavigation);
        attendanceNavigation.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AttendanceMain.class);
            startActivity(intent);
        });

        CardView lostandfoundNavigation = findViewById(R.id.LostAndFoundNavigation);
        lostandfoundNavigation.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LostAndFoundActivity.class);
            startActivity(intent);
        });



        AppDatabase db = AppDatabase.getInstance(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Check if we already have data so we don't keep adding the same building
                if (db.buildingDao().getAllBuildings().isEmpty()) {
                    BuildingEntity testBuilding = new BuildingEntity();
                    testBuilding.name = "Faculty of Computing";
                    testBuilding.description = "Main hub for IT students";
                    testBuilding.latitude = 3.5432;
                    testBuilding.longitude = 103.4288;

                    db.buildingDao().insertBuilding(testBuilding);
                    System.out.println("DATABASE TEST: First building saved!");
                } else {
                    System.out.println("DATABASE TEST: Data already exists.");
                }
            }
        }).start();
    }
}