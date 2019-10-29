package iat359.nmatharu.campuscannons.gameplay.inventoryPackage;

import android.content.Context;
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

import static iat359.nmatharu.campuscannons.gameplay.hubPackage.HubActivity.getImageID;

public class InventoryRecyclerAdapter extends RecyclerView.Adapter<InventoryRecyclerAdapter.ViewHolder> {

    // Declares and constructor initializes the list of Strings that has the IDs of the items that this hub holds
    private ArrayList<String> itemIDS;

    // Passing in a reference to the hubActivity so that we can call .refreshRecycler()
    private Context context;
    private InventoryActivity inventoryActivity;

    public InventoryRecyclerAdapter(ArrayList<String> itemIDS, Context context, InventoryActivity inventoryActivity) {
        this.itemIDS = itemIDS;
        this.context = context;
        this.inventoryActivity = inventoryActivity;
    }

    // Inflates each of the RecyclerView item views with the R.layout.inventory_row layout file we've made
    // which specifies the layout of 6 elements: the item image, its name, the min dmg range, the max dmg range,
    // the reload speed, and a button to equip
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_row, parent, false);
        return new ViewHolder(view);
    }

    // Defines the specific elements of each item in the RecyclerView after being passed a ViewHolder object
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get the item values of the current item given the position in the recyclerView
        final String itemID = itemIDS.get(position);
        String[] itemStats = GameCalcs.getCannonVals(itemID).split(",");

        // Get all the View objects in the LinearLayout for this row
        ImageView img = holder.layout.findViewById(R.id.iir_Image);
        TextView name = holder.layout.findViewById(R.id.iir_ItemName);
        TextView minDmg = holder.layout.findViewById(R.id.iir_ItemMinDmg);
        TextView maxDmg = holder.layout.findViewById(R.id.iir_ItemMaxDmg);
        TextView reload = holder.layout.findViewById(R.id.iir_ItemReload);
        final Button equip = holder.layout.findViewById(R.id.iir_EquipButton);

        // Similar to InventoryActivity-- get the values of the item
        int minRangeMinDmg = Integer.valueOf(itemStats[0]);
        int minRangeMaxDmg = Integer.valueOf(itemStats[1]);
        int maxRangeMinDmg = 2*minRangeMinDmg;
        int maxRangeMaxDmg = 2*minRangeMaxDmg;
        int reloadVal = Integer.valueOf(itemStats[2]);

        // More stuff the same as InventoryActivity-- building the strings for our Views
        String minDmgString = minRangeMinDmg + "-" + minRangeMaxDmg;
        String maxDmgString = maxRangeMinDmg + "-" + maxRangeMaxDmg;
        String reloadString = String.format(Locale.CANADA, "%.1f", reloadVal/60f) + "s";

        // Set the strings and the imageView given the item ID
        img.setImageResource(getImageID(context, "c_" + itemID));
        name.setText(GameCalcs.getCannonName(itemID));
        minDmg.setText(minDmgString);
        maxDmg.setText(maxDmgString);
        reload.setText(reloadString);

        // Gets the ID of the user's current cannon, if its the same as the one here, that means
        // this is the weapon that the user has equipped and we disable the equip button and change to text
        // to "EQUIPPED"-- else, the button can be pressed to equip the new weapon
        SharedPreferences prefs = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        String equippedItemID = prefs.getString("cannonID", Constants.DEFAULT_CANNON);
        if(itemID.equals(equippedItemID)) {
            equip.setEnabled(false);
            equip.setText(R.string.inventory_button_equipped);
        } else {
            equip.setEnabled(true);
            equip.setText(R.string.inventory_button_equip);
        }

        // If the button is pressed, disable the button and set it to "EQUIPPED"
        equip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                equip.setEnabled(false);
                equip.setText(R.string.inventory_button_equipped);

                // Edit the player's current weapon in SharedPreferences
                SharedPreferences preferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("cannonID", itemID);

                editor.commit();

                // Calls inventory Activity's updateRecycler to update the buttons
                inventoryActivity.updateRecycler();
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
