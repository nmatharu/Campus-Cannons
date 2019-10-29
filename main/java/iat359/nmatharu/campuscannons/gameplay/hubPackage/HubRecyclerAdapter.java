package iat359.nmatharu.campuscannons.gameplay.hubPackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import iat359.nmatharu.campuscannons.R;
import iat359.nmatharu.campuscannons.utilities.Constants;
import iat359.nmatharu.campuscannons.utilities.GameCalcs;

public class HubRecyclerAdapter extends RecyclerView.Adapter<HubRecyclerAdapter.ViewHolder> {

    // Declares and constructor initializes the list of Strings that has the IDs of the items that this hub holds
    private ArrayList<String> itemIDS;

    // Passing in a reference to the hubActivity so that we can call .refreshRecycler()
    private HubActivity hubActivity;
    private Context context;

    public HubRecyclerAdapter(ArrayList<String> itemIDS, HubActivity hubActivity, Context context) {
        this.itemIDS = itemIDS;
        this.hubActivity = hubActivity;
        this.context = context;
    }

    // Inflates each of the RecyclerView item views with the R.layout.hubitems_row layout file we've made
    // which specifies the layout of 6 elements: the item image, its name, the min dmg range, the max dmg range,
    // the reload speed, and a button to purchase
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hubitems_row, parent, false);
        return new ViewHolder(view);
    }

    // Defines the specific elements of each item in the RecyclerView after being passed a ViewHolder object
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the item values of the current item given the position in the recyclerView
        final String itemID = itemIDS.get(position);
        String[] itemStats = GameCalcs.getCannonVals(itemID).split(",");

        // Get all the View objects in the LinearLayout for this row
        ImageView img = holder.layout.findViewById(R.id.hir_Image);
        TextView name = holder.layout.findViewById(R.id.hir_ItemName);
        TextView minDmg = holder.layout.findViewById(R.id.hir_ItemMinDmg);
        TextView maxDmg = holder.layout.findViewById(R.id.hir_ItemMaxDmg);
        TextView reload = holder.layout.findViewById(R.id.hir_ItemReload);
        final Button purchase = holder.layout.findViewById(R.id.hir_BuyButton);

        // Similar to HubActivity-- get the values of the item
        int minRangeMinDmg = Integer.valueOf(itemStats[0]);
        int minRangeMaxDmg = Integer.valueOf(itemStats[1]);
        int maxRangeMinDmg = 2*minRangeMinDmg;
        int maxRangeMaxDmg = 2*minRangeMaxDmg;
        int reloadVal = Integer.valueOf(itemStats[2]);
        final int costCoins = Integer.valueOf(itemStats[3]);

        // Gets the user's current coin amount from SharedPreferences-- if their current number of
        // coins is less than the cost of the item, they cannot afford it and the purchase button is greyed out
        SharedPreferences prefs = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        int playerCoins = prefs.getInt("coins", Constants.DEFAULT_VAL);
        if(playerCoins < costCoins) purchase.setEnabled(false);

        // More stuff the same as HubActivity-- building the strings for our Views
        String minDmgString = minRangeMinDmg + "-" + minRangeMaxDmg;
        String maxDmgString = maxRangeMinDmg + "-" + maxRangeMaxDmg;
        String reloadString = String.format(Locale.CANADA, "%.1f", reloadVal/60f) + "s";
        String costString = costCoins + " COINS";

        // Set the strings and the imageView given the item ID
        img.setImageResource(HubActivity.getImageID(context, "c_" + itemID));
        name.setText(GameCalcs.getCannonName(itemID));
        minDmg.setText(minDmgString);
        maxDmg.setText(maxDmgString);
        reload.setText(reloadString);
        purchase.setText(costString);

        // Gets the players list of items from SharedPreferences and checks if any of them are the same as the current
        // item-- if so, set the purchase button to disabled and replace the text with "PURCHASED"
        // ..why? Because otherwise, players would purchase an item and there would be no feedback that they had, the item
        // would simply grey out as if they couldn't purchase it. Changing the text to "PURCHASED" shows this
        String[] playerItems = prefs.getString("cannonsList", Constants.DEFAULT_CANNON_LIST).split(",");
        for(String s : playerItems) {
            if(s.equals(itemID))   {
                purchase.setEnabled(false);
                purchase.setText(R.string.hubbutton_purchased);
            }
        }

        // When purchase button is clicked
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an AlertDialog.Builder to create a dialogue box that pops up to confirm with the user
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // User can press back to cancel the dialogue box
                builder.setCancelable(true);

                // Title of box is "confirm purchase?"
                builder.setTitle("CONFIRM PURCHASE");

                // Asks user if they're sure they want to purchase this item
                builder.setMessage("Are you sure you want to purchase " + GameCalcs.getCannonName(itemID) + " for " + costCoins + " coins?");

                // DialogInterface.OnClickListener has an onClick method--
                // .setPositiveButton means this is called when the player clicks "confirm"
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // If player confirms purchase of item, we open up an Editor to SharedPreferences cause we'll
                                // need to modify their coin count
                                SharedPreferences prefs = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();

                                // Get their current number of coins
                                int currPlayerCoins = prefs.getInt("coins", Constants.DEFAULT_VAL);

                                // Calculate the updated number and put it back into SharedPreferences
                                int updatedCoins = currPlayerCoins - costCoins;
                                editor.putInt("coins", updatedCoins);

                                // Add the ID of the item they just bought to their list of items string in SharedPreferences
                                String playersItems = prefs.getString("cannonsList", Constants.DEFAULT_CANNON_LIST);
                                playersItems += "," + itemID;
                                editor.putString("cannonsList", playersItems);

                                editor.commit();

                                if(context instanceof Activity) {
                                    // Updates the value of the player's coin count on the right side of the Activity
                                    TextView playerCoinsText = ((Activity) context).findViewById(R.id.hubCurrCoins);
                                    String playerCoinsString = updatedCoins + " COINS";
                                    playerCoinsText.setText(playerCoinsString);
                                }

                                // Calls hub Activity's method to refresh the recyclerView
                                hubActivity.refreshRecycler();
                            }
                        });

                // Nothing happens if the user clicks "cancel"
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        });

                // Create and show the dialogue box
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // Count of items in the RecyclerView is the size of the list of item IDs
    @Override
    public int getItemCount() {
        return itemIDS.size();
    }

    // ViewHolder object that gets recycled in the RecyclerView for each sensor in our list
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Declare the LinearLayout because we need it as the holder
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView;
        }
    }
}
