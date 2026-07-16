package com.example.walker.module.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "plans")

public class Plan {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo
    private String name;
    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "state")
    private String state;

    @ColumnInfo(name = "lat")
    private float lat;
    @ColumnInfo(name = "longt")
    private float longt;

    public String getDate() {
        return date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLongt(float longt) {
        this.longt = longt;
    }

    public float getLat() {
        return lat;
    }

    public float getLongt() {
        return longt;
    }
}
