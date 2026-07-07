package com.example.walker.controllers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.walker.module.entities.Place;
import com.example.walker.module.entities.Plan;

import java.util.ArrayList;

public class DatabaseController extends SQLiteOpenHelper {

    private static final String DB_NAME = "walker.db";
    private static final int DB_VERSION = 1;

    public DatabaseController(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE stops(id integer PRIMARY KEY AUTOINCREMENT, name TEXT);");
        db.execSQL("CREATE TABLE plans(id integer PRIMARY KEY AUTOINCREMENT, name TEXT, data TEXT, state TEXT);");
        db.execSQL("CREATE TABLE ps(id integer PRIMARY KEY AUTOINCREMENT, id_plan integer, id_stop integer,  FOREIGN KEY(id_stop) REFERENCES stops(id), FOREIGN KEY(id_plan) REFERENCES plans(id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS stops;");
        db.execSQL("DROP TABLE IF EXISTS plans;");
        db.execSQL("DROP TABLE IF EXISTS ps;");
        onCreate(db);
    }

    public ArrayList<Place> getAllPlacesForPlan(int id){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT stops.id, stops.name FROM ps JOIN stops ON ps.id_stop = stops.id WHERE ps.id_plan = ?;", new String[]{String.valueOf(id)});
        ArrayList<Place> list = new ArrayList<>();
        while(cur.moveToNext()){
            Place pl = new Place();
            pl.setId(cur.getInt(0));
            pl.setName(cur.getString(1));
            list.add(pl);
        }
        cur.close();
        return list;
    }
    //get everything ready in activity!
    public long AddNewPlace(Place pl, Plan plan){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO stops (name) VALUES (?);", new String[]{pl.getName()});
        Cursor idCur = db.rawQuery("SELECT last_insert_rowid();", null);
        idCur.moveToFirst();
        long stopId = idCur.getLong(0);
        idCur.close();
        db.execSQL("INSERT INTO ps (id_plan, id_stop) VALUES (?, ?);", new String[]{String.valueOf(plan.getId()), String.valueOf(stopId)});
        Log.d("DB", "added new place");
        return stopId;
    }

}
