package com.example.walker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class PlansActivity extends AppCompatActivity {
    private MapView map = null;
    private MyLocationNewOverlay locationOverlay;
    private Button addBtn = null;
    private Button plansBtn = null;
    private Button settingsBtn = null;



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

    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // handle permissions before inflating MapView
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_plans);

        addBtn = findViewById(R.id.add_btn);
        plansBtn = findViewById(R.id.plan_btn);
        settingsBtn = findViewById(R.id.setting_btn);
        if (addBtn == null | plansBtn == null | settingsBtn == null) {
            Log.e("PlansActivity", "addBtn or plansBtn or settingsBtn is null");
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("PlansActivity", "clicked add plan");
                Intent addPlanIntent = new Intent(PlansActivity.this, AddPlanActivity.class);
                startActivity(addPlanIntent);
                finish();
        }});
//        map = (MapView) findViewById(R.id.mapView);
//        if (map != null) {
//            map.setTileSource(TileSourceFactory.MAPNIK);
//            map.setMultiTouchControls(true);
//            map.setBuiltInZoomControls(true);
//
//            IMapController mapController = map.getController();
//            mapController.setZoom(10);
//            GeoPoint startPoint = new GeoPoint(51496994, -134733);
//            mapController.setCenter(startPoint);
//        }

        checkPermissions();
    }

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