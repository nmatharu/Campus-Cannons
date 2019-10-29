package iat359.nmatharu.campuscannons.gameplay.hubPackage;

import android.content.Context;
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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import iat359.nmatharu.campuscannons.R;
import iat359.nmatharu.campuscannons.utilities.Constants;
import iat359.nmatharu.campuscannons.utilities.GameCalcs;

public class HubActivity extends AppCompatActivity {

    // Activity that displays all the items available for purchase at a hub

    // The list of items and recyclerAdapter for the recyclerView for the list of items
    private ArrayList<String> hubItemIDS = new ArrayList<>();
    private HubRecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forcing reverse landscape orientation across entire application -- see SplashActivity for explanation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        setContentView(R.layout.activity_hub);

        // Gets the hubID given the info put in the extras of the intent-- the hub that the player clicked on from WorldMapSketch
        String hubID = getIntent().getExtras().getString("hubID");

        // Title of the layout is set to the title given the hubID and the list of items is initialized given the ID as well
        TextView titleView = findViewById(R.id.hubTitle);
        titleView.setText(GameCalcs.getHubName(hubID));
        hubItemIDS = GameCalcs.getHubItems(hubID);

        // Gets the ID of the players currently equipped cannon from SharedPreferences to display it on the right side
        SharedPreferences preferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String currCannonID = preferences.getString("cannonID", Constants.DEFAULT_CANNON);

        // Sets the image on the right side of the layout to the player's current cannon
        ImageView iv = findViewById(R.id.hubCurrCannon);
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
        TextView itemText = findViewById(R.id.hubCurrStats);
        itemText.setText(itemString);

        // Get the players current number of coins and set it to the coins textView value
        int playerCoins = preferences.getInt("coins", Constants.DEFAULT_VAL);
        TextView playerCoinsText = findViewById(R.id.hubCurrCoins);
        String playerCoinsString = playerCoins + " COINS";
        playerCoinsText.setText(playerCoinsString);

        // Typical back button behaviour, same as other activities
        Button backButton = findViewById(R.id.hubBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Gets the players current list of items they own so that any item they own will be removed from the list of
        // items available for purchase in the shop-- players don't need to see the option to buy items they already own
        String[] playerItems = preferences.getString("cannonsList", Constants.DEFAULT_CANNON_LIST).split(",");
        removeOwnedItems(hubItemIDS, playerItems);

        // Initializes recyclerView and its adapter, sets the LinearLayoutManager and links the adapter to the recyclerView
        RecyclerView recyclerView = findViewById(R.id.hubRecyclerView);
        recyclerAdapter = new HubRecyclerAdapter(hubItemIDS, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
    }

    // From Fattie and binnyb on StackOverflow, just a method used to get a drawable ID given the string name
    public static int getImageID(Context context, String imageName) {
        return context.getResources().
                getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    // Given the list of items the hub has and the list of items the player has, .removeAll will remove any String
    // from playerItemsList from hubItemIDS so that the shop won't display any items the player already owns
    private void removeOwnedItems(ArrayList<String> hubItemIDS, String[] playerItems) {
        ArrayList<String> playerItemsList = new ArrayList<>(Arrays.asList(playerItems));
        hubItemIDS.removeAll(playerItemsList);
    }

    // Method for refreshing the recyclerView once a purchase has been made, mostly to update the buttons
    public void refreshRecycler() {
        recyclerAdapter.notifyDataSetChanged();
    }
}
