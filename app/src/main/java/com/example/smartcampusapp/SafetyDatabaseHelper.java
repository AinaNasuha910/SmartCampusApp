package com.example.smartcampusapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SafetyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student_safety.db";
    private static final int DATABASE_VERSION = 1;

    public SafetyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Emergency logs table
        db.execSQL(
                "CREATE TABLE emergency_logs (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "latitude REAL NOT NULL," +
                        "longitude REAL NOT NULL," +
                        "timestamp TEXT NOT NULL" +
                        ")"
        );

        // Step history table
        db.execSQL(
                "CREATE TABLE step_history (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "date TEXT NOT NULL," +
                        "steps INTEGER NOT NULL" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS emergency_logs");
        db.execSQL("DROP TABLE IF EXISTS step_history");
        onCreate(db);
    }
}

