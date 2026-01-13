package com.example.smartcampusapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.SearchView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class BuildingsActivity extends AppCompatActivity {
    private GridView gridView;
    private BuildingAdapter adapter;
    private List<BuildingEntity> allBuildings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        gridView = findViewById(R.id.buildingGridView);
        FloatingActionButton fab = findViewById(R.id.fabAddBuilding);

        fab.setOnClickListener(v -> startActivity(new Intent(this, AddBuildingActivity.class)));

        // CRUD: UPDATE & DELETE Trigger
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            BuildingEntity selected = adapter.getItem(position);
            String[] options = {"Edit Building", "Delete Building"};

            new AlertDialog.Builder(this)
                    .setTitle(selected.name)
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) { // Edit
                            Intent intent = new Intent(this, AddBuildingActivity.class);
                            intent.putExtra("BUILDING_ID", selected.id);
                            startActivity(intent);
                        } else { // Delete
                            showDeleteConfirm(selected);
                        }
                    }).show();
            return true;
        });

        loadBuildings();
    }

    private void loadBuildings() {
        new Thread(() -> {
            allBuildings = AppDatabase.getInstance(this).buildingDao().getAllBuildings();
            runOnUiThread(() -> {
                adapter = new BuildingAdapter(this, new ArrayList<>(allBuildings));
                gridView.setAdapter(adapter);
            });
        }).start();
    }

    private void showDeleteConfirm(BuildingEntity building) {
        new AlertDialog.Builder(this)
                .setMessage("Delete " + building.name + "?")
                .setPositiveButton("Yes", (d, w) -> new Thread(() -> {
                    AppDatabase.getInstance(this).buildingDao().deleteBuilding(building);
                    loadBuildings();
                }).start()).setNegativeButton("No", null).show();
    }

    @Override
    protected void onResume() { super.onResume(); loadBuildings(); }
}