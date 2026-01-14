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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
    }

    private final ArrayList<HistoryItem> list;
    private final OnItemClickListener listener;

    public HistoryAdapter(ArrayList<HistoryItem> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_grid, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        HistoryItem item = list.get(position);

        h.historyName.setText(item.name);
        h.historyLocation.setText(item.location);
        h.historyDateTime.setText(item.date + " • " + item.time);
        h.historyCode.setText("Code: " + item.claimCode);

        // EDIT: Load gambar dari URI
        if (!TextUtils.isEmpty(item.photoUri)) {
            try {
                h.historyPhoto.setImageURI(Uri.parse(item.photoUri));
            } catch (Exception e) {
                h.historyPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            h.historyPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView historyPhoto;
        TextView historyName, historyLocation, historyDateTime, historyCode;

        VH(@NonNull View itemView) {
            super(itemView);
            historyPhoto = itemView.findViewById(R.id.historyPhoto);
            historyName = itemView.findViewById(R.id.historyName);
            historyLocation = itemView.findViewById(R.id.historyLocation);
            historyDateTime = itemView.findViewById(R.id.historyDateTime);
            historyCode = itemView.findViewById(R.id.historyCode);
        }
    }
}
