package com.example.walker.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.walker.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class AddPlanActivity extends AppCompatActivity {
    private MapView map = null;
    private MyLocationNewOverlay locationOverlay;
    private ImageButton btnBack = null;
    private EditText editTextNane = null;

    Button dateBtn = null;
    boolean exit;
    private PopupWindow.OnDismissListener popupListener;
    private PopupWindow popupWindow;

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

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        popupWindow.setOnDismissListener(popupListener);
        popupWindow.showAtLocation(findViewById(R.id.activity_add_plan), Gravity.CENTER, 0, 0);
    }

    private void setupLocationOverlay() {
        if (map == null) return;
        this.locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        this.locationOverlay.enableMyLocation();
        this.locationOverlay.enableFollowLocation();

        map.getOverlays().add(this.locationOverlay);
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
