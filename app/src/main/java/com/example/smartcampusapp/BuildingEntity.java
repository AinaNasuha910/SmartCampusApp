package com.example.smartcampusapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "buildings")
public class BuildingEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;
    public double latitude;
    public double longitude;
}
