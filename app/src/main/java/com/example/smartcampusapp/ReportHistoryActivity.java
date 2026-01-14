package com.example.smartcampusapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReportHistoryActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    private RecyclerView recyclerHistory;
    private TextView emptyHistory;

    private final ArrayList<HistoryItem> data = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.HistoryRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        recyclerHistory = findViewById(R.id.recyclerHistory);
        emptyHistory = findViewById(R.id.emptyHistory);

        // Grid macam gambar awak (2 column)
        recyclerHistory.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new HistoryAdapter(data, item -> {
            Intent intent = new Intent(ReportHistoryActivity.this, EditReportActivity.class);
            intent.putExtra("report_id", item.id);
            startActivity(intent);
        });
        recyclerHistory.setAdapter(adapter);

        loadHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        String username = sp.getString("username", "");

        if (TextUtils.isEmpty(username)) {
            emptyHistory.setText("Session not found. Please login again.");
            emptyHistory.setVisibility(View.VISIBLE);
            recyclerHistory.setVisibility(View.GONE);
            return;
        }

        data.clear();
        data.addAll(dbHelper.getUserUnclaimedHistory(username));
        adapter.notifyDataSetChanged();

        if (data.isEmpty()) {
            emptyHistory.setText("No unclaimed reports yet (your reports).");
            emptyHistory.setVisibility(View.VISIBLE);
            recyclerHistory.setVisibility(View.GONE);
        } else {
            emptyHistory.setVisibility(View.GONE);
            recyclerHistory.setVisibility(View.VISIBLE);
        }
    }
}

