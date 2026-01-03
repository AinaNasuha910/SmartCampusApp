package com.example.smartcampusapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class BuildingsActivity extends AppCompatActivity {

    ListView buildingListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        buildingListView = findViewById(R.id.buildingListView);

        // Simple building list (hardcoded)
        String[] buildings = {
                "Faculty of Engineering",
                "Faculty of Computing",
                "Perpustakaan Universiti",
                "Dewan Astaka"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                buildings
        );

        buildingListView.setAdapter(adapter);

        // Click building (map comes later)
        buildingListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBuilding = buildings[position];

            // For now, just go to MapsActivity later
            Intent intent = new Intent(BuildingsActivity.this, MapsActivity.class);
            intent.putExtra("building_name", selectedBuilding);
            startActivity(intent);
        });
    }
}
