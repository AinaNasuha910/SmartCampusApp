package com.example.smartcampusapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "project.db";
    private static final int DB_VERSION = 9; // EDIT: naikkan version sebab tambah claimer_id

    // USERS
    public static final String TABLE_USERS = "users";
    public static final String COL_U_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // REPORTS
    public static final String TABLE_REPORTS = "reports";
    public static final String COL_R_ID = "id";
    public static final String COL_ITEM_NAME = "item_name";
    public static final String COL_DESC = "item_desc";
    public static final String COL_LOCATION = "item_location";
    public static final String COL_DATE = "lost_date";
    public static final String COL_TIME = "lost_time";
    public static final String COL_PHOTO_URI = "photo_uri";
    public static final String COL_HANDOVER = "handover_point";
    public static final String COL_PHONE = "phone_whatsapp";
    public static final String COL_CLAIM_CODE = "claim_code";
    public static final String COL_STATUS = "status";
    public static final String COL_CREATED_AT = "created_at";

    // siapa yang submit report
    public static final String COL_REPORTED_BY = "reported_by";

    // EDIT: siapa yang claim item
    public static final String COL_CLAIMER_ID = "claimer_id";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsers = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT UNIQUE NOT NULL, "
                + COL_PASSWORD + " TEXT NOT NULL"
                + ")";
        db.execSQL(createUsers);

        String createReports = "CREATE TABLE " + TABLE_REPORTS + " ("
                + COL_R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ITEM_NAME + " TEXT NOT NULL, "
                + COL_DESC + " TEXT NOT NULL, "
                + COL_LOCATION + " TEXT NOT NULL, "
                + COL_DATE + " TEXT NOT NULL, "
                + COL_TIME + " TEXT NOT NULL, "
                + COL_PHOTO_URI + " TEXT NOT NULL, "
                + COL_HANDOVER + " TEXT NOT NULL, "
                + COL_PHONE + " TEXT NOT NULL, "
                + COL_CLAIM_CODE + " TEXT NOT NULL, "
                + COL_STATUS + " TEXT DEFAULT 'Unclaimed', "
                + COL_CREATED_AT + " INTEGER, "
                + COL_REPORTED_BY + " TEXT NOT NULL, "
                + COL_CLAIMER_ID + " TEXT"
                + ")";
        db.execSQL(createReports);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Untuk fasa testing: reset terus
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
        onCreate(db);
    }

    // ===== LOGIN / REGISTER =====

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, username);
        cv.put(COL_PASSWORD, password);
        long res = db.insert(TABLE_USERS, null, cv);
        return res != -1;
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ? LIMIT 1",
                new String[]{username}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ? LIMIT 1",
                new String[]{username, password}
        );
        boolean ok = cursor.moveToFirst();
        cursor.close();
        return ok;
    }

    // ===== INSERT REPORT =====
    public boolean insertReport(
            String itemName,
            String itemDesc,
            String location,
            String date,
            String time,
            String photoUri,
            String handoverPoint,
            String phoneWhatsApp,
            String claimCode,
            String reportedBy
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_ITEM_NAME, itemName);
        cv.put(COL_DESC, itemDesc);
        cv.put(COL_LOCATION, location);
        cv.put(COL_DATE, date);
        cv.put(COL_TIME, time);
        cv.put(COL_PHOTO_URI, photoUri);
        cv.put(COL_HANDOVER, handoverPoint);
        cv.put(COL_PHONE, phoneWhatsApp);
        cv.put(COL_CLAIM_CODE, claimCode);
        cv.put(COL_STATUS, "Unclaimed");
        cv.put(COL_CREATED_AT, System.currentTimeMillis());
        cv.put(COL_REPORTED_BY, reportedBy);

        // EDIT: belum ada claimer masa report
        cv.putNull(COL_CLAIMER_ID);

        long res = db.insert(TABLE_REPORTS, null, cv);
        return res != -1;
    }

    // ===== GET REPORTS BY STATUS (Listing utama) =====
    public ArrayList<ReportItem> getReportsByStatus(String status) {
        ArrayList<ReportItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_R_ID + ", " + COL_ITEM_NAME + ", " + COL_LOCATION + ", " + COL_TIME + ", " + COL_PHOTO_URI + ", " + COL_STATUS +
                        " FROM " + TABLE_REPORTS +
                        " WHERE " + COL_STATUS + " = ?" +
                        " ORDER BY " + COL_CREATED_AT + " DESC",
                new String[]{status}
        );

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String name = c.getString(1);
                String location = c.getString(2);
                String time = c.getString(3);
                String photo = c.getString(4);
                String st = c.getString(5);

                if (time == null || time.trim().isEmpty()) time = "--:--";

                list.add(new ReportItem(id, name, location, time, photo, st));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    // ===== HISTORY LIST (user punya report sahaja + Unclaimed sahaja) =====
    public ArrayList<HistoryItem> getUserUnclaimedHistory(String username) {
        ArrayList<HistoryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_R_ID + ", " + COL_ITEM_NAME + ", " + COL_LOCATION + ", " + COL_DATE + ", " + COL_TIME + ", " + COL_CLAIM_CODE + ", " + COL_PHOTO_URI +
                        " FROM " + TABLE_REPORTS +
                        " WHERE " + COL_REPORTED_BY + " = ? AND " + COL_STATUS + " = 'Unclaimed' " +
                        " ORDER BY " + COL_CREATED_AT + " DESC",
                new String[]{username}
        );

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String name = c.getString(1);
                String location = c.getString(2);
                String date = c.getString(3);
                String time = c.getString(4);
                String code = c.getString(5);
                String photoUri = c.getString(6);

                list.add(new HistoryItem(id, name, location, date, time, code, photoUri));
            } while (c.moveToNext());
        }

        c.close();
        return list;
    }

    // ===== GET REPORT BY ID (DETAIL FULL) =====
    public ReportItem getReportById(int reportId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_R_ID + ", " + COL_ITEM_NAME + ", " + COL_LOCATION + ", " +
                        COL_DATE + ", " + COL_TIME + ", " + COL_DESC + ", " +
                        COL_PHOTO_URI + ", " + COL_HANDOVER + ", " + COL_PHONE + ", " +
                        COL_CLAIM_CODE + ", " + COL_STATUS + ", " + COL_REPORTED_BY + ", " + COL_CLAIMER_ID +
                        " FROM " + TABLE_REPORTS +
                        " WHERE " + COL_R_ID + " = ? LIMIT 1",
                new String[]{String.valueOf(reportId)}
        );

        ReportItem item = null;

        if (c.moveToFirst()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String location = c.getString(2);
            String date = c.getString(3);
            String time = c.getString(4);
            String desc = c.getString(5);
            String photo = c.getString(6);
            String handover = c.getString(7);
            String phone = c.getString(8);
            String claimCode = c.getString(9);
            String st = c.getString(10);
            String finder = c.getString(11);
            String claimer = c.getString(12);

            if (time == null || time.trim().isEmpty()) time = "--:--";
            if (date == null) date = "-";
            if (desc == null) desc = "-";
            if (handover == null) handover = "-";
            if (phone == null) phone = "-";
            if (claimCode == null) claimCode = "";
            if (finder == null || finder.trim().isEmpty()) finder = "-";
            if (claimer == null) claimer = "";

            item = new ReportItem(
                    id, name, location, date, time,
                    desc, photo, handover, phone,
                    claimCode, st, finder, claimer
            );
        }

        c.close();
        return item;
    }

    // ===== UPDATE: hanya field yang dibenarkan =====
    public boolean updateReportEditableFields(int reportId, String newName, String newDesc, String newPhone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_ITEM_NAME, newName);
        cv.put(COL_DESC, newDesc);
        cv.put(COL_PHONE, newPhone);

        int rows = db.update(TABLE_REPORTS, cv, COL_R_ID + " = ?", new String[]{String.valueOf(reportId)});
        return rows > 0;
    }

    // ===== security check - report ini milik user =====
    public boolean isReportOwnedByUser(int reportId, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + TABLE_REPORTS +
                        " WHERE " + COL_R_ID + " = ? AND " + COL_REPORTED_BY + " = ? LIMIT 1",
                new String[]{String.valueOf(reportId), username}
        );
        boolean ok = c.moveToFirst();
        c.close();
        return ok;
    }

    // ===== DELETE REPORT =====
    public boolean deleteReportById(int reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_REPORTS, COL_R_ID + " = ?", new String[]{String.valueOf(reportId)});
        return rows > 0;
    }

    // ===== SECURE CLAIM: validate code + update Claimed + save claimer_id =====
    public boolean claimReportWithCode(int reportId, String inputCode, String claimerUsername) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_STATUS, "Claimed");
        cv.put(COL_CLAIMER_ID, claimerUsername);

        // Update hanya jika:
        // - id betul
        // - status masih Unclaimed
        // - claim_code sepadan
        int rows = db.update(
                TABLE_REPORTS,
                cv,
                COL_R_ID + " = ? AND " + COL_STATUS + " = 'Unclaimed' AND " + COL_CLAIM_CODE + " = ?",
                new String[]{String.valueOf(reportId), inputCode}
        );

        return rows > 0;
    }
}
