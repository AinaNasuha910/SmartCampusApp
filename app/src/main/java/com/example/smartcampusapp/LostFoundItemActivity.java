package com.example.smartcampusapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LostFoundItemActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    private ImageView detailPhoto;
    private TextView detailName, detailFinder, detailLocation, detailDate, detailTime;
    private TextView detailDesc, detailHandover, detailPhone, detailStatus, detailClaimer;

    private Button btnClaim;
    private ImageButton btnMessage, btnLocation; // ✅ EDIT

    private int reportId = -1;
    private ReportItem item;

    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lost_found_item);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        username = sp.getString("username", "");

        detailPhoto = findViewById(R.id.detailPhoto);
        detailName = findViewById(R.id.detailName);
        detailFinder = findViewById(R.id.detailFinder);
        detailLocation = findViewById(R.id.detailLocation);
        detailDate = findViewById(R.id.detailDate);
        detailTime = findViewById(R.id.detailTime);

        detailDesc = findViewById(R.id.detailDesc);
        detailHandover = findViewById(R.id.detailHandover);
        detailPhone = findViewById(R.id.detailPhone);
        detailStatus = findViewById(R.id.detailStatus);
        detailClaimer = findViewById(R.id.detailClaimer);

        btnClaim = findViewById(R.id.btnClaim);
        btnMessage = findViewById(R.id.btnMessage);
        btnLocation = findViewById(R.id.btnLocation); // ✅ EDIT

        reportId = getIntent().getIntExtra("report_id", -1);
        if (reportId == -1) {
            finish();
            return;
        }

        loadData();

        btnClaim.setOnClickListener(v -> {
            if (item == null) return;

            if ("Claimed".equalsIgnoreCase(item.status)) {
                new AlertDialog.Builder(this)
                        .setTitle("Already claimed")
                        .setMessage("This item has already been claimed.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            if (TextUtils.isEmpty(username)) {
                new AlertDialog.Builder(this)
                        .setTitle("Login required")
                        .setMessage("Please login to claim an item.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            showSecureCodeDialog();
        });

        // WhatsApp message
        btnMessage.setOnClickListener(v -> {
            if (item == null) return;
            openWhatsAppMessage(item);
        });

        // ✅ EDIT: Open Google Maps / Directions
        btnLocation.setOnClickListener(v -> {
            if (item == null) return;
            openGoogleMaps(item.location);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        item = dbHelper.getReportById(reportId);
        if (item == null) {
            finish();
            return;
        }

        detailName.setText(item.name);

        String finder = (item.finder == null || item.finder.trim().isEmpty()) ? "-" : item.finder;
        detailFinder.setText("Finder: " + finder);

        detailLocation.setText("Location: " + safeText(item.location));
        detailDate.setText("Date: " + safeText(item.date));
        detailTime.setText("Time: " + safeText(item.time));

        detailDesc.setText(safeText(item.desc));
        detailHandover.setText("Handover Point: " + safeText(item.handover));
        detailPhone.setText("Phone (WhatsApp): " + safeText(item.phone));
        detailStatus.setText("Status: " + safeText(item.status));

        if ("Claimed".equalsIgnoreCase(item.status)) {
            detailClaimer.setText("Claimer: " + safeText(item.claimer));
            detailClaimer.setVisibility(View.VISIBLE);

            btnClaim.setText("Claimed");
            btnClaim.setEnabled(false);
            btnClaim.setAlpha(0.6f);
        } else {
            detailClaimer.setVisibility(View.GONE);

            btnClaim.setText("Claim");
            btnClaim.setEnabled(true);
            btnClaim.setAlpha(1f);
        }

        if (!TextUtils.isEmpty(item.photoUri)) {
            try {
                detailPhoto.setImageURI(Uri.parse(item.photoUri));
            } catch (Exception e) {
                detailPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            detailPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    // ===== Secure code dialog =====
    private void showSecureCodeDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter 4-digit code");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        new AlertDialog.Builder(this)
                .setTitle("Secure Code Required")
                .setMessage("Please enter the 4-digit claim code to confirm claiming this item.")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (d, w) -> {
                    String code = input.getText().toString().trim();

                    if (code.length() != 4) {
                        new AlertDialog.Builder(this)
                                .setTitle("Invalid code")
                                .setMessage("Code must be 4 digits.")
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }

                    doSecureClaim(code);
                })
                .show();
    }

    private void doSecureClaim(String code) {
        boolean ok = dbHelper.claimReportWithCode(reportId, code, username);

        if (ok) {
            new AlertDialog.Builder(this)
                    .setTitle("Success")
                    .setMessage("Item successfully claimed.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (d, w) -> finish())
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Failed")
                    .setMessage("Wrong code or item already claimed.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    // ===== WhatsApp function =====
    private void openWhatsAppMessage(ReportItem item) {
        String rawPhone = (item.phone == null) ? "" : item.phone.trim();
        if (TextUtils.isEmpty(rawPhone)) {
            new AlertDialog.Builder(this)
                    .setTitle("No phone number")
                    .setMessage("This report does not have a WhatsApp number.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String phone = normalizePhoneForWhatsApp(rawPhone);
        if (TextUtils.isEmpty(phone)) {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid phone number")
                    .setMessage("WhatsApp number format is invalid. Please check the phone number in the report.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String message = "Hi, I'm the claimer for the item \"" + safeText(item.name) + "\"."
                + "\n\nDetails:"
                + "\nLocation: " + safeText(item.location)
                + "\nDate/Time: " + safeText(item.date) + " " + safeText(item.time)
                + "\n\nCan we arrange the handover?";

        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        Uri uri = Uri.parse("https://wa.me/" + phone + "?text=" + encoded);

        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        i.setPackage("com.whatsapp");

        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (Exception ex) {
                new AlertDialog.Builder(this)
                        .setTitle("Cannot open WhatsApp")
                        .setMessage("WhatsApp is not available on this device.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    private String normalizePhoneForWhatsApp(String raw) {
        String digits = raw.replaceAll("[^0-9]", "");
        if (TextUtils.isEmpty(digits)) return "";

        if (digits.startsWith("0") && digits.length() >= 9 && digits.length() <= 11) {
            digits = "60" + digits.substring(1);
        }

        if (digits.length() < 8) return "";
        return digits;
    }

    // ===== EDIT: Open Google Maps (lat/lng OR text location) =====
    private void openGoogleMaps(String locationText) {
        if (locationText == null) locationText = "";
        String loc = locationText.trim();

        if (loc.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("No location")
                    .setMessage("This item does not have a location.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Try parse "Lat 3.12345, Lng 103.12345"
        double[] latLng = extractLatLng(loc);

        Uri uri;
        if (latLng != null) {
            // Directions to coordinates
            String dest = latLng[0] + "," + latLng[1];
            uri = Uri.parse("google.navigation:q=" + dest);
        } else {
            // Search by text location
            String q = URLEncoder.encode(loc, StandardCharsets.UTF_8);
            uri = Uri.parse("geo:0,0?q=" + q);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // fallback (browser / any map app)
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (Exception ex) {
                new AlertDialog.Builder(this)
                        .setTitle("Cannot open Maps")
                        .setMessage("Google Maps is not available on this device.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    private double[] extractLatLng(String text) {
        // match like: Lat 3.54350, Lng 103.42800 (case-insensitive)
        Pattern p = Pattern.compile("lat\\s*([+-]?[0-9]*\\.?[0-9]+)\\s*,\\s*lng\\s*([+-]?[0-9]*\\.?[0-9]+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            try {
                double lat = Double.parseDouble(m.group(1));
                double lng = Double.parseDouble(m.group(2));
                return new double[]{lat, lng};
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String safeText(String s) {
        if (s == null) return "-";
        String t = s.trim();
        return t.isEmpty() ? "-" : t;
    }
}
