package com.example.smartcampusapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceRecyclerAdapter
        extends RecyclerView.Adapter<AttendanceRecyclerAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Attendance attendance);
    }

    List<Attendance> list;
    OnItemClickListener listener;

    public AttendanceRecyclerAdapter(List<Attendance> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtStudentId, txtClassCode, txtStatus;
        ImageView imgStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtStudentId = itemView.findViewById(R.id.txtStudentId);
            txtClassCode = itemView.findViewById(R.id.txtClassCode);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgStatus = itemView.findViewById(R.id.imgStatus);
        }

        public void bind(Attendance a, OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(a));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attendance a = list.get(position);

        holder.txtName.setText("Name: " + a.name);
        holder.txtStudentId.setText("Student ID: " + a.studentId);
        holder.txtClassCode.setText("Class Code: " + a.classCode);
        holder.txtStatus.setText("Status: " + a.status);

        if (a.status.equalsIgnoreCase("Present"))
            holder.imgStatus.setImageResource(R.drawable.present);
        else if (a.status.equalsIgnoreCase("Late"))
            holder.imgStatus.setImageResource(R.drawable.late);
        else if (a.status.equalsIgnoreCase("Absent"))
            holder.imgStatus.setImageResource(R.drawable.absent);

        holder.bind(a, listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

