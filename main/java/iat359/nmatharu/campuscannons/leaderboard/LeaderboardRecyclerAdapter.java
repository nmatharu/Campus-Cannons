package iat359.nmatharu.campuscannons.leaderboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import iat359.nmatharu.campuscannons.R;

public class LeaderboardRecyclerAdapter extends RecyclerView.Adapter<LeaderboardRecyclerAdapter.ViewHolder> {

    // Recycler will be populated with the lists of stats for each player on the leaderboard
    public ArrayList<Stats> playersStats;
    public Context context;

    public LeaderboardRecyclerAdapter(ArrayList<Stats> playersStats, Context context) {
        this.playersStats = playersStats;
        this.context = context;
    }

    // Inflates each of the RecyclerView item views with the R.layout.leaderboard_layout layout file we've made
    // which specifies the layout of 4 elements: the player ID, their name, their level, and their XP
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_row, parent, false);
        return new ViewHolder(view);
    }

    // Defines the specific elements of each item in the RecyclerView after being passed a ViewHolder object
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get the Stats object given the current position on the list
        Stats s = playersStats.get(position);

        // Gets each of the TextViews in the LinearLayout
        TextView lbdName = holder.layout.findViewById(R.id.lbrPlayerName);
        TextView lbdID = holder.layout.findViewById(R.id.lbrPlayerID);
        TextView lbdLevel = holder.layout.findViewById(R.id.lbrPlayerLevel);
        TextView lbdXP = holder.layout.findViewById(R.id.lbrPlayerXP);

        // Fills the text in the views with the Stats data
        lbdName.setText(s.getName());
        lbdID.setText(String.valueOf(s.getID()));
        lbdLevel.setText(String.valueOf(s.getLevel()));
        lbdXP.setText(String.valueOf(s.getXP()));
    }

    // Count is equal to the size of the Stats list
    @Override
    public int getItemCount() {
        return playersStats.size();
    }

    // Count of items in the RecyclerView is the size of the list of item IDs
    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView;
        }
    }
}
