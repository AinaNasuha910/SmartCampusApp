package com.example.smartcampusapp;

public class EditReportItem {
    public int id;
    public String name;
    public String desc;
    public String phone;

    public String location;
    public String date;
    public String time;
    public String photoUri;

    public String claimCode;
    public String status;

    public String reportedBy; // EDIT: tambah

    public EditReportItem(int id, String name, String desc, String phone,
                          String location, String date, String time, String photoUri,
                          String claimCode, String status, String reportedBy) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.phone = phone;
        this.location = location;
        this.date = date;
        this.time = time;
        this.photoUri = photoUri;
        this.claimCode = claimCode;
        this.status = status;
        this.reportedBy = reportedBy;
    }
}
