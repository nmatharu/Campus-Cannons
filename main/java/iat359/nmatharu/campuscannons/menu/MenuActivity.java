package iat359.nmatharu.campuscannons.menu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import iat359.nmatharu.campuscannons.R;
import iat359.nmatharu.campuscannons.battleIndex.BattleIndexActivity;
import iat359.nmatharu.campuscannons.gameplay.WorldMapActivity;
import iat359.nmatharu.campuscannons.leaderboard.LeaderboardActivity;
import iat359.nmatharu.campuscannons.profile.ProfileActivity;

public class MenuActivity extends AppCompatActivity {

    // All UI buttons
    private Button playButton;
    private Button profileButton;
    private Button battleIndexButton;
    private Button leaderboardButton;
    private Button websiteButton;

    // Global static variable for use in WorldMapSketch for testing purposes only
    // --can only be turned off with a code in the EditName field and is to test
    // the app when not on campus by moving with touches on the screen
    public static boolean useGPSToMove = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forcing reverse landscape orientation across entire application -- see SplashActivity for explanation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        setContentView(R.layout.activity_menu);

        // Get all buttons from view IDs
        playButton = findViewById(R.id.playButton);
        profileButton = findViewById(R.id.profileButton);
        battleIndexButton = findViewById(R.id.battleIndexButton);
        leaderboardButton = findViewById(R.id.leaderboardButton);
        websiteButton = findViewById(R.id.websiteButton);

        // When the play button is clicked, request fine location permission from the user if the
        // user hasn't already granted the permission-- see the result in onRequestPermissionResult
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MenuActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        });

        // When the profile button is clicked, use explicit intent to launch ProfileActivity
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, ProfileActivity.class));
            }
        });

        // When the battle index button is clicked, use explicit intent to launch BattleIndexActivity
        battleIndexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, BattleIndexActivity.class));
            }
        });

        // When the leaderboard button is clicked, don't do anything yet-- I would like to add a global
        // leaderboard activity for my bonus functionality
        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, LeaderboardActivity.class));
            }
        });

        // When the how to play button is clicked, use implicit intent to launch the how to play webpage
        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse("https://campuscannons.weebly.com/");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(webIntent);
            }
        });
    }

    // If the request code matches the one we gave above (1)
    // Then we check if the grantResults array has length more than 0 (if it's empty, then the user didn't grant any permissions)
    // And check to see if our permission was granted
    // If it is, we're good to go and start our main gameplay "WorldMapActivity"
    // If it's not, then we Toast to the user and explain that we need location permissions to play and keep them in the menu
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(MenuActivity.this, WorldMapActivity.class));
            } else {
                Toast.makeText(MenuActivity.this, "Location permissions are required for this game.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
