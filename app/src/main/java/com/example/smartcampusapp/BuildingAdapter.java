package com.example.smartcampusapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BuildingAdapter extends ArrayAdapter<Building> {

    public BuildingAdapter(Context context, List<Building> buildings) {
        super(context, 0, buildings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_building, parent, false);
        }

        Building building = getItem(position);

        TextView name = convertView.findViewById(R.id.txtBuildingName);
        TextView desc = convertView.findViewById(R.id.txtBuildingDesc);

        name.setText(building.name);
        desc.setText(building.description);

        return convertView;
    }
}
