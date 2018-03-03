package com.atribus.projectbus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Tracking extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        getdevicelocation();
    }

    public void getdevicelocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task location = fusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location currentloc = (Location) task.getResult();
                    LatLng a = new LatLng(currentloc.getLatitude(), currentloc.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(a).title("Marker in Sydney"));
                    mMap.setMyLocationEnabled(true);
                    movecamera(a, 15f);
                    //   Toast.makeText(UserHomeActivity.this, "LAN : "+currentloc.getLongitude()+"\n LON "+currentloc.getLongitude(), Toast.LENGTH_SHORT).show();
                } else {
                    // Toast.makeText(UserHomeActivity.this, "Cannot find location", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void movecamera(LatLng a, float zoom) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(a, zoom));
    }
}
