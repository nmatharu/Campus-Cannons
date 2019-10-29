package iat359.nmatharu.campuscannons.gameplay.gameObjects;

import processing.core.PVector;

public class Hub {

    // Object for Hub on world map sketch

    private PVector worldMapPos;
    private String hubID;

    public Hub(String ID, float xCoord, float yCoord) {
        worldMapPos = new PVector(xCoord, yCoord);

        // Hub ID is a unique identifier that has information attached to it, like the items the hub has, the levels of the
        // enemies it spawns, etc.
        hubID = ID;
    }

    public String getHubID() {
        return hubID;
    }

    public PVector getWorldMapPos() {
        return worldMapPos;
    }

    // Method that returns whether or not a point collides with a hub based off a reasonable radius
    public boolean touchCollides(float mx, float my) {
        return PVector.dist(new PVector(mx, my), worldMapPos) < 125;
    }
}
