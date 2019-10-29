package iat359.nmatharu.campuscannons.battleIndex;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import iat359.nmatharu.campuscannons.R;

public class BattleIndexActivity extends AppCompatActivity {

    // Database object
    private MyDatabase db;

    // RecyclerView and its adapter
    private RecyclerView myRecyclerView;
    private RecyclerViewAdapter myAdapter;

    private Context context;

    // EditTexts for the query, as well as a String that will tell us which query the user last inputted in
    private EditText playerLevelEdit;
    private EditText enemyLevelEdit;
    private EditText battleResultEdit;
    private String whichQuery = "";

    // Buttons for viewing database and going back
    private Button viewAllButton;
    private Button viewQueryButton;

    private Button battleIndexBackButton;

    // ArrayList that holds the strings of each row in the database as a string for the recycler view
    private ArrayList<String> rows = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forcing reverse landscape orientation across entire application -- see SplashActivity for explanation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        // Needed this just so I could refactor to the fillRecyclerAllHistory method below
        context = getApplicationContext();

        // Stops the activity from auto-opening the software keyboard when there are EditTexts in the activity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_battle_index);

        // Initializing recycler view and database
        myRecyclerView = findViewById(R.id.battleIndexRecyclerView);
        db = new MyDatabase(this);

        // Makes the recyclerViewAdapter given the rows (currently empty cause not displaying anything quite yet)
        // sets its layout manager to a new LinearLayoutManager, and sets its adapter to the new RecyclerViewAdapter object
        myAdapter = new RecyclerViewAdapter(rows);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(myAdapter);

        // For each EditText (Repeated twice below), add a text changed listener that allows us to clear the other two
        // EditTexts after one has been typed in, so that the user can only query by one of the columns. After the text
        // is changed, we also set our whichQuery variable to which one was last typed in so that we know what to query by
        playerLevelEdit = findViewById(R.id.battleIndexPlayerLevelEdit);
        playerLevelEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {

                // This condition needs to be checked else creates an infinite loop when the others have their text set to ""
                // and then they call their afterTextChanged, etc.
                if(!editable.toString().equals("")) {
                    enemyLevelEdit.setText("");
                    battleResultEdit.setText("");
                    whichQuery = "PLAYER_LEVEL";
                }
            }
        });

        // etc.
        enemyLevelEdit = findViewById(R.id.battleIndexEnemyLevelEdit);
        enemyLevelEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals("")) {
                    playerLevelEdit.setText("");
                    battleResultEdit.setText("");
                    whichQuery = "ENEMY_LEVEL";
                }
            }
        });

        // etc.
        battleResultEdit = findViewById(R.id.battleIndexResultEdit);
        battleResultEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals("")) {
                    playerLevelEdit.setText("");
                    enemyLevelEdit.setText("");
                    whichQuery = "BATTLE_RESULT";
                }
            }
        });

        // When the view all button is clicked, fill the recycler view with the entire database (method below)
        viewAllButton = findViewById(R.id.indexViewAll);
        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillRecyclerAllHistory();
            }
        });

        // When the view by query button is pressed...
        viewQueryButton = findViewById(R.id.indexViewQuery);
        viewQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If all the EditTexts are empty, prompt the user to fill in one of them and return
                if(playerLevelEdit.getText().toString().isEmpty() &&
                        enemyLevelEdit.getText().toString().isEmpty() &&
                        battleResultEdit.getText().toString().isEmpty()) {
                    Toast.makeText(BattleIndexActivity.this, "Please fill in a query.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If not, switch case by the type of query (last EditText filled) and call the appropriate method
                // Default case in-case somehow the whichQuery is not one of the 3 listed
                switch (whichQuery) {
                    case "PLAYER_LEVEL":
                        fillRecyclerPlayerLvlQuery(playerLevelEdit.getText().toString());
                        break;
                    case "ENEMY_LEVEL":
                        fillRecyclerEnemyLvlQuery(enemyLevelEdit.getText().toString());
                        break;
                    case "BATTLE_RESULT":
                        fillRecyclerResultQuery(battleResultEdit.getText().toString());
                        break;
                    default:
                        Toast.makeText(BattleIndexActivity.this, "Please fill in a query.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        // When the layout back button is pressed, finish activity
        battleIndexBackButton = findViewById(R.id.battleIndexBackButton);
        battleIndexBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void fillRecyclerAllHistory() {

        // Get database object and initialize cursor that points to a row
        db = new MyDatabase(context);
        Cursor cursor = db.getData();

        // Make sure the ArrayList of strings is empty before we start this
        rows.clear();

        // Indexes for all the columns we're interested in
        int index1 = cursor.getColumnIndex(DatabaseConstants.PLAYER_LEVEL);
        int index2 = cursor.getColumnIndex(DatabaseConstants.ENEMY_LEVEL);
        int index3 = cursor.getColumnIndex(DatabaseConstants.BATTLE_RESULT);
        int index4 = cursor.getColumnIndex(DatabaseConstants.DATE_TIME);

        // Move cursor to first row, and continue to loop while moving cursor to next until it hits the end
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {

            // Get the cells from the database
            int playerLevel = cursor.getInt(index1);
            int enemyLevel = cursor.getInt(index2);
            String battleResult = cursor.getString(index3);
            String dateTime = cursor.getString(index4);

            // Add the String to the rows ArrayList for the recyclerViewAdapter to split by "&" into its components
            rows.add(playerLevel + "&" + enemyLevel + "&" + battleResult + "&" + dateTime);

            cursor.moveToNext();
        }

        // remakes the Adapter with the new rows ArrayList and sets the adapter to the recycler view
        myAdapter = new RecyclerViewAdapter(rows);
        myRecyclerView.setAdapter(myAdapter);
    }

    public void fillRecyclerResultQuery(String battleResult) {

        // Making sure that even if the user didn't type in all-caps, we get the right names of the battle result
        if(battleResult.equalsIgnoreCase("victory"))    battleResult = "VICTORY";
        if(battleResult.equalsIgnoreCase("defeat"))     battleResult = "DEFEAT";
        if(battleResult.equalsIgnoreCase("flee"))       battleResult = "FLEE";

        // Does the same thing as fillRecyclerAllHistory() but instead gets the rows from a method in the
        // MyDatabase object which I'll talk about in there
        db = new MyDatabase(context);
        myAdapter = new RecyclerViewAdapter(db.getSelectedBattleResult(battleResult));
        myRecyclerView.setAdapter(myAdapter);
    }

    public void fillRecyclerPlayerLvlQuery(String playerLevel) {

        // Does the same thing as fillRecyclerAllHistory() but instead gets the rows from a method in the
        // MyDatabase object which I'll talk about in there
        db = new MyDatabase(context);
        myAdapter = new RecyclerViewAdapter(db.getSelectedPlayerLevel(playerLevel));
        myRecyclerView.setAdapter(myAdapter);
    }

    public void fillRecyclerEnemyLvlQuery(String enemyLevel) {

        // Does the same thing as fillRecyclerAllHistory() but instead gets the rows from a method in the
        // MyDatabase object which I'll talk about in there
        db = new MyDatabase(context);
        myAdapter = new RecyclerViewAdapter(db.getSelectedEnemyLevel(enemyLevel));
        myRecyclerView.setAdapter(myAdapter);
    }

}
