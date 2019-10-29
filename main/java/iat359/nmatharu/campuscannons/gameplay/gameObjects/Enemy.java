package iat359.nmatharu.campuscannons.gameplay.gameObjects;

import iat359.nmatharu.campuscannons.utilities.RandomVals;
import processing.core.PVector;

public class Enemy {

    // Object for enemy in the world map sketch

    private int level;
    private PVector worldMapPos;

    // Their speed on the map
    private PVector speed;

    public Enemy(int level, float xCoord, float yCoord) {
        this.level = level;
        worldMapPos = new PVector(xCoord, yCoord);

        // Sets random speed
        speed = new PVector(RandomVals.randomSignedUnit()*RandomVals.randomFloat(0.15f,0.35f),
                                RandomVals.randomSignedUnit()*RandomVals.randomFloat(0.1f,0.2f));
    }

    public int getLevel() {
        return level;
    }

    public float getRotation() {
        return speed.heading();
    }

    public void updatePos() {
        worldMapPos.add(speed);
    }

    public PVector getWorldMapPos() {
        return worldMapPos;
    }

    // Method that returns whether or not a point collides with an enemy's ship based off a reasonable radius
    public boolean touchCollides(float mx, float my) {
        return PVector.dist(new PVector(mx, my), worldMapPos) < 100;
    }
}
