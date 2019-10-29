package iat359.nmatharu.campuscannons.splash;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import iat359.nmatharu.campuscannons.menu.MenuActivity;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

public class SplashSketch extends PApplet {

    private static final int GAME_WIDTH_PIXELS = 1920;
    private static final int GAME_HEIGHT_PIXELS = 1080;
    private static final int GAME_FRAME_RATE = 60;

    private Context context;

    // app res is the target resolution of the sketch (1920 x 1080)
    // device res is the resolution of the player's device
    // we use these two PVectors to scale the sketch up/down to fit the device
    private PVector appRes;
    private PVector deviceRes;

    private PImage splashIcon;

    // Positions of the splash icon
    private PVector iconPos;
    private PVector iconTargetPos;

    private PFont dimbo;

    public SplashSketch(Context c) {
        super();
        context = c;

        appRes = new PVector(GAME_WIDTH_PIXELS, GAME_HEIGHT_PIXELS);

        // Get device resolution
        deviceRes = new PVector(
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels);

        iconPos = new PVector(appRes.x/2, 2*appRes.y);
        iconTargetPos = new PVector(appRes.x/2, appRes.y/2);
    }

    // Sets size of Processing sketch to the device resolution and then will scale 1920x1080 components to fit that
    // P2D is used so that the Processing sketch can utilize the graphic acceleration capabilities of OpenGL without
    // having to use any OpenGL code
    @Override
    public void settings() {
        size((int)deviceRes.x, (int)deviceRes.y, P2D);
    }

    @Override
    public void setup() {
        frameRate(GAME_FRAME_RATE);

        // Loads images and fonts from assets folder (see milestone report, files used for Processing MUST be in the assets folder)
        splashIcon = loadImage("splash/skull.png");
        dimbo = createFont("fonts/dimbo.ttf", 64f);

        // Anti-aliasing
        smooth(4);

        imageMode(CENTER);
        textFont(dimbo);
    }

    @Override
    public void draw() {

        // Once this number of frames have passed, call the method to kick us out of the splash activity, and exit() to close sketch
        if(frameCount >= 385) {
            finishSplash();
            exit();
            return;
        }

        // Scale to match resolution
        pushMatrix();
        scale(width/appRes.x, height/appRes.y);

        // Graphics stuff and math below, again, feel free to ask me anything about this or my process, it's just absurd to comment
        // all of it (especially in the other sketches which have way more)

        background(255 - (iconPos.y-540)/8.45f);

        pushMatrix();
        translate(0, 75);

        fill(0, 0, 0, (float)(Math.pow((255-(iconPos.y-540)/8.45f), 4)/(Math.pow(255,3))));
        textMode(CENTER);
        textAlign(CENTER);
        text("\"I've-already-made-2-pirate-games-this-semester\" GAMES", iconTargetPos.x, iconTargetPos.y + 120);

        iconPos = new PVector((29*iconPos.x + iconTargetPos.x)/30, (29*iconPos.y + iconTargetPos.y)/30);
        image(splashIcon, iconPos.x, iconPos.y - 150);

        popMatrix();

        if(frameCount > 300) {
            fill(0, 0, 0, 3*(frameCount-300));
            rect(0, 0, appRes.x, appRes.y);
        }

        popMatrix();

        // I actually realized later I don't need to do this because frameCount increments automatically, but removing
        // this line changes the timing of my graphical splash screen animation so I just left it, doesn't change much,
        // removing it would just half the speed of the animation
        frameCount++;
    }

    public void finishSplash() {

        // Starts the main menu activity, sets the explicit intent flag to clear the activity stack so that the splash activity
        // doesn't exist anymore and so that the user cannot navigate back to it (like most splash screens)
        Intent i = new Intent(context, MenuActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }
}
