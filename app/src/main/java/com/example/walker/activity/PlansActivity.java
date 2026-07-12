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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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
    private Button addBtn = null;
    private Button plansBtn = null;
    private Button settingsBtn = null;

    private ScrollView scrollView = null;

    private DatabaseController dbc = new DatabaseController(getApplicationContext());

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
        scrollView=findViewById(R.id.scrollPlans);
        if (addBtn == null | plansBtn == null | scrollView == null| settingsBtn == null) {
            Log.e("PlansActivity", "addBtn or ScrollView or plansBtn or settingsBtn is null");
        }


        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("PlansActivity", "clicked add plan");
                Intent addPlanIntent = new Intent(PlansActivity.this, AddPlanActivity.class);
                startActivity(addPlanIntent);
                //finish();
        }});



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
            name.setText(pl.getDate());
            TextView state = new TextView(this);
            name.setText(pl.getState());
            Log.d("Plans Activity", "added note from list");

        }

        return;
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