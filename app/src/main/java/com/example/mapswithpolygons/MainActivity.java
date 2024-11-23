package com.example.mapswithpolygons;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mapswithpolygons.databinding.ActivityMainBinding;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity{
    final private String MainActivityLogTag = "MapsMainActivity";

    // Used to load the 'mapswithpolygons' library on application startup.
    static {
        System.loadLibrary("mapswithpolygons");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        GoogleMapImpl myMaps = new GoogleMapImpl(this);
        mapFragment.getMapAsync(myMaps);
        var startDrawButton = findViewById(R.id.startDraw);
        var stopDrawButton = findViewById(R.id.stopDraw);
        var pointCheckButton = findViewById(R.id.pointcheck);
        var polygonIntersectButton = findViewById(R.id.polygonIntersect);
        var polygonAreaButton = findViewById(R.id.polygonArea);
        var polygonClearButton = findViewById(R.id.clearpolygons);

        /// Button mappings
        startDrawButton.setOnClickListener(args -> myMaps.setDrawModeActivationFlag(true));
        stopDrawButton.setOnClickListener(args -> {
            myMaps.drawFinalPolygon();
            myMaps.setDrawModeActivationFlag(false);
            myMaps.resetDrawCount();
            myMaps.clearCoordinatesList();
        });
        pointCheckButton.setOnClickListener(args -> myMaps.checkIfPointIsInsidePolygon());
        polygonIntersectButton.setOnClickListener(args -> myMaps.polygonIntersectionCheck());
        polygonAreaButton.setOnClickListener(args -> myMaps.setCalculatePolygonAreaFlagea(true));
        polygonClearButton.setOnClickListener(args -> myMaps.clearPolygons());
    }

}