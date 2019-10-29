package iat359.nmatharu.campuscannons.gameplay.gameObjects;

import android.util.Log;

import iat359.nmatharu.campuscannons.utilities.GameCalcs;
import iat359.nmatharu.campuscannons.utilities.RandomVals;
import processing.core.PVector;

public class BattleEnemy {

    // Enemy object for battle sketch

    private int level;
    private int health;
    private int maxHealth;

    // These two specify the range of damage, for ex. a cannonball may do 13 - 15 dmg-- 13 is minDamage, 15 is maxDamage
    private int minDamage;
    private int maxDamage;

    // Number of frames it takes to reload
    private int reloadFrames;

    private PVector battlePos;

    public BattleEnemy(int lvl, PVector appRes) {
        level = lvl;
        battlePos = new PVector(appRes.x - 210, 820);

        // Get these values from the GameCalcs
        minDamage = GameCalcs.calcMinDamage(level);
        maxDamage = GameCalcs.calcMaxDamage(level);
        reloadFrames = GameCalcs.calcReloadFrames(level);
        maxHealth = GameCalcs.getHealth(level);

        // Enemies are full health when battle is initiated
        health = maxHealth;
    }

    public void updatePos(int fc) {
        battlePos.x += Math.sin((fc+60)/60f);
    }

    public float getHealthPct() {
        return ((float)(health))/maxHealth;
    }

    // When hit by an player's cannonballs, calculate the damage given the min-max range,
    // increase the damage by the distance scaling value (how far the cannonball travelled),
    // subtract it from health, and if health dropped below 0, set it to 0 for convenience
    // (so we don't see negative numbers briefly on activity)
    public int hitBy(BattlePlayer p, Cannonball c) {
        double damageDealt = RandomVals.randomInt(p.getMinDamage(), p.getMaxDamage());
        double distScaling = ((double) c.getFramesAlive())/35d;
        distScaling = Math.sqrt(distScaling);
        damageDealt *= distScaling;

        health -= (int) damageDealt;

        if(health < 0)  health = 0;
        return (int) damageDealt;
    }

    // Method that returns a Cannonball to be added to the cannonballs list, represents a cannonball being shot by the enemy
    // Calculates a semi-random angle to shoot at and a semi-random power to shoot with
    // The enemy's accuracy is okay at the moment, it can definitely be improved, but it depends on the balance numbers as well
    public Cannonball shoot() {
        float angle = (float)((0.7 - ((RandomVals.randomFloat(0.28f, 0.36f))+0.7))*((Math.PI/2)/0.7f));
        angle += 2*(Math.PI/2 - angle);
        return new Cannonball(battlePos.x - 50, battlePos.y + 40, angle,
                RandomVals.randomInt(26, 28), true);
    }

    public int getLevel() {
        return level;
    }

    public int getHealth() {
        return health;
    }

    public PVector getBattlePos() {
        return battlePos;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getReloadFrames() {
        return reloadFrames;
    }
}
