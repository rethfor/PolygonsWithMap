package com.example.mapswithpolygons;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoogleMapImpl implements OnMapReadyCallback {

    private GoogleMap myMap = null;
    static final int LEAST_POSSIBLE_POLYGON_EDGE_SIZE = 3;
    private final String GoogleMapImplLogTag = "GoogleMapImpl";
    private boolean drawModeActivationFlag = false;
    private final List<LatLng> coordinates = new ArrayList<>();
    private final List<Polygon> polygons = new ArrayList<>();
    private final List<Polyline> polylines = new ArrayList<>();
    private boolean pointInsidePolygonCheckModeFlag = false;
    private boolean calculatePolygonAreaFlag = false;
    private Marker marker = null;
    private int drawCount = 0;
    private Context context = null;

    GoogleMapImpl(Context context) {
        this.context = context;
    }

    public void setDrawModeActivationFlag(boolean drawModeActivationFlag) {
        this.drawModeActivationFlag = drawModeActivationFlag;
        pointInsidePolygonCheckModeFlag = false;
        calculatePolygonAreaFlag = false;
        if(!polygons.isEmpty()) {
            polygons.forEach(polygon -> polygon.setClickable(false));
        }
    }

    public boolean getDrawModeActivationFlag() {
        return drawModeActivationFlag;
    }

    public void resetDrawCount() {
        drawCount = 0;
    }

    public void clearCoordinatesList() {
        if (!coordinates.isEmpty()) coordinates.clear();
    }

    public void drawFinalPolygon() {
        if(myMap == null) return;
        if(coordinates.size() >= LEAST_POSSIBLE_POLYGON_EDGE_SIZE) {
            Random rnd = new Random();
            int color = Color.argb(155, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            Polygon polygon = myMap.addPolygon(new PolygonOptions()
                    .addAll(coordinates)
                    .fillColor(color));
            polygon.getPoints().forEach(latLng -> loadPolygonEdges(latLng.latitude, latLng.longitude));
            loadNativeGlobalPoylgons();
            polygons.add(polygon);
        }
        if (polylines.isEmpty()) return;
        polylines.forEach(Polyline::remove);
        polylines.clear();
    }

    public void polygonIntersectionCheck() {
        try {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "ANY POLYGON INTERSECTION: " + polygonsIntersectionCheck(), duration);
            toast.show();
        } catch (Exception e) {
            Log.e(GoogleMapImplLogTag, e.getMessage());
        }
    }

    public void checkIfPointIsInsidePolygon() {
        if(!drawModeActivationFlag && !pointInsidePolygonCheckModeFlag) {
            pointInsidePolygonCheckModeFlag = true;
            calculatePolygonAreaFlag = false;
            polygons.forEach(polygon -> polygon.setClickable(false));
        } else if(pointInsidePolygonCheckModeFlag) {
            polygons.forEach(polygon -> polygon.setClickable(true));
            pointInsidePolygonCheckModeFlag = false;
        }
    }

    public void setCalculatePolygonAreaFlagea(boolean flag) {
        polygons.forEach(polygon -> polygon.setClickable(true));
        calculatePolygonAreaFlag = flag;
        pointInsidePolygonCheckModeFlag = false;
    }

    public void clearPolygons() {
        polygons.forEach(Polygon::remove);
        polygons.clear();
        clearNativePolygons();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setOnMapClickListener(latLng -> {
            Log.d(GoogleMapImplLogTag, "clicked: " + latLng);
            if(drawModeActivationFlag) {
                coordinates.add(latLng);
            }
            if (coordinates.size() > 1 && drawModeActivationFlag) {
                drawCount++;
                polylines.add(myMap.addPolyline(new PolylineOptions()
                        .add(coordinates.get(drawCount-1), coordinates.get(drawCount))
                        .width(5)
                        .color(Color.RED)));
            }
            if (pointInsidePolygonCheckModeFlag) {
                if(marker != null) marker.remove();
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("point"));
                try {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, "IS POINT INSIDE POLYGON: " + isPointInsidePolygonCheck(latLng.latitude, latLng.longitude), duration);
                    toast.show();
                } catch (Exception e) {
                    Log.e(GoogleMapImplLogTag, e.getMessage());
                }
            }
        });

        myMap.setOnPolygonClickListener(polygon -> {
            if(!calculatePolygonAreaFlag) return;
            polygon.getPoints().forEach(latLng -> loadPolygonEdges(latLng.latitude, latLng.longitude));
            try {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "POLYGON AREA: " + calculatePolygonAreaNative(), duration);
                toast.show();
            } catch (Exception e) {
                Log.e(GoogleMapImplLogTag, e.getMessage());
            }
        });
    }

    private native void loadPolygonEdges(double polygonLat, double polygonLon);
    private native void loadNativeGlobalPoylgons();
    private native boolean isPointInsidePolygonCheck(double pointLat, double pointLon);
    private native boolean polygonsIntersectionCheck();
    private native double calculatePolygonAreaNative();
    private native void clearNativePolygons();

}
