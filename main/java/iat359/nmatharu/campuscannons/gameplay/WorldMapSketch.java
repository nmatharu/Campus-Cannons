package iat359.nmatharu.campuscannons.gameplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import iat359.nmatharu.campuscannons.R;
import iat359.nmatharu.campuscannons.battleIndex.MyDatabase;
import iat359.nmatharu.campuscannons.gameplay.gameObjects.Enemy;
import iat359.nmatharu.campuscannons.gameplay.gameObjects.Hub;
import iat359.nmatharu.campuscannons.gameplay.gameObjects.Player;
import iat359.nmatharu.campuscannons.gameplay.hubPackage.HubActivity;
import iat359.nmatharu.campuscannons.gameplay.inventoryPackage.InventoryActivity;
import iat359.nmatharu.campuscannons.menu.MenuActivity;
import iat359.nmatharu.campuscannons.utilities.Constants;
import iat359.nmatharu.campuscannons.utilities.GameCalcs;
import iat359.nmatharu.campuscannons.utilities.RandomVals;
import iat359.nmatharu.campuscannons.utilities.TouchGraphic;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

import static android.content.Context.MODE_PRIVATE;

public class WorldMapSketch extends PApplet {

    /*  Processing sketch that handles interactions on the gameplay world map screen
        Decent amount of code here, will try to be as concise with comments as possible
        Again, leaving out most graphics handling stuff cause it isn't specific to what we're
        learning in Android studio and most people are familiar with it after working with Processing
     */

    private static final int GAME_WIDTH_PIXELS = 1920;
    private static final int GAME_HEIGHT_PIXELS = 1080;
    private static final int GAME_FRAME_RATE = 60;

    private static final int REQUEST_BATTLE = 1;

    // Constants for the mez coordinates-- the starting position
    private static final double MEZ_LATITUDE = 49.187478;
    private static final double MEZ_LONGITUDE = -122.849386;

    private Context context;

    // app res is the target resolution of the sketch (1920 x 1080)
    // device res is the resolution of the player's device
    // we use these two PVectors to scale the sketch up/down to fit the device
    private PVector appRes;
    private PVector deviceRes;

    // Font
    private PFont dimbo;

    private PImage bg;
    private PImage playerIcon;
    private PImage enemylv1Icon;
    private PImage enemylv2Icon;
    private PImage enemylv3Icon;
    private PImage enemylv4Icon;
    private PImage enemylv5Icon;

    private PImage MEZHubIcon;
    private PImage THRHubIcon;
    private PImage LECHubIcon;
    private PImage LIBHubIcon;
    private PImage LABHubIcon;
    private PImage VRAHubIcon;
    private PImage IATHubIcon;

    private PImage uiSkeleton;

    // Player object
    private Player player;

    // Doubles that hold the current player's latitude and longitude
    private double playerLatitude;
    private double playerLongitude;

    // ArrayList of enemy, index variable to remove enemies
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private int enemyToRemove;

    // ArrayList of hubs, only 1 at the moment but there will be more
    private ArrayList<Hub> hubs = new ArrayList<>();

    // ArrayList of the touchGraphics that are described in the TouchGraphic class, index to remove it as well
    private ArrayList<TouchGraphic> tGraphics = new ArrayList<>();
    private int tgToRemove;

    // Values for the scale to zoom the map in and out, smoothing constant to make this movement smooth
    private float currScale = 1f;
    private float targetScale = 1f;
    private static final int SMOOTHING = 25;

    public WorldMapSketch(Context c) {
        super();
        context = c;

        appRes = new PVector(GAME_WIDTH_PIXELS, GAME_HEIGHT_PIXELS);

        // Get device resolution
        deviceRes = new PVector(
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels);

        player = new Player(c.getSharedPreferences("USER_INFO", MODE_PRIVATE));

        // Add all 7 hubs to the list
        hubs.add(new Hub("MEZ", 2000, 1800));
        hubs.add(new Hub("THR", 4956, 1496));
        hubs.add(new Hub("LEC", -3270, 2580));
        hubs.add(new Hub("LIB", 5208, -1204));
        hubs.add(new Hub("LAB", -726, 2876));
        hubs.add(new Hub("VRA", 3930, -1752));
        hubs.add(new Hub("IAT", 4296, -744));
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

        bg = loadImage("world_map/world_bg.png");
        playerIcon = loadImage("world_map/player_icon.png");

        enemylv1Icon = loadImage("world_map/enemy_icon_lv1.png");
        enemylv2Icon = loadImage("world_map/enemy_icon_lv2.png");
        enemylv3Icon = loadImage("world_map/enemy_icon_lv3.png");
        enemylv4Icon = loadImage("world_map/enemy_icon_lv4.png");
        enemylv5Icon = loadImage("world_map/enemy_icon_lv5.png");

        MEZHubIcon = loadImage("world_map/hubs/MEZ.png");
        THRHubIcon = loadImage("world_map/hubs/THR.png");
        LECHubIcon = loadImage("world_map/hubs/LEC.png");
        LIBHubIcon = loadImage("world_map/hubs/LIB.png");
        LABHubIcon = loadImage("world_map/hubs/LAB.png");
        VRAHubIcon = loadImage("world_map/hubs/VRA.png");
        IATHubIcon = loadImage("world_map/hubs/IAT.png");

        uiSkeleton = loadImage("world_map/ui.png");

        textFont(dimbo);

        noStroke();
        imageMode(CENTER);
    }

    @Override
    public void draw() {

        // Scale to match resolution
        pushMatrix();
        scale(width/appRes.x, height/appRes.y);

        // Scaling in and out for zooming
        pushMatrix();
        translate(appRes.x/2, appRes.y/2);
        scale(currScale);
        translate(-appRes.x/2, -appRes.y/2);

        // Smoothing to the target scale value
        currScale = (currScale*SMOOTHING + targetScale)/(SMOOTHING + 1);

        // Scale to center camera on player
        pushMatrix();
        translate(appRes.x/2 - player.getWorldMapPos().x, appRes.y/2 - player.getWorldMapPos().y);

        background(18, 171, 178);

        image(bg, appRes.x/2, appRes.y/2, bg.width*5, bg.height*5);

        // method that both spawns enemies near the player and removes ones outside their view
        handleEnemies();

        // Calls method that handles all the TouchGraphic objects
        updateTouchGraphics();

        // For all enemies, update their position and call the method to draw them
        for(Enemy e : enemies) {
            e.updatePos();
            drawEnemy(e);
        }

        // Update a player's position based off baseline (MEZ) and current lat and long
        // As mentioned in ProfileActivity, this useGPSToMove boolean is only ever false for testing purposes by
        // entering a specific code into the name field
        if(MenuActivity.useGPSToMove)   player.updatePos(MEZ_LATITUDE, MEZ_LONGITUDE, playerLatitude, playerLongitude);
        drawPlayer();

        // Draw all hubs
        for(Hub h : hubs) {
            drawHub(h);
        }

        popMatrix();
        popMatrix();
        drawUI();
        popMatrix();
    }

    private void handleEnemies() {

        // If there aren't very many enemies, then...
        if(enemies.size() <= 6) {

            // We find the hub that is closest to the player
            String closestHubID = "MEZ";
            float closestDist = 1000000f;
            for (Hub h : hubs) {
                if(PVector.dist(player.getWorldMapPos(), h.getWorldMapPos()) < closestDist) {
                    closestDist = PVector.dist(player.getWorldMapPos(), h.getWorldMapPos());
                    closestHubID = h.getHubID();
                }
            }

            // Calculate the min and max level of the enemies we should spawn based off of the closest hub
            // (some hubs spawn easier/harder enemies)
            int minLevel = GameCalcs.getEnemiesMinLevelByNearestHub(closestHubID);
            int maxLevel = GameCalcs.getEnemiesMaxLevelByNearestHub(closestHubID);

            // Get random coordinates to spawn at near the player
            float xCoord = player.getWorldMapPos().x + RandomVals.randomFloat(-appRes.x, appRes.x);
            float yCoord = player.getWorldMapPos().y + RandomVals.randomFloat(-appRes.y, appRes.y);

            // If these coordinates are on the map, add a new enemy at that position and random level
            if(onMap(new PVector(xCoord, yCoord))) {
                enemies.add(new Enemy(RandomVals.randomInt(minLevel, maxLevel), xCoord, yCoord));
            }
        }

        // Same code as before to remove
        // If the enemies is very far away from the player, de-spawn it
        int enemyToRemove = -1;
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (PVector.dist(e.getWorldMapPos(), player.getWorldMapPos()) > appRes.x) {
                enemyToRemove = i;
            }
        }
        if(enemyToRemove != -1) enemies.remove(enemyToRemove);
    }

    // Implementation I use for removing an element in a loop without doing it in the loop (cause that causes an Exception without
    // use of an Iterator)-- set index to remove to -1, for all Graphics, draw them, and if they should be removed, set the index
    // of it to the remove index, if the index is no longer -1 at the end, remove the one in the variables
    // Obviously this can only remove one object per frame, but this is plenty fast enough to remove as many objects as the player
    // would like to generate and keeps the memory well within check
    private void updateTouchGraphics() {
        tgToRemove = -1;
        for(int i = 0; i < tGraphics.size(); i++) {
            TouchGraphic tg = tGraphics.get(i);
            tg.drawMe();
            if(tg.shouldRemove())   tgToRemove = i;
        }
        if(tgToRemove != -1)    tGraphics.remove(tgToRemove);
    }

    // Method for drawing all the UI elements
    private void drawUI() {

        // draws UI skeleton to screen (fairly temporary, I would like to make this more graphically pleasing and add colour)
        image(uiSkeleton, appRes.x/2, appRes.y/2);

        pushMatrix();
        translate(0, 57);

        // Draw HUD text, user's name, level, coins, with different fonts, sizes, etc. etc.

        fill(255);
        textAlign(LEFT);
        textSize(80f);
        text(player.getName(), 30, 40);

        text(String.valueOf(player.getLevel()), 975, 40);

        textAlign(RIGHT);
        text(String.valueOf(player.getCoins()), 1725, 40);

        textAlign(LEFT);
        textSize(80f);
        text(String.valueOf(player.getXP()), 155, 987);

        textAlign(RIGHT);
        text((int)player.getWorldMapPos().x + ", " + (int)player.getWorldMapPos().y, 1725, 987);

        popMatrix();
    }

    // When user touches screen (on touch DOWN, not full click or release, on immediate touch down)
    @Override
    public void mousePressed() {

        // If the mouseClick is within a certain range of pixels, it means the user clicked on the backpack on the bottom of the HUD
        // The backpack launches the explicit intent of the Inventory Activity
        // *(appRes.x/width) is used to scale the mouseX and mouseY values to 1920x1080 from the original device resolution
        // OR the user clicked on the magnifying glass which means we should toggle the scale value
        if(PVector.dist(new PVector(877, 1011), new PVector(mouseX*(appRes.x/width), mouseY*(appRes.y/height))) < 60) {
            context.startActivity(new Intent(context, InventoryActivity.class));
            return;
        } else if(PVector.dist(new PVector(1069, 1011), new PVector(mouseX*(appRes.x/width), mouseY*(appRes.y/height))) < 60) {
            if(targetScale == 1f) {
                targetScale = 0.5f;
            } else {
                targetScale = 1f;
            }
            return;
        }

        // scaled mouseX and mouseY values are those that correspond to the actual positions in the game world,
        // we need to do this because the camera follows the player but the mouseX and mouseY values do not account for that
        float scaledMouseX = mouseX*(appRes.x/width) - (appRes.x/2 - player.getWorldMapPos().x);
        float scaledMouseY = mouseY*(appRes.y/height) - (appRes.y/2 - player.getWorldMapPos().y);

        // Modifies this value based on the current scale (normally this will do nothing, but when the map is zoomed out
        // this adjusts the mouse values to the scaled positions
        scaledMouseX += -player.getWorldMapPos().x;
        scaledMouseY += -player.getWorldMapPos().y;
        scaledMouseX *= 1f/currScale;
        scaledMouseY *= 1f/currScale;
        scaledMouseX += player.getWorldMapPos().x;
        scaledMouseY += player.getWorldMapPos().y;

        // If player touches one of the hubs...
        for(Hub h : hubs) {
            if(h.touchCollides(scaledMouseX, scaledMouseY)) {

                // Start Hub activity
                Intent intent = new Intent(context, HubActivity.class);
                intent.putExtra("hubID", h.getHubID());
                context.startActivity(intent);
                return;
            }
        }

        // Same idea as the TouchGraphics, index set to -1, if it gets changed, remove that enemy
        enemyToRemove = -1;
        for(int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);

            // For all enemies, if an enemy is touched

            if(e.touchCollides(scaledMouseX, scaledMouseY)) {

                // Required so that Android Studio doesn't complain about context not necessarily being an Activity
                if(context instanceof Activity) {

                    // Launch the battle activity from explicit intent (battle with the other ship)
                    // and put all the necessary information into the intent so that the battle activity can retrieve it
                    Intent intent = new Intent(context, BattleActivity.class);
                    intent.putExtra("enemyLevel", e.getLevel());
                    intent.putExtra("playerName", player.getName());
                    intent.putExtra("playerMaxHealth", player.getMaxHealth());
                    intent.putExtra("playerLevel", player.getLevel());

                    // Remove an enemy from the world map that has been engaged with in combat
                    enemyToRemove = i;

                    // Start activity for RESULT cause we want to result information from the battle
                    ((Activity) context).startActivityForResult(intent, REQUEST_BATTLE);

                    // Break out so only one enemy can be engaged in combat at a time
                    break;

                } else {

                    // Not sure why this context would ever not be an instance of Activity, but in case it isn't, Toast the error
                    Toast.makeText(context, "Can't start Battle Activity for some reason", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(enemyToRemove != -1) {
            enemies.remove(enemyToRemove);
            return;
        }

        // If a player taps on their own boat, launch the Inventory Activity as well
        if(player.touchCollides(scaledMouseX, scaledMouseY)) {
            context.startActivity(new Intent(context, InventoryActivity.class));
            return;
        }

        // If a player didn't collide with anything on their tap, add a TouchGraphic to the place they pressed
        tGraphics.add(new TouchGraphic(scaledMouseX, scaledMouseY,this));

        // Temp movement method you can use if you are not on campus by entering code in ProfileActivity-- see class for details
        if(!MenuActivity.useGPSToMove)   player.setWorldMapPos(scaledMouseX, scaledMouseY);
    }

    // Drawing the player, mostly graphical stuff...
    private void drawPlayer() {
        pushMatrix();

        // Translate to their position so we can draw things at 0, 0
        translate(player.getWorldMapPos().x, player.getWorldMapPos().y);

        pushMatrix();

        // Rotate the graphic by the direction the player is heading in
        rotate(player.getHeading());

        image(playerIcon, 0, 0);
        popMatrix();

        // Low opacity black box underneath player with their name in it
        fill(0, 30);
        rectMode(CENTER);
        textAlign(CENTER);
        textSize(48f);

        rect(0, 160, 50 + textWidth(player.getName()), 100);
        fill(255, 200);
        text(player.getName(), 0, 177);

        popMatrix();
    }

    // Drawing the enemy, mostly graphical stuff...
    private void drawEnemy(Enemy e) {
        pushMatrix();

        // Translate to their position so we can draw things at 0, 0
        translate(e.getWorldMapPos().x, e.getWorldMapPos().y);
        pushMatrix();

        // Rotate graphic to the direction they're moving in
        rotate(e.getRotation() + PI/2);

        // Switch case to draw the appropriate enemy given their level
        switch (e.getLevel()) {
            case 1:
                image(enemylv1Icon, 0, 0);
                break;
            case 2:
                image(enemylv2Icon, 0, 0);
                break;
            case 3:
                image(enemylv3Icon, 0, 0);
                break;
            case 4:
                image(enemylv4Icon, 0, 0);
                break;
            case 5:
                image(enemylv5Icon, 0, 0);
                break;
            default:
                break;
        }

        popMatrix();

        // Low opacity black box underneath enemy with text that shows their level
        fill(0, 30);
        rectMode(CENTER);
        textAlign(CENTER);
        textSize(64f);

        rect(0, 155, 50 + textWidth("Lv. " + e.getLevel()), 100);
        fill(255, 200);
        text("LV. " + e.getLevel(), 0, 178);

        popMatrix();
    }

    // Drawing the hub icon
    private void drawHub(Hub h) {
        pushMatrix();
        translate(h.getWorldMapPos().x, h.getWorldMapPos().y);
        switch (h.getHubID()) {
            case "MEZ":
                image(MEZHubIcon, 0, 0);
                break;
            case "THR":
                image(THRHubIcon, 0, 0);
                break;
            case "LAB":
                image(LABHubIcon, 0, 0);
                break;
            case "LIB":
                image(LIBHubIcon, 0, 0);
                break;
            case "LEC":
                image(LECHubIcon, 0, 0);
                break;
            case "VRA":
                image(VRAHubIcon, 0, 0);
                break;
            case "IAT":
                image(IATHubIcon, 0, 0);
                break;
        }
        popMatrix();
    }

    // FusedLocationProviderClient calls this every time it gets a location request (specified by our interval)
    public void updateLocation(Location location) {

        // Update the player latitude and longitude variables
        playerLatitude = location.getLatitude();
        playerLongitude = location.getLongitude();
    }

    // On the result of an activity we've launched from here (namely the Battle Activity)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If it was from the battle (it's the only activity for result we've launched anyways...)
        if(requestCode == REQUEST_BATTLE && resultCode == Activity.RESULT_OK) {

            // If it has all the data we want
            if(data.hasExtra("playerLevel") && data.hasExtra("enemyLevel")
                    && data.hasExtra("battleResult") && data.hasExtra("playerHealth")) {

                // Use simple data format to set the format to, example: Mon., Mar. 11, 2019, 23:27:06 PDT
                // Make a new Date object that gets the current date
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy, HH:mm:ss z", Locale.CANADA);
                Date date = new Date();

                // Initializes variables and gets all the passed values from the ActivityResult from .getExtras()
                int playerLevel = data.getExtras().getInt("playerLevel");
                int enemyLevel = data.getExtras().getInt("enemyLevel");
                String battleResult = data.getExtras().getString("battleResult");

                // Get the database and insert a new row for the level the player was during the battle, the level the enemy was,
                // the result of the battle, and the date (dateFormat.format(date) gets the string representation of the current date)
                MyDatabase db = new MyDatabase(context);
                long id = db.insertData(playerLevel, enemyLevel, battleResult, dateFormat.format(date));

                // If the database passes us back -1 for insertData, it means we couldn't add the result to the database, and Toast
                // to the other to let them know
                if(id == -1)    Toast.makeText(context, "Failed to add battle result to index.", Toast.LENGTH_LONG).show();

                // Get the shared preferences cause we need to update and get values in it
                SharedPreferences sp = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                // Call the GameCalcs methods to get the new number of coins and XP based on the player's battle
                int newCoins = GameCalcs.updateCoins(sp.getInt("coins", Constants.DEFAULT_VAL), enemyLevel, battleResult);
                int newXP = GameCalcs.updateXP(sp.getInt("XP", Constants.DEFAULT_VAL), enemyLevel, battleResult);

                // Start building a dialogue that will inform the user of the battle conclusion
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Inflate the dialogue with our own custom battle_result_dialog layout file
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View v = inflater.inflate(R.layout.battle_result_dialog, null);
                builder.setView(v);

                // Can press back to cancel dialogue
                builder.setCancelable(true);

                // Sets the title TextView in our custom layout to the battle result
                TextView title = v.findViewById(R.id.battleResultDialogTitle);
                title.setText(battleResult);

                // Gets the body text TextView in our custom layout and builds a string for it to be set to
                TextView text = v.findViewById(R.id.battleResultDialogText);
                String dialogText = "";

                // BASICALLY,
                // Victory will say something like "# coins gained, # XP gained"
                // Defeat will say something like "# XP gained"
                // Flee will say "You fled from battle."
                // and we use GameCalcs to get some of those vales-- see those methods for more
                switch (battleResult) {
                    case "VICTORY":
                        dialogText = (newCoins - sp.getInt("coins", Constants.DEFAULT_VAL)) + " coins gained. \n" +
                                ((newXP) - sp.getInt("XP", Constants.DEFAULT_VAL)) + " XP gained.";
                        break;
                    case "DEFEAT":
                        dialogText = (newXP) - sp.getInt("XP", Constants.DEFAULT_VAL) + " XP gained.";
                        break;
                    case "FLEE":
                        dialogText = "You fled from battle.";
                        break;
                }

                // set the body text to the string we made above
                text.setText(dialogText);

                // This dialogue only needs a confirmation button cause it's just notifying the user, and the onClick
                // method doesn't do anything for the same reason
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) { }
                });

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                // Puts the new number of coins, XP into SharedPreferences
                editor.putInt("coins", newCoins);
                editor.putInt("XP", newXP);

                // Sets the player's fields in this sketch to the new values
                player.setCoins(newCoins);
                player.setXP(newXP);
                player.updateLevel();

                // Commit shared preferences changes
                editor.commit();
            }

        }
    }

    // Returns whether or not the player is more-or-less on the map
    private boolean onMap(PVector pos) {
        return  (pos.x >= -5000) &&
                (pos.x <= 7000) &&
                (pos.y >= -3000) &&
                (pos.y <= 4500);
    }

    // Overrides onResume to make sure that the player coin count is updated properly-- if the player
    // comes from an activity like HubActivity which can change their coin count, we need to update it here as well
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        player.setCoins(preferences.getInt("coins", Constants.DEFAULT_VAL));
    }
}
