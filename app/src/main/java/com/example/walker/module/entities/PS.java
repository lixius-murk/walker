package com.example.walker.module.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ps")

public class PS {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "id_plan")
    private long id_plan;
    @ColumnInfo(name = "id_stop")
    private long id_stop;

    public long getId_stop() {
        return id_stop;
    }

    public long getId_plan() {
        return id_plan;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setId_plan(long id_plan) {
        this.id_plan = id_plan;
    }

    public void setId_stop(long id_stop) {
        this.id_stop = id_stop;
    }
}
