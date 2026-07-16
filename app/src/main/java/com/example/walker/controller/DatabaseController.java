package com.example.walker.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.walker.module.entities.Place;
import com.example.walker.module.entities.Plan;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DatabaseController extends SQLiteOpenHelper {

    private static final String DB_NAME = "walker.db";
    private static final int DB_VERSION = 1;

    public DatabaseController(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE stops(id integer PRIMARY KEY AUTOINCREMENT, name TEXT, lat DOUBLE, longt DOUBLE);");
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
    public ArrayList<Plan> getAllPlans(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM plans", null);
        ArrayList<Plan> list = new ArrayList<>();

        while(cursor.moveToNext()){
            Plan pl = new Plan();
            pl.setId(cursor.getInt(0));
            pl.setName(cursor.getString(1));
            pl.setDate(cursor.getString(2));
            list.add(pl);
        }
        cursor.close();
        return list;
    }

    public List<Plan> getPlacesForPlan(Long id){
        List<Long> ids = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_stop FROM ps WHERE id_plan = ?;", new String[]{String.valueOf(id)});
        while(cursor.moveToNext()){
            ids.add(cursor.getLong(0));
        }
        cursor.close();

        List<Plan> list = new ArrayList<>();
        for(Long idd: ids){
            cursor = db.rawQuery("SELECT * FROM stops WHERE id = ?;", new String[]{String.valueOf(idd)});
            if(cursor.moveToNext()){
                Plan pl = new Plan();
                pl.setId(cursor.getLong(0));
                pl.setName(cursor.getString(1));
                pl.setLat(cursor.getFloat(2));
                pl.setLongt(cursor.getFloat(3));

                list.add(pl);
            }
        }
        return list;
    }



    public long addNewPlace(String name, Long planId){
        SQLiteDatabase db = getWritableDatabase();

        long stopId;
        Cursor existing = db.rawQuery("SELECT id FROM stops WHERE name = ?;", new String[]{name});
        if (existing.moveToFirst()) {
            stopId = existing.getLong(0);
        } else {
            db.execSQL("INSERT INTO stops (name) VALUES (?);", new Object[]{name});
            Cursor idCur = db.rawQuery("SELECT last_insert_rowid();", null);
            idCur.moveToFirst();
            stopId = idCur.getLong(0);
            idCur.close();
        }
        existing.close();

        db.execSQL("INSERT INTO ps (id_plan, id_stop) VALUES (?, ?);", new Object[]{planId, stopId});
        Log.d("DB", "added new place");
        return stopId;
    }


    public List<Place> getAllPlaces(){
        List<Place> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM stops", null);
        while(cursor.moveToNext()){
            Place pl = new Place();
            pl.setId(cursor.getInt(0));
            pl.setName(cursor.getString(1));
            list.add(pl);
        }
        cursor.close();
        return list;
    }
    public long AddNewPlan(String name, Date date, Place[] places){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("INSERT INTO plans (name, data, state) VALUES (?, ?, ?);",
                new Object[]{name, date.toString(), "ready to go"});
        Cursor planIdCur = db.rawQuery("SELECT last_insert_rowid();", null);
        planIdCur.moveToFirst();
        long planId = planIdCur.getLong(0);
        planIdCur.close();

        for (Place place : places) {
            long stopId;
            Cursor existing = db.rawQuery("SELECT id FROM stops WHERE name = ?;", new String[]{place.getName()});
            if (existing.moveToFirst()) {
                stopId = existing.getLong(0);
            } else {
                db.execSQL("INSERT INTO stops (name) VALUES (?);", new Object[]{place.getName()});
                Cursor idCur = db.rawQuery("SELECT last_insert_rowid();", null);
                idCur.moveToFirst();
                stopId = idCur.getLong(0);
                idCur.close();
            }
            existing.close();

            db.execSQL("INSERT INTO ps (id_plan, id_stop) VALUES (?, ?);", new Object[]{planId, stopId});
        }

        Log.d("DB", "added new plan");
        return planId;
    }

}
