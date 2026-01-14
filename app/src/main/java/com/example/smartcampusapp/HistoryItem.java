package com.example.smartcampusapp;

public class HistoryItem {
    public int id;
    public String name;
    public String location;
    public String date;
    public String time;
    public String claimCode;
    public String photoUri; // EDIT: tambah gambar

    public HistoryItem(int id, String name, String location, String date, String time, String claimCode, String photoUri) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.time = time;
        this.claimCode = claimCode;
        this.photoUri = photoUri;
    }
}
