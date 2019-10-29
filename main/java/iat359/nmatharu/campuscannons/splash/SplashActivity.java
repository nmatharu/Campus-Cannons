package iat359.nmatharu.campuscannons.splash;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import processing.android.CompatUtils;
import processing.android.PFragment;
import processing.core.PApplet;

public class SplashActivity extends AppCompatActivity {

    /*      Start here! This is technically the Main Activity.

            My comments will generally be briefer than normal cause there is a hefty amount of code to get through.
            I generally will just put comments a block for explanation and not explain what each line does, if you
            would like me to explain any certain implementation in or outside of class, I am more than happy to--
            it was just not feasible to include in depth comments for all of this code.

            Enjoy!
     */

    // Processing sketch for splash screen
    private PApplet splashSketch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Many games force a landscape orientation-- I decided to force reverse landscape so that the sketch object
        // would't have to be recreated each time the player changed the landscape orientation of their device-- it would
        // slow down the app considerably.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        // From Processing for Android Studio documentation, making FrameLayout for the Processing sketch to put in
        // and filling the entire activity's view with it
        FrameLayout myFrame = new FrameLayout(this);
        myFrame.setId(CompatUtils.getUniqueViewId());
        setContentView(myFrame, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Make the sketch object, PFragment is from the Processing for Android Studio application and seems to be a
        // class that is specified for making sketches work on Android
        splashSketch = new SplashSketch(this);
        PFragment myFragment = new PFragment(splashSketch);
        myFragment.setView(myFrame, this);
    }

    // From the Processing for Android Studio documentation, it recommends to override the onRequestPermissionsResult
    // in this way to make sure that the app can handle the use of dangerous permissions properly and make sure that
    // the requested permission reaches the app. I don't think I ever used this but I followed the documentation for safety
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(splashSketch != null) {
            splashSketch.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Same as before, recommended by Processing for Android Studio documentation to make sure that the sketch can handle
    // intents sent by the main activity
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(splashSketch != null) {
            splashSketch.onNewIntent(intent);
        }
    }
}
