package iat359.nmatharu.campuscannons.leaderboard;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import iat359.nmatharu.campuscannons.utilities.Constants;
import iat359.nmatharu.campuscannons.utilities.GameCalcs;

public class Stats implements Comparable<Stats> {

    // Object that holds the relevant info of a player for the leaderboards
    // Their name, their unique ID, their level, and their XP

    private String name;
    private int ID;
    private int level;
    private int XP;

    // Constructors given a string passed it to split to the 4 values OR by SharedPreferences

    public Stats(String line) {
        String[] pieces = line.split(",");
        name = pieces[0];
        ID = Integer.parseInt(pieces[1]);
        level = Integer.parseInt(pieces[2]);
        XP = Integer.parseInt(pieces[3]);
    }

    public Stats(SharedPreferences prefs) {
        name = prefs.getString("name", Constants.DEFAULT_NAME);
        ID = prefs.getInt("leaderboardID", Constants.DEFAULT_LBD_ID);
        XP = prefs.getInt("XP", Constants.DEFAULT_VAL);
        level = GameCalcs.calcLevel(XP);
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public int getLevel() {
        return level;
    }

    public int getXP() {
        return XP;
    }

    // Overrides compareTo so we can order the players on the leaderboard by their XP
    @Override
    public int compareTo(@NonNull Stats stats) {
        return Integer.compare(this.getXP(), stats.getXP());
    }
}
