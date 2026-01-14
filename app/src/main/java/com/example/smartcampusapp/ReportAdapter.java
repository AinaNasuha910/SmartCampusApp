package com.example.smartcampusapp;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(ReportItem item);
    }

    private final ArrayList<ReportItem> list;
    private final OnItemClickListener listener;

    public ReportAdapter(ArrayList<ReportItem> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ReportItem item = list.get(position);

        h.txtName.setText(item.name);
        h.txtLocation.setText(item.location);
        h.txtTime.setText(item.time);

        // Load photo safely
        if (!TextUtils.isEmpty(item.photoUri)) {
            try {
                h.imgPhoto.setImageURI(Uri.parse(item.photoUri));
            } catch (Exception e) {
                h.imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            h.imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Clickable card
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView txtName, txtLocation, txtTime;

        VH(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            txtName = itemView.findViewById(R.id.txtName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
