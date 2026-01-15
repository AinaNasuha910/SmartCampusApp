package com.example.smartcampusapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StepHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StepHistoryAdapter adapter;
    ArrayList<StepHistory> list = new ArrayList<>();
    SafetyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_history);

        recyclerView = findViewById(R.id.recyclerViewSteps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StepHistoryAdapter(list);
        recyclerView.setAdapter(adapter);

        dbHelper = new SafetyDatabaseHelper(this);

        loadStepHistory();
    }

    private void loadStepHistory() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM step_history ORDER BY id DESC", null
        );

        list.clear();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String date = cursor.getString(1);
                int steps = cursor.getInt(2);

                list.add(new StepHistory(id, date, steps));
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
