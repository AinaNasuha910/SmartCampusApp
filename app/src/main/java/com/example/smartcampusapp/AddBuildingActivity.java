package com.example.smartcampusapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddBuildingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_building);

        EditText editName = findViewById(R.id.editName);
        EditText editDesc = findViewById(R.id.editDesc);
        EditText editLat = findViewById(R.id.editLat);
        EditText editLng = findViewById(R.id.editLng);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString();
            String desc = editDesc.getText().toString();
            String latStr = editLat.getText().toString();
            String lngStr = editLng.getText().toString();

            // Simple Validation
            if (name.isEmpty() || latStr.isEmpty() || lngStr.isEmpty()) {
                Toast.makeText(this, "Please fill in Name and GPS", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the Entity object
            BuildingEntity newBuilding = new BuildingEntity();
            newBuilding.name = name;
            newBuilding.description = desc;
            newBuilding.latitude = Double.parseDouble(latStr);
            newBuilding.longitude = Double.parseDouble(lngStr);

            // Save to Database in a background thread
            new Thread(() -> {
                AppDatabase.getInstance(this).buildingDao().insertBuilding(newBuilding);

                // Close this screen and go back to the list
                runOnUiThread(() -> {
                    Toast.makeText(this, "Building Added!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });
    }
}