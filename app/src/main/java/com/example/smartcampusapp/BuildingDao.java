package com.example.smartcampusapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface BuildingDao {
    @Insert
    void insertBuilding(BuildingEntity building);

    @Query("SELECT * FROM buildings")
    List<BuildingEntity> getAllBuildings();

    @Query("SELECT * FROM buildings WHERE id = :id LIMIT 1")
    BuildingEntity getBuildingById(int id);

    @Update
    void updateBuilding(BuildingEntity building);

    @Delete
    void deleteBuilding(BuildingEntity building);
}