package com.example.smartcampusapp;

public class EmergencyLog {

    public int id;
    public double latitude;
    public double longitude;
    public String timestamp;

    public EmergencyLog(int id, double latitude, double longitude, String timestamp) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
}
