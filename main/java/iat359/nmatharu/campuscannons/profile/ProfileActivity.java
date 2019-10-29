package iat359.nmatharu.campuscannons.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import iat359.nmatharu.campuscannons.R;
import iat359.nmatharu.campuscannons.battleIndex.MyDatabase;
import iat359.nmatharu.campuscannons.menu.MenuActivity;
import iat359.nmatharu.campuscannons.utilities.Constants;
import iat359.nmatharu.campuscannons.utilities.GameCalcs;

public class ProfileActivity extends AppCompatActivity {

    // View objects

    private Button profileBackButton;
    private Button profileUpdateName;
    private Button profileResetWeapons;

    private TextView profileNameText;
    private TextView profileNameReqs;

    private EditText profileEditName;

    private TextView profileLevelText;
    private TextView profileXPText;
    private TextView profileCoinsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forcing reverse landscape orientation across entire application -- see SplashActivity for explanation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        setContentView(R.layout.activity_profile);

        // Get SharedPreferences for accessing saved user information
        SharedPreferences prefs = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        // Initialize all the view objects from ID

        profileBackButton = findViewById(R.id.profileBackButton);
        profileUpdateName = findViewById(R.id.profileUpdateName);

        profileNameText = findViewById(R.id.profileNameText);

        // Set the profile name text to the "name" saved in SharedPreferences
        profileNameText.setText(prefs.getString("name", Constants.DEFAULT_NAME));

        profileNameReqs = findViewById(R.id.profileNameReqs);
        profileEditName = findViewById(R.id.profileEditName);

        profileLevelText = findViewById(R.id.profileLevelText);

        // Set the level text to the level value calculated from XP from SharedPreferences
        // (Level is not actually something that is stored, it is calculated based off XP which is stored)
        profileLevelText.setText(String.valueOf(GameCalcs.calcLevel(prefs.getInt("XP", Constants.DEFAULT_VAL))));

        profileXPText = findViewById(R.id.profileXPText);

        // Set the XP text to the "XP" saved in SharedPreferences
        profileXPText.setText(String.valueOf(prefs.getInt("XP", Constants.DEFAULT_VAL)));

        profileCoinsText = findViewById(R.id.profileCoinsText);

        // Set the coins text to the "coins" saved in SharedPreferences
        profileCoinsText.setText(String.valueOf(prefs.getInt("coins", Constants.DEFAULT_VAL)));

        // When "back" is pressed (not the Android back button, the one in the Activity UI), finish the activity and go back to menu
        profileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // When update name button is pressed...
        profileUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If the name the user tried to change to doesn't meet the requirements (1 <= characters <= 12)
                // Reset the edit text box and fill a text view with a text that prompts the user of the requirements
                if (profileEditName.getText().toString().length() <= 0 || profileEditName.getText().toString().length() > 12) {
                    profileNameReqs.setText(R.string.prof_name_req);
                    profileEditName.setText("");

                } else {

                    // Else, get the profile name from the edit text, set the name text to it, empty the fields
                    // and then save the new name in SharedPreferences with the editor and commit the changes

                    String profName = profileEditName.getText().toString();

                    // Secret condition I can input for a profile name to set my coins and XP for testing purposes and demo
                    // JUST FOR TESTING
                    if(profName.length() >= 6 && profName.contains("_")) {
                        if(profName.substring(0, 3).equals("HX_")) {
                            String[] vals = profName.split("_");
                            if(vals.length == 3) {
                                int hackedXP = Integer.valueOf(vals[1]);
                                int hackedCoins = Integer.valueOf(vals[2]);

                                SharedPreferences hackedPrefs = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                                SharedPreferences.Editor hackedEditor = hackedPrefs.edit();
                                hackedEditor.putInt("XP", hackedXP);
                                hackedEditor.putInt("coins", hackedCoins);
                                Toast.makeText(ProfileActivity.this, "XP and Coin values changed!", Toast.LENGTH_SHORT).show();
                                hackedEditor.commit();
                            }
                        }
                    }

                    // Toggle use-touch-to-move vs. GPS, again, just for testing purposes
                    if(profName.equals("HX_TOGGLEGPS")) {
                        MenuActivity.useGPSToMove = !MenuActivity.useGPSToMove;
                        if(MenuActivity.useGPSToMove) {
                            Toast.makeText(ProfileActivity.this, "GPS Movement enabled", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "GPS Movement disabled", Toast.LENGTH_SHORT).show();
                        }
                    }

                    profileNameText.setText(profName);
                    profileEditName.setText("");
                    profileNameReqs.setText("");

                    SharedPreferences sp = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("name", profName);
                    editor.commit();
                }
            }
        });

        profileResetWeapons = findViewById(R.id.profileResetItems);
        profileResetWeapons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an AlertDialog.Builder to create a dialogue box that pops up to confirm with the user
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

                // User can press back to cancel the dialogue box
                builder.setCancelable(true);

                // Title of box is "confirm reset?"
                builder.setTitle("Confirm reset?");

                builder.setMessage("Are you sure you want to reset your weapons? This is really only for testing purposes.");

                // DialogInterface.OnClickListener has an onClick method--
                // .setPositiveButton means this is called when the player clicks "confirm"
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // If the player confirms the reset, get the weapons list for SharedPreferences and replace it
                                // with the default weapon only string "def"
                                SharedPreferences preferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("cannonsList", "def");
                                editor.putString("cannonID", "def");
                                editor.commit();
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
