package com.example.walker.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.GnssAntennaInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.walker.R;
import com.example.walker.controller.DatabaseController;
import com.example.walker.module.entities.Place;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddPlanActivity extends AppCompatActivity {
    private DatabaseController dbController;
    private ArrayList<Place> selectedStops = new ArrayList<>();

    private MapView map = null;
    private MyLocationNewOverlay locationOverlay;
    private ImageButton btnBack = null;
    private EditText editTextNane = null;

    Button dateBtn = null;
    Button saveBtn = null;
    private Date selectedDate = null;

    boolean exit;
    private PopupWindow.OnDismissListener popupListener;
    private PopupWindow popupWindow;
    private Long planId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        exit = false;
        setContentView(R.layout.activity_add_plan);
        editTextNane = findViewById(R.id.nameEditText);
        btnBack = findViewById(R.id.backActionButton);
        btnBack.setOnClickListener(v -> goBack());
        dateBtn = findViewById(R.id.dateBtn);
        dateBtn.setOnClickListener(v -> showPopup());
        saveBtn = findViewById(R.id.saveNewPlanBtn);
        saveBtn.setOnClickListener(v -> savePlan());

//in kotlin
//        datePicker.init(
//                today.get(Calendar.YEAR),
//                today.get(Calendar.MONTH),
//                today.get(Calendar.DAY_OF_MONTH),
//                new DatePicker.OnDateChangedListener() {
//                    @Override
//                    public void onDateChanged(DatePicker view, int year, int month, int day) {
//                        // Display selected date in Toast message
//                        String msg = "You Selected: " + day + "/" + (month + 1) + "/" + year;
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                }
        popupListener = new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {}
        };


        map = (MapView) findViewById(R.id.map);
        if (map != null) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);
            map.setBuiltInZoomControls(true);

            IMapController mapController = map.getController();
            mapController.setZoom(10);
            GeoPoint startPoint = new GeoPoint(51496994, -134733);
            mapController.setCenter(startPoint);
        }
        dbController = new DatabaseController(getApplicationContext());
        SetUpMapStops();

    }

    private void savePlan() {
        String name = editTextNane.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "plan name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDate == null) {
            Toast.makeText(this, "please pick a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedStops.isEmpty()) {
            Toast.makeText(this, "add at least one stop", Toast.LENGTH_SHORT).show();
            return;
        }

        long newPlanId = dbController.AddNewPlan(name, selectedDate, selectedStops.toArray(new Place[0]));
        planId = newPlanId;

        Intent resultIntent = new Intent();
        resultIntent.putExtra("planName", name);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "plan saved", Toast.LENGTH_SHORT).show();
        Log.d("AddPlanActivity", "plan saved");
        finish();
    }
    private void SetUpMapStops() {
        MapEventsReceiver receiver = new MapEventsReceiver() {

            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                showAddStopDialog(p);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        map.getOverlays().add(new MapEventsOverlay(receiver));

    }
    private void showAddStopDialog(GeoPoint point) {
        String[] options = {"add new stop", "choose existing stop"};
        new AlertDialog.Builder(this)
                .setTitle("add stop")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        setNewStopName(point);
                    } else {
                        setExistingStops(point);
                    }
                })
                .show();
    }
    private void setNewStopName(GeoPoint point) {
        EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Stop name")
                .setView(input)
                .setPositiveButton("add", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Place place = new Place();
                    place.setName(name);
                    addStop(place, point);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addStop(Place place, GeoPoint point) {
        selectedStops.add(place);
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setTitle(place.getName());
        map.getOverlays().add(marker);
        map.invalidate();
    }

    private void setExistingStops(GeoPoint point) {
        List<Place> existing = dbController.getAllPlaces();
        if (existing.isEmpty()) {
            Toast.makeText(this, "No existing stops yet", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] names = new String[existing.size()];
        for (int i = 0; i < existing.size(); i++) {
            names[i] = existing.get(i).getName();
        }
        new AlertDialog.Builder(this)
                .setTitle("Choose stop")
                .setItems(names, (dialog, which) -> addStop(existing.get(which), point))
                .show();
    }

    @Override
    public void onContentChanged(){
        super.onContentChanged();
        exit = false;
    }

    private void goBack() {
        if (exit) {
            finish();
        } else {
            exit = true;
            Toast.makeText(this, "tap back again to leave without saving", Toast.LENGTH_SHORT).show();
            //non-blocking delay
            new android.os.Handler(getMainLooper()).postDelayed(() -> exit = false, 2000);
        }
    }


    // ACCESS_COARSE_LOCATION is for last location (less accurate)
    // ACCESS_FINE_LOCATION keeps fresh location
    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if (coarseLocationGranted!=null || fineLocationGranted !=null) {
                    setupLocationOverlay();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setupLocationOverlay();
        } else {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }
    private void showPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_date, null);

        DatePicker datePicker = popupView.findViewById(R.id.datePicker);
        Calendar today = Calendar.getInstance();
        datePicker.init(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),
                (view, year, month, day) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, day);
                    selectedDate = cal.getTime();
                    dateBtn.setText(day + "/" + (month + 1) + "/" + year);
                }
        );

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        Drawable back = getDrawable(R.drawable.background);
        popupWindow.setBackgroundDrawable(back);
        popupWindow.setAnimationStyle(-1);
        popupWindow.setOnDismissListener(popupListener);
        popupWindow.showAtLocation(findViewById(R.id.activity_add_plan), Gravity.CENTER, 0, 0);
    }

    private void setupLocationOverlay() {
        if (map == null) return;
        this.locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        this.locationOverlay.enableMyLocation();
        this.locationOverlay.enableFollowLocation();

        map.getOverlays().add(this.locationOverlay);
        map.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }
}
