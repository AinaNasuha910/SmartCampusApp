package com.example.smartcampusapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditReportActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    private ImageView editPhoto;
    private EditText EditItemName, EditItemDesc, EditPhone;
    private TextView ViewLocation, ViewDateTime, ViewClaimCode, ViewStatus;

    private Button btnCancelEdit, btnSaveEdit, btnDeleteItem;

    private int reportId = -1;
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_report);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.EditRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        editPhoto = findViewById(R.id.editPhoto);

        EditItemName = findViewById(R.id.EditItemName);
        EditItemDesc = findViewById(R.id.EditItemDesc);
        EditPhone = findViewById(R.id.EditPhone);

        ViewLocation = findViewById(R.id.ViewLocation);
        ViewDateTime = findViewById(R.id.ViewDateTime);
        ViewClaimCode = findViewById(R.id.ViewClaimCode);
        ViewStatus = findViewById(R.id.ViewStatus);

        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnSaveEdit = findViewById(R.id.btnSaveEdit);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);

        reportId = getIntent().getIntExtra("report_id", -1);
        if (reportId == -1) {
            finish();
            return;
        }

        // Get current username from session
        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        username = sp.getString("username", "");

        // Security check: only owner can edit/delete
        if (TextUtils.isEmpty(username) || !dbHelper.isReportOwnedByUser(reportId, username)) {
            new AlertDialog.Builder(this)
                    .setTitle("Access denied")
                    .setMessage("You can only manage items you reported.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (d, w) -> finish())
                    .show();
            return;
        }

        loadData();

        btnCancelEdit.setOnClickListener(v -> finish());
        btnSaveEdit.setOnClickListener(v -> saveEdit());
        btnDeleteItem.setOnClickListener(v -> confirmDelete());
    }

    private void loadData() {
        // EDIT: guna getReportById() yang full detail
        ReportItem item = dbHelper.getReportById(reportId);
        if (item == null) {
            finish();
            return;
        }

        // Editable fields
        EditItemName.setText(safeText(item.name));
        EditItemDesc.setText(safeText(item.desc));
        EditPhone.setText(safeText(item.phone));

        // Read-only info
        ViewLocation.setText(safeText(item.location));
        ViewDateTime.setText(safeText(item.date) + " • " + safeText(item.time));
        ViewClaimCode.setText(safeText(item.claimCode)); // NOTE: claim code display dalam edit page (kalau awak nak hide pun boleh)
        ViewStatus.setText(safeText(item.status));

        // Photo
        if (!TextUtils.isEmpty(item.photoUri)) {
            try {
                editPhoto.setImageURI(Uri.parse(item.photoUri));
            } catch (Exception e) {
                editPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            editPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void saveEdit() {
        String newName = EditItemName.getText().toString().trim();
        String newDesc = EditItemDesc.getText().toString().trim();
        String newPhone = EditPhone.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            EditItemName.setError("Required");
            EditItemName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newDesc)) {
            EditItemDesc.setError("Required");
            EditItemDesc.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newPhone)) {
            EditPhone.setError("Required");
            EditPhone.requestFocus();
            return;
        }

        // Extra security (double-check)
        if (TextUtils.isEmpty(username) || !dbHelper.isReportOwnedByUser(reportId, username)) {
            new AlertDialog.Builder(this)
                    .setTitle("Access denied")
                    .setMessage("You can only edit items you reported.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        boolean ok = dbHelper.updateReportEditableFields(reportId, newName, newDesc, newPhone);

        if (ok) {
            new AlertDialog.Builder(this)
                    .setTitle("Saved")
                    .setMessage("Your report has been updated.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (d, w) -> finish())
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Failed")
                    .setMessage("Update failed. Try again.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?\n\nThis action cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> deleteNow())
                .show();
    }

    private void deleteNow() {
        // Extra security (double-check)
        if (TextUtils.isEmpty(username) || !dbHelper.isReportOwnedByUser(reportId, username)) {
            new AlertDialog.Builder(this)
                    .setTitle("Access denied")
                    .setMessage("You can only delete items you reported.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        boolean ok = dbHelper.deleteReportById(reportId);
        if (ok) {
            new AlertDialog.Builder(this)
                    .setTitle("Deleted")
                    .setMessage("Item has been deleted.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (d, w) -> finish())
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Failed")
                    .setMessage("Delete failed. Try again.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private String safeText(String s) {
        if (s == null) return "";
        String t = s.trim();
        return t.isEmpty() ? "" : t;
    }
}
