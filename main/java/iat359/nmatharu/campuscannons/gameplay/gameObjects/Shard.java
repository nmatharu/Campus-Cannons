package iat359.nmatharu.campuscannons.gameplay.gameObjects;

import iat359.nmatharu.campuscannons.utilities.RandomVals;
import processing.core.PVector;

public class Shard {

    // Graphics object that is essentially pieces of the pirate ships that are
    // smashed in combat-- just for visuals

    // Most things here are fairly self-explanatory and are similar to some other gameObject classes

    private PVector pos, speed;
    private float angle;
    private float rotSpeed;
    private boolean isEnemyShard;

    public Shard(float x, float y, boolean isEnemyShard) {
        pos = new PVector(x, y);
        speed = new PVector(RandomVals.randomFloat(-15, 15), RandomVals.randomFloat(-8, 0));

        angle = 0;
        rotSpeed = RandomVals.randomFloat(-0.3f, 0.3f);
        this.isEnemyShard = isEnemyShard;
    }

    public void update() {
        pos.add(speed);
        angle += rotSpeed;
        speed.y += 0.5;
    }

    public boolean isEnemyShard() {
        return isEnemyShard;
    }

    public boolean shouldRemove(PVector appRes) {
        return  (pos.x < 0) ||
                (pos.y > appRes.x) ||
                (pos.y  > appRes.y);
    }

    public PVector getPos() {
        return pos;
    }

    public float getAngle() {
        return angle;
    }
}
