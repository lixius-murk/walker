package com.example.walker.activity;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.walker.R;
import com.example.walker.controller.DatabaseController;
import com.example.walker.module.entities.Plan;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Iterator;

public class PlansActivity extends AppCompatActivity {
    private MyLocationNewOverlay locationOverlay;
    private ImageButton addBtn = null;
    private ImageButton plansBtn = null;
    private ImageButton settingsBtn = null;

    private ConstraintLayout layoutPlans = null;

    private DatabaseController dbc;

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
        layoutPlans=findViewById(R.id.layoutPlans);
        if (addBtn == null | plansBtn == null | layoutPlans == null| settingsBtn == null) {
            Log.e("PlansActivity", "addBtn or ScrollView or plansBtn or settingsBtn is null");
        }
        dbc = new DatabaseController(getApplicationContext());
        final ActivityResultLauncher<Intent> addPlanLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String planName = result.getData().getStringExtra("planName");
                    }
                });

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("PlansActivity", "clicked add plan");
                Intent addPlanIntent = new Intent(PlansActivity.this, AddPlanActivity.class);
                addPlanLauncher.launch(addPlanIntent);
            }
        });
        plansBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("PlansActivity", "clicked plans");
            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("PlansActivity", "clicked settings");
            }
        });
        loadPlans();



    }


    public void loadPlans(){
        ArrayList<Plan> list = dbc.getAllPlans();
        if(list.isEmpty()){
            Log.d("Plans Activity", "empty list");
            return;
        }
        for(Plan pl: list){
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView name = new TextView(this);
            name.setText(pl.getName());
            TextView date = new TextView(this);
            date.setText(pl.getDate());
            TextView state = new TextView(this);
            state.setText(pl.getState());
            linearLayout.addView(name);
            linearLayout.addView(date);
            linearLayout.addView(state);
            layoutPlans.addView(linearLayout);
            Log.d("Plans Activity", "added note from list");
        }
    }



    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}