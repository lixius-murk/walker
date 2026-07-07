package com.example.walker.module.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "plans")

public class Plan {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "date")
    private String data;

    @ColumnInfo(name = "state")
    private String state;

    public String getData() {
        return data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


}
