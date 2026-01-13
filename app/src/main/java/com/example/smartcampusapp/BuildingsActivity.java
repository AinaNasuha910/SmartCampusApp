package com.example.smartcampusapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class BuildingsActivity extends AppCompatActivity {

    private ListView listView;
    private BuildingAdapter adapter;
    private List<BuildingEntity> allBuildings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        listView = findViewById(R.id.buildingListView);
        SearchView searchView = findViewById(R.id.searchView);
        FloatingActionButton fab = findViewById(R.id.fabAddBuilding);

        // 1. "Plus" Button logic (CREATE)
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBuildingActivity.class);
            startActivity(intent);
        });

        // 2. Long Click logic (DELETE)
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            BuildingEntity selected = adapter.getItem(position);
            showDeleteDialog(selected);
            return true;
        });

        // 3. Search logic (READ)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    // Refresh list every time we come back to this screen
    @Override
    protected void onResume() {
        super.onResume();
        loadBuildings();
    }

    private void loadBuildings() {
        new Thread(() -> {
            // Fetch fresh data from Database
            allBuildings = AppDatabase.getInstance(this).buildingDao().getAllBuildings();

            // Update the UI
            runOnUiThread(() -> {
                if (adapter == null) {
                    adapter = new BuildingAdapter(this, new ArrayList<>(allBuildings));
                    listView.setAdapter(adapter);
                } else {
                    adapter.clear();
                    adapter.addAll(allBuildings);
                    adapter.notifyDataSetChanged(); // Tells the screen to redraw
                }
            });
        }).start();
    }

    private void filterList(String text) {
        List<BuildingEntity> filteredList = new ArrayList<>();
        for (BuildingEntity building : allBuildings) {
            if (building.name.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(building);
            }
        }
        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(BuildingEntity building) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Building")
                .setMessage("Are you sure you want to delete " + building.name + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // RUN ON BACKGROUND THREAD
                    new Thread(() -> {
                        // 1. Delete from Room
                        AppDatabase.getInstance(this).buildingDao().deleteBuilding(building);

                        // 2. IMPORTANT: Refresh the list immediately
                        loadBuildings();
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}