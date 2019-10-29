package iat359.nmatharu.campuscannons.gameplay.gameObjects;

import iat359.nmatharu.campuscannons.utilities.GameCalcs;
import iat359.nmatharu.campuscannons.utilities.RandomVals;
import processing.core.PVector;

public class BattlePlayer {

    // Player object for the battle sketch

    private String name;
    private int health;
    private int maxHealth;
    private int level;

    // These two specify the range of damage, for ex. a cannonball may do 13 - 15 dmg-- 13 is minDamage, 15 is maxDamage
    private int minDamage;
    private int maxDamage;

    // Number of frames it takes to reload
    private int reloadFrames;

    private PVector battlePos;

    public BattlePlayer(String n, int maxHP, int lvl, String cannonID) {
        name = n;

        maxHealth = maxHP;
        health = maxHealth;

        level = lvl;

        // Get the info about the players current cannon
        String[] cannonVals = GameCalcs.getCannonVals(cannonID).split(",");

        // Get these values from the GameCalcs
        minDamage = Integer.valueOf(cannonVals[0]);
        maxDamage = Integer.valueOf(cannonVals[1]);
        reloadFrames = Integer.valueOf(cannonVals[2]);

        battlePos = new PVector(210, 820);
    }

    public void updatePos(int fc) {
        battlePos.x += Math.sin(fc/60f);
    }

    public float getHealthPct() {
        return ((float)(health))/maxHealth;
    }

    // When hit by an enemy's cannonballs, calculate the damage given the min-max range,
    // increase the damage by the distance scaling value (how far the cannonball travelled),
    // subtract it from health, and if health dropped below 0, set it to 0 for convenience
    // (so we don't see negative numbers briefly on activity)
    public int hitBy(BattleEnemy e, Cannonball c) {
        double damageDealt = RandomVals.randomInt(e.getMinDamage(), e.getMaxDamage());
        double distScaling = ((double) c.getFramesAlive())/35d;
        distScaling = Math.sqrt(distScaling);
        damageDealt *= distScaling;

        health -= (int) damageDealt;

        if(health < 0)  health = 0;
        return (int) damageDealt;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getLevel() {
        return level;
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
