package com.example.smartcampusapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private LatLng selectedLatLng;

    private TextView txtSelected;
    private Button btnUseLocation, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // EDIT: pastikan layout name betul
        setContentView(R.layout.activity_map_picker);

        // EDIT: pastikan id mapRoot wujud (kalau tak, crash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mapRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtSelected = findViewById(R.id.txtSelected);
        btnUseLocation = findViewById(R.id.btnUseLocation);
        btnCancel = findViewById(R.id.btnCancelMap);

        btnUseLocation.setOnClickListener(v -> {
            if (selectedLatLng == null) {
                txtSelected.setText("Please tap on the map to choose a location.");
                return;
            }

            String locationText = String.format(
                    Locale.getDefault(),
                    "Lat %.5f, Lng %.5f",
                    selectedLatLng.latitude,
                    selectedLatLng.longitude
            );

            Intent data = new Intent();
            data.putExtra("picked_location_text", locationText);
            data.putExtra("picked_lat", selectedLatLng.latitude);
            data.putExtra("picked_lng", selectedLatLng.longitude);
            setResult(RESULT_OK, data);
            finish();
        });

        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // EDIT: get map fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            // Kalau fragment null, akan nampak message ni (bukan crash)
            txtSelected.setText("Map error: SupportMapFragment not found. Check activity_map_picker.xml");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Default center (boleh ubah ikut UMPSA)
        LatLng defaultCenter = new LatLng(3.5435, 103.4280);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultCenter, 16f));

        mMap.setOnMapClickListener(this::setMarkerAt);
        mMap.setOnMapLongClickListener(this::setMarkerAt);

        txtSelected.setText("Tap on map to choose location");
    }

    private void setMarkerAt(@NonNull LatLng latLng) {
        selectedLatLng = latLng;

        if (marker != null) marker.remove();
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        txtSelected.setText(String.format(
                Locale.getDefault(),
                "Selected: Lat %.5f, Lng %.5f",
                latLng.latitude,
                latLng.longitude
        ));
    }
}
