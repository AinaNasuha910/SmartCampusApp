package com.example.smartcampusapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StepHistoryAdapter
        extends RecyclerView.Adapter<StepHistoryAdapter.ViewHolder> {

    List<StepHistory> list;

    public StepHistoryAdapter(List<StepHistory> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtDate, txtSteps;

        public ViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtSteps = itemView.findViewById(R.id.txtSteps);
        }
    }

    @NonNull
    @Override
    public StepHistoryAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step_history, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StepHistory history = list.get(position);

        holder.txtDate.setText(history.date);
        holder.txtSteps.setText(history.steps + " steps");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
