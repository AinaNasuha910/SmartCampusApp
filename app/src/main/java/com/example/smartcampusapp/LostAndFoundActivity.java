package com.example.smartcampusapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class LostAndFoundActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    private RecyclerView recycler;
    private TextView emptyState;

    private Button btnUnclaimed, btnClaimed;

    private FloatingActionButton AddReport, fabHistory;

    private final ArrayList<ReportItem> data = new ArrayList<>();
    private ReportAdapter adapter;

    private String currentStatus = "Unclaimed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lost_and_found);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LostAndFound), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        recycler = findViewById(R.id.LostFoundItems);
        emptyState = findViewById(R.id.EmptyState);

        btnUnclaimed = findViewById(R.id.btnUnclaimed);
        btnClaimed = findViewById(R.id.btnClaimed);

        AddReport = findViewById(R.id.AddReport);
        fabHistory = findViewById(R.id.fabHistory);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter(data, item -> {
            Intent intent = new Intent(LostAndFoundActivity.this, LostFoundItemActivity.class);
            intent.putExtra("report_id", item.id);
            startActivity(intent);
        });
        recycler.setAdapter(adapter);

        btnUnclaimed.setOnClickListener(v -> {
            currentStatus = "Unclaimed";
            loadReports(currentStatus);
            setSelectedTabUI(true);
        });

        btnClaimed.setOnClickListener(v -> {
            currentStatus = "Claimed";
            loadReports(currentStatus);
            setSelectedTabUI(false);
        });

        AddReport.setOnClickListener(v -> {
            Intent intent = new Intent(LostAndFoundActivity.this, ReportLostItemActivity.class);
            startActivity(intent);
        });

        fabHistory.setOnClickListener(v -> {
            Intent intent = new Intent(LostAndFoundActivity.this, ReportHistoryActivity.class);
            startActivity(intent);
        });

        loadReports(currentStatus);
        setSelectedTabUI(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports(currentStatus);
    }

    private void loadReports(String status) {
        data.clear();
        data.addAll(dbHelper.getReportsByStatus(status));
        adapter.notifyDataSetChanged();

        if (data.isEmpty()) {
            emptyState.setText("No " + status.toLowerCase() + " items yet.");
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);
        }
    }

    private void setSelectedTabUI(boolean unclaimedSelected) {
        if (unclaimedSelected) {
            btnUnclaimed.setBackgroundResource(R.drawable.bg_button_black);
            btnUnclaimed.setTextColor(0xFFFFFFFF);

            btnClaimed.setBackgroundResource(R.drawable.bg_button_outline);
            btnClaimed.setTextColor(0xFF000000);
        } else {
            btnClaimed.setBackgroundResource(R.drawable.bg_button_black);
            btnClaimed.setTextColor(0xFFFFFFFF);

            btnUnclaimed.setBackgroundResource(R.drawable.bg_button_outline);
            btnUnclaimed.setTextColor(0xFF000000);
        }
    }
}

