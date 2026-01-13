package com.example.smartcampusapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddBuildingActivity extends AppCompatActivity {
    private int buildingId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_building);

        EditText etName = findViewById(R.id.editName);
        EditText etDesc = findViewById(R.id.editDesc);
        EditText etLat = findViewById(R.id.editLat);
        EditText etLng = findViewById(R.id.editLng);
        Button btnSave = findViewById(R.id.btnSave);

        buildingId = getIntent().getIntExtra("BUILDING_ID", -1);
        if (buildingId != -1) {
            isEditMode = true;
            btnSave.setText("UPDATE");
            new Thread(() -> {
                BuildingEntity b = AppDatabase.getInstance(this).buildingDao().getBuildingById(buildingId);
                runOnUiThread(() -> {
                    if(b != null) {
                        etName.setText(b.name); etDesc.setText(b.description);
                        etLat.setText(String.valueOf(b.latitude)); etLng.setText(String.valueOf(b.longitude));
                    }
                });
            }).start();
        }

        btnSave.setOnClickListener(v -> {
            new Thread(() -> {
                BuildingEntity b = new BuildingEntity();
                b.name = etName.getText().toString();
                b.description = etDesc.getText().toString();
                b.latitude = Double.parseDouble(etLat.getText().toString());
                b.longitude = Double.parseDouble(etLng.getText().toString());

                if (isEditMode) {
                    b.id = buildingId;
                    AppDatabase.getInstance(this).buildingDao().updateBuilding(b);
                } else {
                    AppDatabase.getInstance(this).buildingDao().insertBuilding(b);
                }
                runOnUiThread(() -> finish());
            }).start();
        });
    }
}