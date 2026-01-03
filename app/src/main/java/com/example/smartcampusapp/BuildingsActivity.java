package com.example.smartcampusapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.net.Uri;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BuildingsActivity extends AppCompatActivity {

    ListView buildingListView;
    SearchView searchView;

    List<Building> buildingList;   // original list
    List<Building> filteredList;   // filtered list
    BuildingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        // Find views
        buildingListView = findViewById(R.id.buildingListView);
        searchView = findViewById(R.id.searchView);

        // Original building data
        buildingList = new ArrayList<>();
        buildingList.add(new Building("Faculty of Engineering", "Main engineering building"));
        buildingList.add(new Building("Faculty of Computing", "Computing and IT programs"));
        buildingList.add(new Building("Perpustakaan Universiti", "University library"));
        buildingList.add(new Building("Dewan Astaka", "Main hall for events"));

        // Copy to filtered list
        filteredList = new ArrayList<>(buildingList);

        // Adapter
        adapter = new BuildingAdapter(this, filteredList);
        buildingListView.setAdapter(adapter);

        // Search filter logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList.clear();

                for (Building b : buildingList) {
                    if (b.name.toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(b);
                    }
                }

                adapter.notifyDataSetChanged();
                return true;
            }
        });

        // Click building → open map
        buildingListView.setOnItemClickListener((parent, view, position, id) -> {

            Building selected = filteredList.get(position);

            double lat = 0;
            double lng = 0;

            switch (selected.name) {
                case "Faculty of Engineering":
                    lat = 3.2148;
                    lng = 101.7290;
                    break;

                case "Faculty of Computing":
                    lat = 3.2155;
                    lng = 101.7278;
                    break;

                case "Perpustakaan Universiti":
                    lat = 3.2150;
                    lng = 101.7285;
                    break;

                case "Dewan Astaka":
                    lat = 3.2139;
                    lng = 101.7296;
                    break;
            }

            Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + selected.name + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapIntent);
        });
    }
}
