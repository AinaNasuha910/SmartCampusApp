package com.example.smartcampusapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button; // Add this
import android.widget.TextView;
import java.util.List;

public class BuildingAdapter extends ArrayAdapter<BuildingEntity> {

    public BuildingAdapter(Context context, List<BuildingEntity> buildings) {
        super(context, 0, buildings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_building, parent, false);
        }

        BuildingEntity building = getItem(position);

        TextView name = convertView.findViewById(R.id.txtBuildingName);
        TextView desc = convertView.findViewById(R.id.txtBuildingDesc);
        Button btnNavigate = convertView.findViewById(R.id.btnNavigate); // Link the button from XML

        if (building != null) {
            name.setText(building.name);
            desc.setText(building.description);

            // --- NAVIGATION LOGIC (The Sensor Part) ---
            // --- UPDATED NAVIGATION LOGIC ---
            btnNavigate.setOnClickListener(v -> {
                double lat = building.latitude;
                double lng = building.longitude;
                String label = building.name;

                // This URI format:
                // geo:lat,lng -> center the map
                // ?q=lat,lng -> drop a pin at these coordinates
                // (label) -> text to show on the pin
                String uriString = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + Uri.encode(label) + ")";
                Uri gmmIntentUri = Uri.parse(uriString);

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                // Check if there is an app to handle this intent
                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    getContext().startActivity(mapIntent);
                } else {
                    // Fallback: Just try to start without package check
                    getContext().startActivity(mapIntent);
                }
            });
        }

        return convertView;
    }
}