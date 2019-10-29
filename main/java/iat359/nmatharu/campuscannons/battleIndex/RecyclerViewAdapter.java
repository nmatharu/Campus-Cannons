package iat359.nmatharu.campuscannons.battleIndex;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import iat359.nmatharu.campuscannons.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    // Declares and constructor initializes the list of strings that we will use to populate the recycler view
    public ArrayList<String> list;
    Context context;

    public RecyclerViewAdapter(ArrayList<String> list) {
        this.list = list;
    }

    // Inflates each of the RecyclerView item views with the R.layout.battleindex_row layout file we've made
    // which specifies the layout of the 5 elements: playerLevel, enemyLevel, battleResult, datetime, and trashcan ImageView
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.battleindex_row, parent, false);
        return new ViewHolder(view);
    }

    // Defines the specific elements of each item in the RecyclerView after being passed a ViewHolder object
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get array of Strings by splitting the specific row we were given from the ArrayList by & which we defined
        // earlier so we could get each of the values: playerLevel, enemyLevel, battleResult, and dateTime
        String[] strings = (list.get(position)).split("&");

        // Initializes and sets each TextView from the layout to the string value
        // (The ImageView doesn't need to be initialized here because it doesn't change depending on the list, it's just an image)
        TextView playerLevel = holder.layout.findViewById(R.id.birPlayerLevel);
        TextView enemyLevel = holder.layout.findViewById(R.id.birEnemyLevel);
        TextView battleResult = holder.layout.findViewById(R.id.birBattleResult);
        TextView dateTime = holder.layout.findViewById(R.id.birDateTime);

        playerLevel.setText(strings[0]);
        enemyLevel.setText(strings[1]);
        battleResult.setText(strings[2]);
        dateTime.setText(strings[3]);
    }

    // Count of items in the RecyclerView is equal to the size of ArrayList of strings
    @Override
    public int getItemCount() {
        return list.size();
    }

    // ViewHolder object that gets recycled in the RecyclerView for each sensor in our list
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Declare the LinearLayout because we want to use it to initialize the other Views
        LinearLayout layout;
        Context context;

        // Declare ImageView because we want to add an onClickListener to the trashcans
        ImageView deleteIcon;

        // Declares TextViews for battleResult and dateTime because we want their values
        // for the dialogue box that pops up when a user tries to delete an item
        TextView battleResult;
        TextView dateTime;

        // ViewHolder constructor, the itemView is the LinearLayout which contains our Views for each row
        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize all other fields
            layout = (LinearLayout) itemView;
            context = itemView.getContext();
            deleteIcon = layout.findViewById(R.id.birDeleteIcon);
            battleResult = layout.findViewById(R.id.birBattleResult);
            dateTime = layout.findViewById(R.id.birDateTime);

            // When a player clicks on a trashcan icon to delete a row...
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Create an AlertDialog.Builder to create a dialogue box that pops up to confirm with the user
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    // User can press back to cancel the dialogue box
                    builder.setCancelable(true);

                    // Title of box is "confirm delete?"
                    builder.setTitle("Confirm delete?");

                    // Asks user if they're sure they want to delete their battle that resulted in a VICTORY/DEFEAT/FLEE on whatever date
                    builder.setMessage("Are you sure you want to delete your " + battleResult.getText().toString() +
                                             " on " + dateTime.getText().toString() + "?");

                    // DialogInterface.OnClickListener has an onClick method--
                    // .setPositiveButton means this is called when the player clicks "confirm"
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    // If the player confirms the delete, initialize an object for getting the database
                                    // Call the database deleteItem method with the dateTime of the row in question
                                    // And make a Toast to the user that prompts them to hit one of the buttons again
                                    // in the Battle Index activity to refresh the recycler view list

                                    MyDatabase db = new MyDatabase(context);
                                    db.deleteItem(dateTime.getText().toString());
                                    Toast.makeText(context, "Refresh a list to view the updated database.", Toast.LENGTH_LONG).show();
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
    }

}
