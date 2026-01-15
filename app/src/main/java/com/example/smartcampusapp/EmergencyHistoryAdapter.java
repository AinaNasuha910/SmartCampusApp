package com.example.smartcampusapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class EmergencyHistoryAdapter
        extends RecyclerView.Adapter<EmergencyHistoryAdapter.ViewHolder> {

    List<EmergencyLog> list;

    public EmergencyHistoryAdapter(List<EmergencyLog> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtLocation, txtTime;

        public ViewHolder(View itemView) {
            super(itemView);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }

    @NonNull
    @Override
    public EmergencyHistoryAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergency_log, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        EmergencyLog log = list.get(position);

        holder.txtLocation.setText(
                "Lat: " + log.latitude + " , Lng: " + log.longitude
        );

        String time = DateFormat.getDateTimeInstance()
                .format(new Date(Long.parseLong(log.timestamp)));

        holder.txtTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

