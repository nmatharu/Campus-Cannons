package iat359.nmatharu.campuscannons.gameplay.inventoryPackage;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import iat359.nmatharu.campuscannons.R;
import iat359.nmatharu.campuscannons.utilities.Constants;
import iat359.nmatharu.campuscannons.utilities.GameCalcs;

import static iat359.nmatharu.campuscannons.gameplay.hubPackage.HubActivity.getImageID;

public class InventoryActivity extends AppCompatActivity {

    // Activity that displays all the items a player owns

    // The list of items and recyclerView and Adapter for the recyclerView for the list of items
    private ArrayList<String> itemIDS = new ArrayList<>();
    private RecyclerView recyclerView;
    private InventoryRecyclerAdapter recyclerAdapter;

    // Views for the information about the player's current item on the right side of the layout
    private ImageView iv;
    private TextView itemText;
    private TextView playerCoinsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forcing reverse landscape orientation across entire application -- see SplashActivity for explanation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        setContentView(R.layout.activity_inventory);

        // Gets players current weaponID from SharedPreferences and sets the ImageView to its image given the ID
        SharedPreferences preferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String currCannonID = preferences.getString("cannonID", Constants.DEFAULT_CANNON);
        iv = findViewById(R.id.inventoryCurrCannon);
        iv.setImageResource(getImageID(this, "c_" + currCannonID));

        // Gets all the stats of the player's current cannon given the string from GameCalcs
        String[] currCannonStats = GameCalcs.getCannonVals(currCannonID).split(",");
        int minRangeMinDmg = Integer.valueOf(currCannonStats[0]);
        int minRangeMaxDmg = Integer.valueOf(currCannonStats[1]);
        int maxRangeMinDmg = 2*minRangeMinDmg;
        int maxRangeMaxDmg = 2*minRangeMaxDmg;
        int reloadVal = Integer.valueOf(currCannonStats[2]);

        // Builds the strings to put into the textView
        String minDmgString = minRangeMinDmg + "-" + minRangeMaxDmg + " MAX";
        String maxDmgString = maxRangeMinDmg + "-" + maxRangeMaxDmg + " MAX";
        String reloadString = String.format(Locale.CANADA, "%.1f", reloadVal/60f) + "s RELOAD";
        String itemString = minDmgString + "\n" + maxDmgString + "\n" + reloadString;

        // Gets the item TextView and sets its text to the concatenated string
        itemText = findViewById(R.id.inventoryCurrStats);
        itemText.setText(itemString);

        // Get the players current number of coins and set it to the coins textView value
        int playerCoins = preferences.getInt("coins", Constants.DEFAULT_VAL);
        playerCoinsText = findViewById(R.id.inventoryCurrCoins);
        String playerCoinsString = playerCoins + " COINS";
        playerCoinsText.setText(playerCoinsString);

        // Typical back button behaviour, same as other activities
        Button backButton = findViewById(R.id.inventoryBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Sets the item IDs for the recyclerView to be the player's list of items given the String in SharedPreferences
        String[] itemIDSArray = preferences.getString("cannonsList", Constants.DEFAULT_CANNON_LIST).split(",");
        itemIDS = new ArrayList<>(Arrays.asList(itemIDSArray));

        // Initializes recyclerView and its adapter, sets the LinearLayoutManager and links the adapter to the recyclerView
        recyclerView = findViewById(R.id.inventoryRecyclerView);
        recyclerAdapter = new InventoryRecyclerAdapter(itemIDS, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
    }

    // Method for refreshing the recyclerView once a new weapon has been equipped, mostly to update the buttons
    // Also refreshes the information about the item on the right side
    public void updateRecycler() {

        recyclerAdapter.notifyDataSetChanged();

        // Gets players current weaponID from SharedPreferences and sets the ImageView to its image given the ID
        SharedPreferences preferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String currCannonID = preferences.getString("cannonID", Constants.DEFAULT_CANNON);
        iv = findViewById(R.id.inventoryCurrCannon);
        iv.setImageResource(getImageID(this, "c_" + currCannonID));

        // Gets all the stats of the player's current cannon given the string from GameCalcs
        String[] currCannonStats = GameCalcs.getCannonVals(currCannonID).split(",");
        int minRangeMinDmg = Integer.valueOf(currCannonStats[0]);
        int minRangeMaxDmg = Integer.valueOf(currCannonStats[1]);
        int maxRangeMinDmg = 2*minRangeMinDmg;
        int maxRangeMaxDmg = 2*minRangeMaxDmg;
        int reloadVal = Integer.valueOf(currCannonStats[2]);

        // Builds the strings to put into the textView
        String minDmgString = minRangeMinDmg + "-" + minRangeMaxDmg + " MAX";
        String maxDmgString = maxRangeMinDmg + "-" + maxRangeMaxDmg + " MAX";
        String reloadString = String.format(Locale.CANADA, "%.1f", reloadVal/60f) + "s RELOAD";
        String itemString = minDmgString + "\n" + maxDmgString + "\n" + reloadString;

        // Gets the item TextView and sets its text to the concatenated string
        itemText = findViewById(R.id.inventoryCurrStats);
        itemText.setText(itemString);

        // Get the players current number of coins and set it to the coins textView value
        int playerCoins = preferences.getInt("coins", Constants.DEFAULT_VAL);
        playerCoinsText = findViewById(R.id.inventoryCurrCoins);
        String playerCoinsString = playerCoins + " COINS";
        playerCoinsText.setText(playerCoinsString);
    }
}
