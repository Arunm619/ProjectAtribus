package com.atribus.projectbus.User;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atribus.projectbus.Model.BusRoutes;
import com.atribus.projectbus.Model.User;
import com.atribus.projectbus.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class UserHomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    String MY_PREFS_NAME = "Mydatabase";
    SharedPreferences prefs;

    Button btn_locate, btn_boarded;
    TextView tv_starttime, tvroutenumber;
    BusRoutes busdb;

    static final int errordialogreq = 101;
    LocationManager locationManager;


    public FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("User Home");
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString("UserObj", "");

        User obj = gson.fromJson(json, User.class);
        if (obj == null) {
            startActivity(new Intent(this, UserRegister.class));

            finish();
        }

        busdb = new BusRoutes();

        setupviews();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (obj != null) {
            tv_starttime.setText(busdb.startingtimes.get(obj.getBusroute()));

            tvroutenumber.setText(getString((R.string.routeNumber)) + obj.getBusroute());
        }

        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserHomeActivity.this, Tracking.class);
                startActivity(i);
            }
        });
        btn_boarded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (isServicesOkay()) {
            // Toast.makeText(this, "Cool", Toast.LENGTH_SHORT).show();
            btn_locate.setEnabled(true);

        }

        PermissionListener permissionlistener = new PermissionListener() {

            @Override
            public void onPermissionGranted() {
                //  Toast.makeText(SignUp.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                boolean a = isLocationEnabled(UserHomeActivity.this);

                //  Toast.makeText(SignUp.this, "Locaiton enabled? "+a , Toast.LENGTH_SHORT).show();
                if (!a) {
                    turngpson();

                }
            }

            @Override
            public void onPermissionDenied(ArrayList <String> deniedPermissions) {
                finish();
            }


        };
        //call permissions
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")

                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

                .check();


        getdevicelocation();
    }
    // Toast.makeText(this, "welcome " + obj.getName(), Toast.LENGTH_SHORT).show();


    public static boolean isLocationEnabled(Context context) {
        int locationMode;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private void turngpson() {
        GoogleApiClient googleApiClient = null;
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(UserHomeActivity.this).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************

        PendingResult <LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback <LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(UserHomeActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    private void setupviews() {
        tvroutenumber = findViewById(R.id.tvroutenumber);
        tv_starttime = findViewById(R.id.tv_starttime);
        btn_boarded = findViewById(R.id.btn_boarded);
        btn_locate = findViewById(R.id.btn_locate);
        btn_locate.setEnabled(false);

    }


    public boolean isServicesOkay() {
        int availablity = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(UserHomeActivity.this);
        if (availablity == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(availablity))

        {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(UserHomeActivity.this, availablity, errordialogreq);
            dialog.show();
        } else {
            Toast.makeText(this, "You cant make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    public void getdevicelocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task location = fusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location currentloc = (Location) task.getResult();
                    //   Toast.makeText(UserHomeActivity.this, "LAN : "+currentloc.getLongitude()+"\n LON "+currentloc.getLongitude(), Toast.LENGTH_SHORT).show();
                } else {
                    // Toast.makeText(UserHomeActivity.this, "Cannot find location", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}