package iat359.nmatharu.campuscannons.gameplay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import processing.android.CompatUtils;
import processing.android.PFragment;
import processing.core.PApplet;

public class WorldMapActivity extends AppCompatActivity {

    // Processing sketch for the world map gameplay sketch
    private PApplet worldMapSketch;

    // Declaring FusedLocationProviderClient for GPS functionality and LocationRequest object for specifications
    // on how we want to get data from the FusedLocationProviderClient
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forcing reverse landscape orientation across entire application -- see SplashActivity for explanation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        // Initializes FusedLocationProviderClient and LocationRequest objects
        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        locationRequest = new LocationRequest();

        // Sets accuracy to highest possible, fastest interval to 1 second and regular to 2 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(2000);

        // Android Studio will give an error if you try to call fusedLocationProviderClient's requestLocationUpdates
        // method without first checking for permissions-- however, we already did that in the activity before this;
        // This activity cannot be accessed without location permissions, but to appease Android Studio, we do this
        // check here and return out in case the permissions are off

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location Permissions must be enabled.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // From Processing for Android Studio documentation, making FrameLayout for the Processing sketch to put in
        // and filling the entire activity's view with it
        FrameLayout myFrame = new FrameLayout(this);
        myFrame.setId(CompatUtils.getUniqueViewId());
        setContentView(myFrame, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Make the world map sketch object, PFragment is from the Processing for Android Studio application and seems to be a
        // class that is specified for making sketches work on Android
        worldMapSketch = new WorldMapSketch(this);
        PFragment myFragment = new PFragment(worldMapSketch);
        myFragment.setView(myFrame, this);

        // Every time our client gets a location updates (based off our 1 - 2 second interval), we'll take the location
        // object that it passes are pass it to our world map sketch object so that it can update the player's position
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                ((WorldMapSketch) worldMapSketch).updateLocation(locationResult.getLastLocation());
            }
        }, getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        worldMapSketch.onResume();
    }

    // From the Processing for Android Studio documentation, it recommends to override the onRequestPermissionsResult
    // in this way to make sure that the app can handle the use of dangerous permissions properly and make sure that
    // the requested permission reaches the app. I don't think I ever used this but I followed the documentation for safety
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(worldMapSketch != null) {
            worldMapSketch.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Same as before, recommended by Processing for Android Studio documentation to make sure that the sketch can handle
    // intents sent by the main activity
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(worldMapSketch != null) {
            worldMapSketch.onNewIntent(intent);
        }
    }

    // Override this to call our sketch's implementation of it so that we can get battle result data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        worldMapSketch.onActivityResult(requestCode, resultCode, data);
    }
}
