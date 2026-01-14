package com.example.smartcampusapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AttendanceAdapter extends ArrayAdapter<Attendance> {

    Activity context;
    List<Attendance> list;

    public AttendanceAdapter(Activity context, List<Attendance> list) {
        super(context, R.layout.item_attendance, list);
        this.context = context;
        this.list = list;
    }

    // ViewHolder for better performance
    static class ViewHolder {
        TextView txtName, txtStudentId, txtClassCode, txtStatus;
        ImageView imgStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_attendance, parent, false);

            holder = new ViewHolder();
            holder.txtName = convertView.findViewById(R.id.txtName);
            holder.txtStudentId = convertView.findViewById(R.id.txtStudentId);
            holder.txtClassCode = convertView.findViewById(R.id.txtClassCode);
            holder.txtStatus = convertView.findViewById(R.id.txtStatus);
            holder.imgStatus = convertView.findViewById(R.id.imgStatus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Attendance a = list.get(position);

        holder.txtName.setText("Name: " + a.name);
        holder.txtStudentId.setText("Student ID: " + a.studentId);
        holder.txtClassCode.setText("Class Code: " + a.classCode);
        holder.txtStatus.setText("Status: " + a.status);

        // Dynamic status icon
        if (a.status.equalsIgnoreCase("Present")) {
            holder.imgStatus.setImageResource(R.drawable.present);
        } else if (a.status.equalsIgnoreCase("Late")) {
            holder.imgStatus.setImageResource(R.drawable.late);
        } else if (a.status.equalsIgnoreCase("Absent")) {
            holder.imgStatus.setImageResource(R.drawable.absent);
        } else {
            holder.imgStatus.setImageResource(R.drawable.present);
        }

        return convertView;
    }
}