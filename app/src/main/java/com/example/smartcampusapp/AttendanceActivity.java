package com.example.smartcampusapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class AttendanceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SearchView searchView;

    ArrayList<Attendance> attendanceList = new ArrayList<>();
    ArrayList<Attendance> filteredList = new ArrayList<>();

    AttendanceRecyclerAdapter adapter;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter with click listener for edit/delete
        adapter = new AttendanceRecyclerAdapter(filteredList, attendance -> {
            showEditDeleteDialog(attendance);
        });
        recyclerView.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference("attendance");

        // Load Firebase data
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendanceList.clear();
                filteredList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Attendance a = data.getValue(Attendance.class);
                    if (a != null) {
                        a.key = data.getKey();   // Save Firebase key for edit/delete
                        attendanceList.add(a);
                        filteredList.add(a);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AttendanceActivity.this,
                        "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        // Search filter
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterByClassCode(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterByClassCode(newText);
                return true;
            }
        });
    }

    // Filter by Class Code
    private void filterByClassCode(String keyword) {
        filteredList.clear();

        for (Attendance a : attendanceList) {
            if (a.classCode != null &&
                    a.classCode.toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(a);
            }
        }

        adapter.notifyDataSetChanged();
    }

    // Edit/Delete Dialog
    private void showEditDeleteDialog(Attendance attendance) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete Attendance");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_attendance, null);
        builder.setView(view);

        Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);

        // Status options
        String[] statusOptions = {"Present", "Late", "Absent"};

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusOptions
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Set current status
        int currentIndex = 0;
        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equalsIgnoreCase(attendance.status)) {
                currentIndex = i;
                break;
            }
        }
        spinnerStatus.setSelection(currentIndex);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newStatus = spinnerStatus.getSelectedItem().toString();

            DatabaseReference updateRef = FirebaseDatabase.getInstance()
                    .getReference("attendance")
                    .child(attendance.key);

            updateRef.child("status").setValue(newStatus);
        });

        builder.setNegativeButton("Delete", (dialog, which) -> {
            DatabaseReference deleteRef = FirebaseDatabase.getInstance()
                    .getReference("attendance")
                    .child(attendance.key);

            deleteRef.removeValue();
        });

        builder.setNeutralButton("Cancel", null);
        builder.show();
    }

}