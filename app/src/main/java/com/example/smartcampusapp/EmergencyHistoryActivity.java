package com.example.smartcampusapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EmergencyHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EmergencyHistoryAdapter adapter;
    ArrayList<EmergencyLog> list = new ArrayList<>();
    SafetyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_history);

        recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EmergencyHistoryAdapter(list);
        recyclerView.setAdapter(adapter);

        dbHelper = new SafetyDatabaseHelper(this);

        loadEmergencyHistory();
    }

    private void loadEmergencyHistory() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM emergency_logs ORDER BY id DESC", null
        );

        list.clear();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                double lat = cursor.getDouble(1);
                double lng = cursor.getDouble(2);
                String time = cursor.getString(3);

                list.add(new EmergencyLog(id, lat, lng, time));
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
