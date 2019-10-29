package iat359.nmatharu.campuscannons.leaderboard;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import iat359.nmatharu.campuscannons.R;
import iat359.nmatharu.campuscannons.utilities.Constants;

public class LeaderboardActivity extends AppCompatActivity {

    /*  This is the bonus functionality of my app! Essentially, a global leaderboard that is stored on a text-file so that
        players can compare their progress to each other. Now, this approach is more of a proof of concept than something I
        would actually release. This was accomplished by using the Dropbox API to access the folder of a burner Dropbox account
        that I made, which reads the most recent leaderboard text file, and uploads an updated version

        This is unsafe and certainly something no one should ever use in real life as it involves distributing an access token
        to the Dropbox which means anyone with knowledge of the API could alter the Dropbox as much as they want. Again, this is
        more of a proof of concept on how I would implement things like the unique IDs and it works great for personal use to
        distribute with friends.
     */

    private Button backButton;
    private Button updateLeaderboard;

    // RecyclerView and Adapter for our leaderboard entries
    private RecyclerView recyclerView;
    private LeaderboardRecyclerAdapter recyclerAdapter;

    // Access token and Dropbox objects
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN_HERE";
    private DbxRequestConfig config = null;
    DbxClientV2 clientV2 = null;
    FullAccount account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forcing reverse landscape orientation across entire application -- see SplashActivity for explanation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        setContentView(R.layout.activity_leaderboard);

        // Typical back button behvaiour-- exits Activity
        backButton = findViewById(R.id.leaderboardBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Update leaderboard buttons executes the ASyncTask that will deal with the Dropbox data--
        // as you can see below, this is also automatically executed on Activity start up
        updateLeaderboard = findViewById(R.id.leaderboardUpdate);
        updateLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeUpdateTask();
            }
        });

        // Initializes recyclerView and sets its layout manager
        recyclerView = findViewById(R.id.leaderboardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        executeUpdateTask();
    }

    // Launches ASyncTask class
    private void executeUpdateTask() {
        new DropboxLeaderboardTask().execute();
        Toast.makeText(getApplicationContext(), "Updating leaderboard...", Toast.LENGTH_SHORT).show();
    }

    // Task which will read the most recent file from the Dropbox, modify it, upload a new version, and send
    // the information (ArrayList<Stats>) to the RecyclerAdapter

    // A lot of the following code was followed along from the Dropbox V2 API documentation
    private class DropboxLeaderboardTask extends AsyncTask<Void, Void, ArrayList<Stats>> {

        @Override
        protected ArrayList<Stats> doInBackground(Void... voids) {

            // opens Dropbox client given my access token
            config = new DbxRequestConfig("NavsAirControl");
            clientV2 = new DbxClientV2(config, ACCESS_TOKEN);

            // ArrayList of strings that holds the names of the leaderboard files in the Dropbox
            ArrayList<String> lbdFiles = new ArrayList<>();

            // ArrayList of stats that will hold all the player's data
            ArrayList<Stats> playersStats = new ArrayList<>();

            // Get files and folder metadata from Dropbox root directory
            ListFolderResult result;
            try {
                result = clientV2.files().listFolder("");

                // From Dropbox documentation: gets all file and folder names from root directory
                // and if it contains the "leaderboard_" (the prefix of my leaderboards), add it to the list
                while (true) {
                    for (Metadata metadata : result.getEntries()) {
                        if(metadata.getPathLower().contains("leaderboard_")) {
                            lbdFiles.add(metadata.getPathLower());
                        }
                    }
                    if (!result.getHasMore()) {
                        break;
                    }
                    result = clientV2.files().listFolderContinue(result.getCursor());
                }
            } catch (DbxException e) {
                e.printStackTrace();
            }

            // Make a new file on the device to hold the data we're about the read in
            File file = new File(getApplicationContext().getFilesDir(), "temp_lbd.txt");

            // Sort the names of all the leaderboard files so we can get the most recent one (last position)
            Collections.sort(lbdFiles);
            String fileName = lbdFiles.get(lbdFiles.size() - 1);

            // Output file for download --> storage location on local system to download file
            try (FileOutputStream downloadFile = new FileOutputStream(file.getAbsolutePath())) {
                FileMetadata metadata = clientV2.files().downloadBuilder(fileName).download(downloadFile);
            } catch (IOException | DbxException e) {
                e.printStackTrace();
            }

            // Gets the players leaderboardID (if they have one) from SharedPreferences
            SharedPreferences prefs = getSharedPreferences("USER_INFO", MODE_PRIVATE);
            int playerID = prefs.getInt("leaderboardID", Constants.DEFAULT_LBD_ID);

            // Reads lines from the file-- each line corresponds to a players stats and we
            // initialize Stats objects from each line and add them to the list of stats if
            // it is not the ID of the player and if its not empty
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                while ((st = br.readLine()) != null) {
                    Stats player = new Stats(st);
                    if(player.getID() != playerID && !st.isEmpty())  playersStats.add(new Stats(st));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // If the player didn't have an ID (-1 is default value for LBD_ID in sharedPreferences), then..
            if(playerID == -1) {
                SharedPreferences.Editor editor = prefs.edit();

                // We generate numbers from 0-999 until we get an ID that is unique and save it to SharedPreferences

                int newID = (int)(Math.random()*1000);
                while(!IDIsUnique(playersStats, newID)) {
                    newID = (int)(Math.random()*1000);
                }

                editor.putInt("leaderboardID", newID);
                editor.commit();
            }

            // Adds the final Stats object to the list: the player's stats
            prefs = getSharedPreferences("USER_INFO", MODE_PRIVATE);
            playersStats.add(new Stats(prefs));

            // Sort the list by the XP of the players in reverse order (first in list is highest XP)
            Collections.sort(playersStats, Collections.<Stats>reverseOrder());

            // Iterate through all the Stats and write them to the file
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                for(Stats s : playersStats) {
                    out.write(s.getName() + "," + s.getID() + "," + s.getLevel() + "," + s.getXP());
                    out.newLine();
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Upload the file back to the dropbox, with the suffix as the current date so that we can easily
            // get the most recent file (they will be ordered)
            Date date = new Date();
            String newFileName = "/leaderboard_" + date.getTime() + ".txt";
            try {
                InputStream in = new FileInputStream(file);
                FileMetadata metadata = clientV2.files().uploadBuilder(newFileName).uploadAndFinish(in);
            } catch (IOException | DbxException e) {
                e.printStackTrace();
            }

            // Delete the temporary file we made on the user's system
            file.delete();

            // Return the list of Stats so we can make the RecyclerView with them
            return playersStats;
        }

        // Initializes Adapter given the returned list and sets the recyclerView to the adapter
        @Override
        protected void onPostExecute(ArrayList<Stats> playersStats) {
            recyclerAdapter = new LeaderboardRecyclerAdapter(playersStats, getApplicationContext());
            recyclerView.setAdapter(recyclerAdapter);
        }
    }

    // Checks whether or not an argument ID is in the list of playerStats IDs-- in other words, unique
    private boolean IDIsUnique(ArrayList<Stats> playerStats, int newID) {
        for(Stats s : playerStats) {
            if(s.getID() == newID)  return false;
        }
        return true;
    }
}
