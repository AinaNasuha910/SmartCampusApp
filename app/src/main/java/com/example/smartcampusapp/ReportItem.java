package com.example.smartcampusapp;

public class ReportItem {
    public int id;

    // Basic (used in listing)
    public String name;
    public String location;
    public String time;      // lost_time
    public String photoUri;  // photo_uri
    public String status;    // Unclaimed / Claimed

    // Extra (detail/edit)
    public String desc;      // item_desc
    public String date;      // lost_date
    public String handover;  // handover_point
    public String phone;     // phone_whatsapp
    public String finder;    // reported_by
    public String claimCode; // claim_code
    public String claimer;   // claimer_id  ✅ EDIT: tambah

    // Constructor for list (existing usage in getReportsByStatus)
    public ReportItem(int id, String name, String location, String time, String photoUri, String status) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.time = time;
        this.photoUri = photoUri;
        this.status = status;

        this.desc = "";
        this.date = "";
        this.handover = "";
        this.phone = "";
        this.finder = "";
        this.claimCode = "";
        this.claimer = "";
    }

    // Constructor for detail/edit (more info)
    public ReportItem(int id, String name, String location, String date, String time,
                      String desc, String photoUri, String handover, String phone,
                      String claimCode, String status, String finder, String claimer) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.time = time;
        this.desc = desc;
        this.photoUri = photoUri;
        this.handover = handover;
        this.phone = phone;
        this.claimCode = claimCode;
        this.status = status;
        this.finder = finder;
        this.claimer = claimer;
    }
}

