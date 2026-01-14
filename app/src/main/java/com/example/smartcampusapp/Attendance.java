package com.example.smartcampusapp;

public class Attendance {

    public String key;
    public String name;
    public String studentId;
    public String classCode;
    public String status;


    // Required empty constructor for Firebase
    public Attendance() {}

    public Attendance(String name, String studentId, String classCode, String status, String timestamp) {
        this.name = name;
        this.studentId = studentId;
        this.classCode = classCode;
        this.status = status;
    }
}
