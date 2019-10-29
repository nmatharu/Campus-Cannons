package iat359.nmatharu.campuscannons.gameplay.gameObjects;

import android.content.SharedPreferences;

import iat359.nmatharu.campuscannons.utilities.Constants;
import iat359.nmatharu.campuscannons.utilities.GameCalcs;
import processing.core.PVector;

public class Player {

    // Object for player in the world map sketch

    private String name;

    private int maxHealth;
    private int level;
    private int XP;
    private int coins;

    // heading is the current angle that the ship faces, targetHeading is used for smooth motion
    private float heading;
    private float targetHeading;

    // same idea, worldMapPos is player's actual current position on map, targetPos is used for smoothing
    private PVector worldMapPos;
    private PVector targetPos;

    // smoothing amount
    private final int SMOOTHING = 30;

    // value to multiply latitude/longitude by the get usable values for position
    private final int LOCATION_SCALING = 1000000;

    public Player(SharedPreferences prefs) {

        // Initialize fields based on info from shared preferences
        name = prefs.getString("name", Constants.DEFAULT_NAME);
        XP = prefs.getInt("XP", Constants.DEFAULT_VAL);
        coins = prefs.getInt("coins", Constants.DEFAULT_VAL);
        updateLevel();

        // Default location of player
        worldMapPos = new PVector(2250, 1600);
        targetPos = new PVector(2250, 1600);

        maxHealth = GameCalcs.getHealth(level);
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getCoins() {
        return coins;
    }

    public int getXP() {
        return XP;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public PVector getWorldMapPos() {
        return worldMapPos;
    }

    public float getHeading() {
        return heading;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setXP(int XP) {
        this.XP = XP;
    }

    public void updateLevel() {
        level = GameCalcs.calcLevel(XP);
    }

    // Method that updates the Player's position on the map based on the baseline and current latitude/longitude
    public void updatePos(double baseLat, double baseLng, double currLat, double currLng) {

        // Sets the position that the player should be moving to
        targetPos.x = (float)(2250 + -6*LOCATION_SCALING*(baseLat - currLat));
        targetPos.y = (float)(1600 + -4*LOCATION_SCALING*(baseLng - currLng));

        // Gets the difference between where the player was last frame and this frame
        float xDifference = ((worldMapPos.x*SMOOTHING + targetPos.x)/(SMOOTHING + 1)) - worldMapPos.x;
        float yDifference = ((worldMapPos.y*SMOOTHING + targetPos.y)/(SMOOTHING + 1)) - worldMapPos.y;

        // Calculates the heading of this difference and adds PI/2 and sets this value to the angle that the player should be rotating towards
        targetHeading = (new PVector(xDifference, yDifference)).heading() + (float)Math.PI/2;

        // Averaging the current values and the target values with heavy emphasis on the current values allows for
        // smooth change from one value to the next-- this allows for smooth angle turning and smooth position to position
        // movement, which is especially important when the GPS updates every 1-2s and the movement is jerky
        heading = (heading*SMOOTHING + targetHeading)/(SMOOTHING + 1);
        worldMapPos.x = (worldMapPos.x*SMOOTHING + targetPos.x)/(SMOOTHING + 1);
        worldMapPos.y = (worldMapPos.y*SMOOTHING + targetPos.y)/(SMOOTHING + 1);
    }

    // Method that returns whether or not a point collides with the player's ship based off a reasonable radius
    public boolean touchCollides(float mx, float my) {
        return PVector.dist(new PVector(mx, my), worldMapPos) < 80;
    }

    // As mentioned earlier, for manual testing
    public void setWorldMapPos(float x, float y) {
        // Sets the position that the player should be moving to
        worldMapPos = new PVector(x, y);
    }
}
