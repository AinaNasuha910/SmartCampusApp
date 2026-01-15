package com.example.smartcampusapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor accelerometer;

    TextView txtSteps;
    Button btnSaveSteps;

    int stepCount = 0;
    float lastAcceleration = 0;

    SafetyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        txtSteps = findViewById(R.id.txtSteps);
        btnSaveSteps = findViewById(R.id.btnSaveSteps);

        dbHelper = new SafetyDatabaseHelper(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnSaveSteps.setOnClickListener(v -> saveStepsToDatabase());
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x*x + y*y + z*z);

        if (acceleration > 12 && Math.abs(acceleration - lastAcceleration) > 2) {
            stepCount++;
            txtSteps.setText(String.valueOf(stepCount));
        }

        lastAcceleration = acceleration;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void saveStepsToDatabase() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String date = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("steps", stepCount);

        db.insert("step_history", null, values);

        Toast.makeText(this, "Steps saved successfully!", Toast.LENGTH_LONG).show();
    }
}
