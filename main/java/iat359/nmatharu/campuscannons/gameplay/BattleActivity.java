package iat359.nmatharu.campuscannons.gameplay;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import processing.android.CompatUtils;
import processing.android.PFragment;
import processing.core.PApplet;

public class BattleActivity extends AppCompatActivity implements SensorEventListener {

    // Processing sketch for battle activity
    private PApplet battleSketch;

    // SensorManager for getting sensor and Sensor object for our Game Rotation Vector Sensor
    private SensorManager sensorManager;
    private Sensor gameRotSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forcing reverse landscape orientation across entire application -- see SplashActivity for explanation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        // From Processing for Android Studio documentation, making FrameLayout for the Processing sketch to put in
        // and filling the entire activity's view with it
        FrameLayout myFrame = new FrameLayout(this);
        myFrame.setId(CompatUtils.getUniqueViewId());
        setContentView(myFrame, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Make the battle sketch object, PFragment is from the Processing for Android Studio application and seems to be a
        // class that is specified for making sketches work on Android-- also passing in the extras that this intent was launched with
        battleSketch = new BattleSketch(this, getIntent().getExtras());
        PFragment myFragment = new PFragment(battleSketch);
        myFragment.setView(myFrame, this);

        // get sensor service and get the default sensor for Game Rotation Vector
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gameRotSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        // If the user's device doesn't have a game rotation vector sensor, they can't battle ):
        // and we have to finish the activity and use a Toast to let them know
        if(gameRotSensor == null) {
            finish();
            Toast.makeText(this, "Device does not have a game rotation vector sensor-- cannot battle.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Acquire hardware late-- (onResume()), also low delay because it's a game
        sensorManager.registerListener(this, gameRotSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {

        // Release hardware early-- (onPause())
        sensorManager.unregisterListener(this, gameRotSensor);
        super.onPause();
    }

    // We want certain behaviour for the battle when the back button is pressed, so we'll call the sketch's
    // implementation of the method before calling super
    @Override
    public void onBackPressed() {
        battleSketch.onBackPressed();
        super.onBackPressed();
    }

    // Whenever our rotation vector sensor updates, call the updateSensor() method in our sketch and pass the values in
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            BattleSketch bSketch = (BattleSketch) battleSketch;
            bSketch.updateSensor(sensorEvent.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    // From the Processing for Android Studio documentation, it recommends to override the onRequestPermissionsResult
    // in this way to make sure that the app can handle the use of dangerous permissions properly and make sure that
    // the requested permission reaches the app. I don't think I ever used this but I followed the documentation for safety
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(battleSketch != null) {
            battleSketch.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Same as before, recommended by Processing for Android Studio documentation to make sure that the sketch can handle
    // intents sent by the main activity
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(battleSketch != null) {
            battleSketch.onNewIntent(intent);
        }
    }
}
