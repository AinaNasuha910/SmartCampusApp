package com.example.smartcampusapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ReportLostItemActivity extends AppCompatActivity {

    private EditText ItemName, ItemDescription, Location;
    private EditText PhoneNumber;

    private LinearLayout btnPickDate, btnPickTime, btnChangePhoto;
    private TextView SelectedDate, SelectedTime, PhotoFileName, FileStatus;
    private Spinner HandoverOption;
    private Button btnCancel, btnSubmit;

    private Button btnPickMap;

    private DBHelper dbHelper;

    private String pickedDate = "";
    private String pickedTime = "";
    private Uri selectedPhotoUri = null;
    private Uri cameraPhotoUri = null;

    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;

    private ActivityResultLauncher<Intent> mapPickerLauncher;

    private String currentCameraFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_lost_item);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ReportLost), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        bindViews();
        setupLaunchers();
        setupClicks();
    }

    private void bindViews() {
        ItemName = findViewById(R.id.ItemName);
        ItemDescription = findViewById(R.id.ItemDescription);
        Location = findViewById(R.id.Location);

        PhoneNumber = findViewById(R.id.PhoneNumber);

        btnPickMap = findViewById(R.id.btnPickMap);

        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);

        SelectedDate = findViewById(R.id.SelectedDate);
        SelectedTime = findViewById(R.id.SelectedTime);

        PhotoFileName = findViewById(R.id.PhotoFileName);
        FileStatus = findViewById(R.id.FileStatus);

        HandoverOption = findViewById(R.id.HandoverOption);

        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setupLaunchers() {

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedPhotoUri = uri;
                        PhotoFileName.setText(getSimpleNameFromUri(uri));
                        FileStatus.setText("Selected");
                    }
                }
        );

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraPhotoUri != null) {
                        selectedPhotoUri = cameraPhotoUri;
                        if (!TextUtils.isEmpty(currentCameraFileName)) {
                            PhotoFileName.setText(currentCameraFileName);
                        } else {
                            PhotoFileName.setText("camera_photo.jpg");
                        }
                        FileStatus.setText("Captured");
                    } else {
                        FileStatus.setText("Camera cancelled");
                    }
                }
        );

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) openCamera();
                    else FileStatus.setText("Camera permission denied");
                }
        );

        mapPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String text = result.getData().getStringExtra("picked_location_text");
                        if (!TextUtils.isEmpty(text)) {
                            Location.setText(text);
                            FileStatus.setText("Location picked from map");
                        }
                    }
                }
        );
    }

    private void setupClicks() {
        btnPickDate.setOnClickListener(v -> openDatePicker());
        btnPickTime.setOnClickListener(v -> openTimePicker());
        btnChangePhoto.setOnClickListener(v -> showPhotoOptionsDialog());

        btnPickMap.setOnClickListener(v -> {
            Intent intent = new Intent(ReportLostItemActivity.this, MapPickerActivity.class);
            mapPickerLauncher.launch(intent);
        });

        btnCancel.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void openDatePicker() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(year, month, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            pickedDate = sdf.format(picked.getTime());
            SelectedDate.setText(pickedDate);

            FileStatus.setText("");
        }, y, m, d);

        dp.show();
    }

    private void openTimePicker() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        TimePickerDialog tp = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            pickedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            SelectedTime.setText(pickedTime);

            FileStatus.setText("");
        }, hour, min, true);

        tp.show();
    }

    private void showPhotoOptionsDialog() {
        String[] options = {"Choose from Gallery", "Use Camera"};

        new AlertDialog.Builder(this)
                .setTitle("Upload photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openGallery();
                    else checkCameraPermissionThenOpen();
                })
                .show();
    }

    private void openGallery() {
        pickImageLauncher.launch("image/*");
    }

    private void checkCameraPermissionThenOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (picturesDir == null) {
                FileStatus.setText("Camera error: Pictures folder not available");
                return;
            }
            if (!picturesDir.exists()) picturesDir.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            currentCameraFileName = "IMG_" + timeStamp + ".jpg";

            File photoFile = new File(picturesDir, currentCameraFileName);

            cameraPhotoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );

            takePictureLauncher.launch(cameraPhotoUri);

        } catch (Exception e) {
            FileStatus.setText("Camera error: " + e.getMessage());
        }
    }

    private String generateClaimCode4Digit() {
        int code = new Random().nextInt(9000) + 1000;
        return String.valueOf(code);
    }

    private void submitReport() {
        String name = ItemName.getText().toString().trim();
        String desc = ItemDescription.getText().toString().trim();
        String loc = Location.getText().toString().trim();
        String phone = PhoneNumber.getText().toString().trim();

        String handover = (HandoverOption.getSelectedItem() != null)
                ? HandoverOption.getSelectedItem().toString().trim()
                : "";

        if (TextUtils.isEmpty(name)) {
            ItemName.setError("Required");
            ItemName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(desc)) {
            ItemDescription.setError("Required");
            ItemDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(loc)) {
            Location.setError("Required");
            Location.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            PhoneNumber.setError("Required");
            PhoneNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pickedDate)) {
            FileStatus.setText("Please pick a date");
            return;
        }

        if (TextUtils.isEmpty(pickedTime)) {
            FileStatus.setText("Please pick a time");
            return;
        }

        if (selectedPhotoUri == null) {
            FileStatus.setText("Please upload a photo");
            return;
        }

        if (TextUtils.isEmpty(handover)) {
            FileStatus.setText("Please choose a handover point");
            return;
        }

        // EDIT: ambil username yang login
        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        String reportedBy = sp.getString("username", "");
        if (TextUtils.isEmpty(reportedBy)) reportedBy = "unknown";

        String photoUriStr = selectedPhotoUri.toString();
        String claimCode = generateClaimCode4Digit();

        boolean ok = dbHelper.insertReport(
                name,
                desc,
                loc,
                pickedDate,
                pickedTime,
                photoUriStr,
                handover,
                phone,
                claimCode,
                reportedBy // EDIT
        );

        if (ok) {
            new AlertDialog.Builder(this)
                    .setTitle("Report submitted")
                    .setMessage("Your 4-digit claim code is:\n\n" + claimCode +
                            "\n\nShare this code only when you hand over the item to the owner.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (d, w) -> finish())
                    .show();
        } else {
            FileStatus.setText("Failed to save.");
        }
    }

    private String getSimpleNameFromUri(@NonNull Uri uri) {
        String last = uri.getLastPathSegment();
        if (last == null) return "selected_image";
        return last;
    }
}

