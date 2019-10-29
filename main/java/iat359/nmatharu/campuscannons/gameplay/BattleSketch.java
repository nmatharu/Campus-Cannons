package iat359.nmatharu.campuscannons.gameplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

import java.util.ArrayList;

import iat359.nmatharu.campuscannons.R;
import iat359.nmatharu.campuscannons.gameplay.gameObjects.BattleEnemy;
import iat359.nmatharu.campuscannons.gameplay.gameObjects.BattlePlayer;
import iat359.nmatharu.campuscannons.gameplay.gameObjects.Cannonball;
import iat359.nmatharu.campuscannons.gameplay.gameObjects.Shard;
import iat359.nmatharu.campuscannons.utilities.Constants;
import iat359.nmatharu.campuscannons.utilities.RandomVals;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

public class BattleSketch extends PApplet {

    /*  Processing sketch that handles interactions the mechanics of battling between two ships
        Decent amount of code here, will try to be as concise with comments as possible
        Again, leaving out most graphics handling stuff cause it isn't specific to what we're
        learning in Android studio and most people are familiar with it after working with Processing
     */

    private static final int GAME_WIDTH_PIXELS = 1920;
    private static final int GAME_HEIGHT_PIXELS = 1080;
    private static final int GAME_FRAME_RATE = 60;

    private Context context;

    // app res is the target resolution of the sketch (1920 x 1080)
    // device res is the resolution of the player's device
    // we use these two PVectors to scale the sketch up/down to fit the device
    private PVector appRes;
    private PVector deviceRes;

    private PFont dimbo;
    private PFont dimboDmg;

    private PImage bg;
    private PImage wavesA;
    private PImage wavesB;

    private PImage playerShip;
    private PImage enemyShip;

    private PImage playerShard;
    private PImage enemyShard;

    private PImage cannonballImg;
    private PImage enemyCannonballImg;
    private PImage aimGraphic;

    // Factor that controls the amount the screen shakes
    private int shake = 0;

    // the player object for battling
    private BattlePlayer player;
    private String cannonID;

    // Countdown frames for the player's reload
    private int playerReloadFrames;

    // Booleans for battle mechanics
    private boolean chargeCannon = false;
    private boolean reloading = false;

    // Player's cannon shoots between 10-40 power, 10 is the lowest and is the default
    private int power = 10; /// 10- 40

    // the enemy object for battling
    private BattleEnemy enemy;

    // ints that represent the amount of damage that player took last, used for
    // drawing the damage number to the screen
    private int playerTakeDamage;
    private int enemyTakeDamage;

    // Countdowns for graphically making those damage numbers fade from the screen
    private int playerTakeDmgCountdown;
    private int enemyTakeDmgCountdown;

    // ArrayList of the cannonball objects used as projectiles
    private ArrayList<Cannonball> cannonballs = new ArrayList<>();

    // ArrayList of shards for visuals for player's ships being damaged
    private ArrayList<Shard> shards = new ArrayList<>();

    // float value that represents the normalized X and Y axes of the player's Rotation Vector Sensor
    private float sensorXY;

    // Variable that represents control of the battle-- once the battle is over, the battle result will appear
    // on screen and we don't want the player to do anything at that time, so we'll use a flag to control that
    // The ending frames are for the countdown of this ending sequence and the endingResult holds the result of the battle
    private boolean control = true;
    private int endingFrames = 120;
    private String endingResult;

    // SoundPool object that we can use to play MULTIPLE sounds simultaneously (MediaPlayer would not work for this)
    // IDs for the all the sounds we might want to play simultaneously
    private SoundPool soundPool;
    private int soundID_fire1;
    private int soundID_fire2;
    private int soundID_fire3;
    private int soundID_fire4;
    private int soundID_fire5;
    private int soundID_smash1;
    private int soundID_smash2;
    private int soundID_smash3;
    private int soundID_dingding;

    public BattleSketch(Context c, Bundle extras) {
        super();
        context = c;

        appRes = new PVector(GAME_WIDTH_PIXELS, GAME_HEIGHT_PIXELS);

        // Get device resolution
        deviceRes = new PVector(
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels);

        SharedPreferences preferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        cannonID = preferences.getString("cannonID", Constants.DEFAULT_CANNON);

        // Makes BattlePlayer object based off of the Bundle of info given to this intent when launched from WorldMapSketch
        player = new BattlePlayer(
                    extras.getString("playerName"),
                    extras.getInt("playerMaxHealth"),
                    extras.getInt("playerLevel"),
                    cannonID);
        playerReloadFrames = player.getReloadFrames();

        // Same with BattleEnemy object
        enemy = new BattleEnemy(
                    extras.getInt("enemyLevel"),
                    appRes);

        // Initialize the soundPool with 9 streams for the 9 potential sources, set its type to music,
        // and load each of the sounds we want into the IDs
        soundPool = new SoundPool(9, AudioManager.STREAM_MUSIC, 0);
        soundID_fire1 = soundPool.load(context, R.raw.fire_1, 0);
        soundID_fire2 = soundPool.load(context, R.raw.fire_2, 0);
        soundID_fire3 = soundPool.load(context, R.raw.fire_3, 0);
        soundID_fire4 = soundPool.load(context, R.raw.fire_4, 0);
        soundID_fire5 = soundPool.load(context, R.raw.fire_5, 0);
        soundID_smash1 = soundPool.load(context, R.raw.smash_1, 0);
        soundID_smash2 = soundPool.load(context, R.raw.smash_2, 0);
        soundID_smash3 = soundPool.load(context, R.raw.smash_3, 0);
        soundID_dingding = soundPool.load(context, R.raw.dingding, 0);
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

        // Anti-aliasing
        smooth(4);

        // Loads images and fonts from assets folder (see milestone report, files used for Processing MUST be in the assets folder)

        dimbo = createFont("fonts/dimbo.ttf", 80f);
        dimboDmg = createFont("fonts/dimbo.ttf", 360f);

        bg = loadImage("battle/battle_bg.png");
        wavesA = loadImage("battle/battle_waves_a.png");
        wavesB = loadImage("battle/battle_waves_b.png");

        playerShip = loadImage("battle/player_ship.png");
        playerShard = loadImage("battle/shards/player_shard.png");

        enemyShip = loadImage("battle/enemy_ship_lv" + enemy.getLevel() + ".png");
        enemyShard = loadImage("battle/shards/enemy_shard_lv" + enemy.getLevel() + ".png");

        cannonballImg = loadImage("battle/cannonballs/c_" + cannonID + ".png");
        enemyCannonballImg = loadImage("battle/cannonballs/enemy.png");

        aimGraphic = loadImage("battle/aim_graphic.png");

        textFont(dimbo);

        noStroke();
        imageMode(CENTER);
    }

    @Override
    public void draw() {

        // Scale to match resolution
        pushMatrix();
        scale(width/appRes.x, height/appRes.y);

        // If shake>0, the screen will translate in a random directional proportional to it and "shake" over several frames
        translate(0.5f*RandomVals.randomInt(-shake, shake), 0.5f*RandomVals.randomInt(-shake, shake));

        pushMatrix();

        image(bg, appRes.x/2, appRes.y/2);

        image(wavesA, 890 + (float)(30 * Math.sin(frameCount/60f)), 1020 + (float)(5 * Math.sin(frameCount/120f)));

        // Updates the player position based off of frameCount (slightly moving back and forth due to the waves, mainly for aesthetics)
        player.updatePos(frameCount);

        // If player is currently charging up the power of their cannon shot, increment the power,
        // but if the power goes above 40, restart it back at 10
        if(chargeCannon) {
            power++;
            if(power > 40)  power = 10;
        }

        // If the player is currently reloading their cannon, decrement the number of reload frames they have remaining,
        // and if they have none remaining, they are no longer reloading
        if(reloading) {
            playerReloadFrames--;
            if(playerReloadFrames < 0)  reloading = false;
        }
        drawPlayer();

        // Updates the enemy position based off of frameCount (slightly moving back and forth due to the waves, mainly for aesthetics)
        enemy.updatePos(frameCount);

        // The enemy shoots cannonballs after a certain interval defined by their reload frames
        if(frameCount%enemy.getReloadFrames() == 0 && control) {
            cannonballs.add(enemy.shoot());

            // Play cannon fire sound effect
            playFire();
        }
        drawEnemy();

        // Damage calc also done in this method, see below
        updateCannonballs();

        // Method that updates all the shard visuals
        updateShards();

        // After calculating damage, we want to check if either ship has lost all their health
        // If player is <= 0 health, call the finish battle method with the result "DEFEAT", similarly for enemy health <= 0
        if(player.getHealth() <= 0) {
            preFinishBattle("DEFEAT");
        }
        if(enemy.getHealth() <= 0) {
            preFinishBattle("VICTORY");
        }

        // The shake value decays
        if(shake > 0)   shake--;

        image(wavesB, 1035 + (float)(-30 * Math.sin(frameCount/60f)), 1060 + (float)(-5 * Math.sin(frameCount/120f)));
        popMatrix();

        drawUI();

        // The player has lost control, that means the battle is over, and we decrement the frames to represent the time
        // of this sequence and display either "VICTORY" or "DEFEAT" on the screen
        if(!control) {
            endingFrames--;

            textSize(256f);
            textAlign(CENTER);
            fill(255, 15 + 2*endingFrames);
            text(endingResult, appRes.x/2, appRes.y/2);

            // When our ending sequence is finished, we call the method to finish this activity
            if(endingFrames <= 0) {
                finishBattle(endingResult);
            }
        }

        popMatrix();
    }

    // Method for drawing all the UI elements
    private void drawUI() {

        // Gray backdrops to all the rectangles
        rectMode(CENTER);
        noStroke();
        fill(150);
        rect(appRes.x/2, appRes.y - 60, 400, 80);   // Power/reload bar
        rect(20 + 400, 60, 800, 80);                // Player HP bar
        rect(appRes.x - 20 - 400, 60, 800, 80);     // Enemy HP bar

        // Colour fills for each of the rectangles
        rectMode(CORNER);

        // If the player is reloading, the fill is blue and shrinks as the reload completes
        // If the player is charging, the bar fills with orange based on how high their power is
        if(reloading) {
            fill(50, 50, 255);
            rect(appRes.x/2 - 200, appRes.y - 100, 400*((float)playerReloadFrames/(player.getReloadFrames())), 80);
        } else {
            fill(255, 150, 0);
            rect(appRes.x/2 - 200, appRes.y - 100, 400*((power - 10)/30f), 80);
        }

        fill(0, 200, 0);
        rect(20, 20, 800f*player.getHealthPct(), 80);   // Player HP bar

        fill(150, 0, 0);
        rect(appRes.x - 20 - 800f*enemy.getHealthPct(), 20, 800f*enemy.getHealthPct(), 80); // Enemy HP bar


        // Stroke overlay for each of the rectangles
        strokeWeight(8f);
        stroke(86, 61, 35);
        rectMode(CENTER);
        noFill();
        rect(appRes.x/2, appRes.y - 60, 400, 80);   // Power/reload bar
        rect(20 + 400, 60, 800, 80);                // Player HP bar
        rect(appRes.x - 20 - 400, 60, 800, 80);     // Enemy HP bar

        fill(255);

        // Text above the power/reloading bar
        textAlign(CENTER);
        textSize(60f);
        if(reloading) {
            text("RELOADING", appRes.x/2, appRes.y - 40);
        } else {
            text("POWER", appRes.x/2, appRes.y - 40);
        }

        // Text on HUD for player HP above bars, player name, and player level

        textAlign(LEFT);
        textSize(96f);
        text(player.getName(), 20, 195);
        textAlign(RIGHT);
        text("ENEMY", appRes.x - 20, 195);

        textSize(64f);
        textAlign(LEFT);
        text("LV. " + player.getLevel(), 20, 254);
        textAlign(RIGHT);
        text("LV. " + enemy.getLevel(), appRes.x - 20, 254);

        textSize(72f);
        textAlign(LEFT);
        text(player.getHealth() + " HP", 35, 87);
        textAlign(RIGHT);
        text(enemy.getHealth() + " HP", appRes.x - 35, 87);

        // Displays the damage numbers above players when they take a hit
        // playerTakeDmgCountdown is set in the method below
        // Number with the damage the player just took appears above their ship and fades away

        textFont(dimboDmg);
        textAlign(CENTER);

        if(playerTakeDmgCountdown > 0)  playerTakeDmgCountdown--;
        fill(255, playerTakeDmgCountdown*3);
        text(playerTakeDamage, 240, 640);

        if(enemyTakeDmgCountdown > 0)   enemyTakeDmgCountdown--;
        fill(255, enemyTakeDmgCountdown*3);
        text(enemyTakeDamage, appRes.x - 240, 640);

        textFont(dimbo);
    }

    private void updateCannonballs() {

        // Similar removal method as TouchGraphics, see WorldMapActivity
        // index of cannonball object to remove
        int cannonballToRemove = -1;
        for(int i = 0; i <cannonballs.size(); i++) {

            Cannonball c = cannonballs.get(i);
            c.update();
            pushMatrix();
            drawCannonball(c);
            popMatrix();
            if(c.shouldRemove(appRes))    cannonballToRemove = i;

            // If a cannonball collides with an enemy, they take damage based on the player's attributes and
            // their damage countdown is set to 85 for the text graphics
            if(c.collides(enemy)) {
                shake = 30;
                spawnShards(c.getPos(), true);
                playSmash();
                cannonballToRemove = i;
                enemyTakeDamage = enemy.hitBy(player, c);
                enemyTakeDmgCountdown = 85;
            }

            // If a cannonball collides with an player, they take damage based on the enemy's attributes and
            // their damage countdown is set to 85 for the text graphics
            if(c.collides(player)) {
                shake = 30;
                spawnShards(c.getPos(), false);
                playSmash();
                cannonballToRemove = i;
                playerTakeDamage = player.hitBy(enemy, c);
                playerTakeDmgCountdown = 85;
            }
        }
        if(cannonballToRemove != -1)    cannonballs.remove(cannonballToRemove);
    }

    // Drawing the player, mostly graphical stuff...
    private void drawPlayer() {
        pushMatrix();

        // Translate to their position so we can draw things at 0, 0
        translate(player.getBattlePos().x, player.getBattlePos().y);

        pushMatrix();
        translate(50, 40);

        // Rotate to the angle that the trajectory of the player is pointing to and draw the "aimGraphic"
        // which is a rectangle that fades in opacity and shows the direction the player will fire in
        rotate((float)((0.7 - (sensorXY+0.7))*((Math.PI/2)/0.7f)));

        translate(300, 0);
        image(aimGraphic, 0, 0);
        popMatrix();

        // Rotate by a small angle to tilt back and forth and look like the ship is on water (just for aesthetics)
        rotate((float)(Math.sin(frameCount/60f) * 0.1));
        image(playerShip, 0, 0);
        popMatrix();
    }

    // Drawing the enemy, mostly graphical stuff...
    private void drawEnemy() {
        pushMatrix();

        // Translate to their position so we can draw things at 0, 0
        translate(enemy.getBattlePos().x, enemy.getBattlePos().y);

        // Rotate by a small angle to tilt back and forth and look like the ship is on water (just for aesthetics)
        rotate((float)(Math.sin(frameCount/60f) * 0.1));

        // Mirror enemy ships
        pushMatrix();
        scale(-1, 1);

        image(enemyShip, 0, 0);

        popMatrix();
        popMatrix();
    }

    private void drawCannonball(Cannonball c) {
        pushMatrix();
        translate(c.getPos().x, c.getPos().y);
        rotate(c.getAngle());
        if(c.isFromEnemy())     image(enemyCannonballImg, 0, 0);
        if(!c.isFromEnemy())    image(cannonballImg, 0, 0);
        popMatrix();
    }

    // Spawns 5 shards at the position of ship impact
    private void spawnShards(PVector pos, boolean onEnemy) {
        for(int i = 0; i < 5; i++) {
            shards.add(new Shard(pos.x, pos.y, onEnemy));
        }
    }

    // Same removal method as always, and also updates and calls method for drawing shards
    private void updateShards() {
        int shardToRemove = -1;
        for(int i = 0; i < shards.size(); i++) {
            Shard s = shards.get(i);
            s.update();
            drawShard(s);
            if(s.shouldRemove(appRes))  shardToRemove = i;
        }
        if(shardToRemove != -1) shards.remove(shardToRemove);
    }

    // Draws shards
    private void drawShard(Shard s) {

        pushMatrix();
        translate(s.getPos().x, s.getPos().y);
        rotate(s.getAngle());
        rectMode(CENTER);
        if(s.isEnemyShard())    image(enemyShard, 0, 0, 1.6f*enemyShard.width, 1.6f*enemyShard.height);
        if(!s.isEnemyShard())   image(playerShard, 0, 0, 1.6f*playerShard.width, 1.6f*playerShard.height);
        popMatrix();
    }

    // Plays boxing knockout bell sound effect
    private void playDingDing() {
        soundPool.play(soundID_dingding, 1, 1, 0, 0, 1);
    }

    // Plays 1 of the 5 random cannon fire sounds
    private void playFire() {
        switch (RandomVals.randomInt(1, 5)) {
            case 1:
                soundPool.play(soundID_fire1, 1, 1, 0, 0, 1);
                break;
            case 2:
                soundPool.play(soundID_fire2, 1, 1, 0, 0, 1);
                break;
            case 3:
                soundPool.play(soundID_fire3, 1, 1, 0, 0, 1);
                break;
            case 4:
                soundPool.play(soundID_fire4, 1, 1, 0, 0, 1);
                break;
            case 5:
                soundPool.play(soundID_fire5, 1, 1, 0, 0, 1);
                break;
        }
    }

    // Plays 1 of the random 3 ship impact smash sounds
    private void playSmash() {
        switch (RandomVals.randomInt(1, 3)) {
            case 1:
                soundPool.play(soundID_smash1, 1, 1, 0, 0, 1);
                break;
            case 2:
                soundPool.play(soundID_smash2, 1, 1, 0, 0, 1);
                break;
            case 3:
                soundPool.play(soundID_smash3, 1, 1, 0, 0, 1);
                break;
        }
    }

    // When user touches screen
    @Override
    public void mousePressed() {

        // If the player is no longer in control, disable their usage of mouse by returning immediately
        if(!control)    return;

        // If they are not currently reloading and aren't currently charging their cannon, start charging
        if(!chargeCannon && !reloading) {
            chargeCannon = true;
        }
    }

    // When user releases touch
    @Override
    public void mouseReleased() {

        // If the player is no longer in control, disable their usage of mouse by returning immediately
        if(!control)    return;

        // If they were charging their cannon and they release, player shoots a cannonball--
        // Essentially, you hold to charge and release to fire
        if(chargeCannon) {

            // Add a new cannonball based on the player's position
            cannonballs.add(new Cannonball(player.getBattlePos().x + 50, player.getBattlePos().y + 40, sensorXY, power, false));
            playFire();

            // Reset power value to 10, no longer charging cannon, and set reloading to true and set reload frames to start counting down reload
            power = 10;
            chargeCannon = false;
            reloading = true;
            playerReloadFrames = player.getReloadFrames();
        }
    }

    // If the player tries to press the back button during back, they do leave the sketch, but they
    // exit the battle under the "FLEE" battle result
    @Override
    public void onBackPressed() {
        finishBattle("FLEE");
        super.onBackPressed();
    }

    // Method that gets called once the battle finishes-- plays the knockout sound effect, disables control,
    // clears the list of cannonballs, sets the power to the minimum value, and sets the ending result
    private void preFinishBattle(String result) {

        // This if-statement makes it so that the sound only gets played once
        if(control) playDingDing();

        // Disable control
        control = false;
        cannonballs.clear();
        power = 10;
        endingResult = result;
    }

    // When the battle is finished
    private void finishBattle(String result) {

        // Check so we can typecast
        if(context instanceof Activity) {

            // New intent to pass the data back to WorldMapSketch
            Intent i = new Intent();

            // Put the appropriate battle result into the intent
            if(result.equals("VICTORY"))    i.putExtra("battleResult", "VICTORY");
            if(result.equals("DEFEAT"))    i.putExtra("battleResult", "DEFEAT");
            if(result.equals("FLEE"))    i.putExtra("battleResult", "FLEE");

            // Put other values we want into the values we'll return back to WorldMapSketch
            i.putExtra("playerLevel", player.getLevel());
            i.putExtra("enemyLevel", enemy.getLevel());
            i.putExtra("playerHealth", player.getHealth());

            // Finish this activity and pass back the intent with all the data we put in it
            ((Activity) context).setResult(Activity.RESULT_OK, i);
            ((Activity) context).finish();

            // Exits the Processing sketch
            exit();
        }
    }

    // Method that BattleActivity calls to update the sensor
    public void updateSensor(float[] vals) {

        // Don't try to access vals[1] if the length is less than 2
        if(vals.length < 2) return;

        // calculates the normalized X and Y axes of the sensor-- essentially a value of tilt
        sensorXY = (float)(Math.sqrt(vals[0]*vals[0] + vals[1]*vals[1]));
    }
}
