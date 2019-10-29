package iat359.nmatharu.campuscannons.gameplay.gameObjects;

import processing.core.PVector;

public class Cannonball {

    // Cannonball object for Battle sketch

    private PVector pos;
    private PVector speed;
    private float angle = 0;
    private int framesAlive = 0;

    // whether or not the cannonball was from the enemy
    private boolean fromEnemy;

    public Cannonball(float xCoord, float yCoord, float angleXY, int power, boolean fromEnemy) {
        pos = new PVector(xCoord, yCoord);

        // Shoots the cannonball at a certain angle specified by the normalized XY rotation vector sensor axes
        // and scales this speed by the power of the shot
        speed = PVector.fromAngle((float) ((0.7 - (angleXY + 0.7)) * ((Math.PI / 2) / 0.7f)));
        speed.mult(power);

        this.fromEnemy = fromEnemy;
    }

    // Update position with speed and add a constant value to the vertical velocity every frame to simulate gravity
    // Also add to its angle to make it rotate and increment the number of frames the cannonball has been active for
    public void update() {
        pos.add(speed);
        speed.y += 0.5;
        angle += 0.08f;
        framesAlive++;
    }

    public PVector getPos() {
        return pos;
    }

    public float getAngle() {
        return angle;
    }

    public int getFramesAlive() {
        return framesAlive;
    }

    public boolean isFromEnemy() {
        return fromEnemy;
    }

    // Returns whether or not the cannonball is off the screen (but not off the top because those can come down) and should be removed
    public boolean shouldRemove(PVector appRes) {
        return  (pos.x < 0) ||
                (pos.x > appRes.x) ||
                (pos.y > appRes.y);
    }

    // Returns whether or not a cannonball collides with a player
    // The collision is essentially the bottom half of a circle-- (pos.y >= (p.getBattlePos().y + 40)) ensures that the
    // collision can only take place on the bottom half of the circle in which collision can happen
    public boolean collides(BattlePlayer p) {

        // Cannot collide if it was from the player itself
        if(!isFromEnemy())  return false;

        return (PVector.dist(pos, (new PVector(p.getBattlePos().x, p.getBattlePos().y + 40))) < (30 + 100) &&
                (pos.y >= (p.getBattlePos().y + 40)));
    }

    // Returns whether or not a cannonball collides with an enemy
    public boolean collides(BattleEnemy e) {

        // Enemy cannot collide with one of their own cannonballs
        if(isFromEnemy())   return false;

        return (PVector.dist(pos, (new PVector(e.getBattlePos().x, e.getBattlePos().y + 40))) < (30 + 100) &&
                (pos.y >= (e.getBattlePos().y + 40)));
    }
}
