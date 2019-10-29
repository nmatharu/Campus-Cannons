package iat359.nmatharu.campuscannons.battleIndex;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    // SQL code for creating table in database with the columns id, player level, enemy level, battle result, and date/time
    private static final String CREATE_TABLE = "CREATE TABLE " +
            DatabaseConstants.TABLE_NAME + " (" +
            DatabaseConstants.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DatabaseConstants.PLAYER_LEVEL + " INTEGER, " +
            DatabaseConstants.ENEMY_LEVEL + " INTEGER, " +
            DatabaseConstants.BATTLE_RESULT + " TEXT, " +
            DatabaseConstants.DATE_TIME + " TEXT);";

    // SQL code for dropping table from Database
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_NAME;

    // Helper class which will create our database and replace it when the version number is changed
    // The context is grabbed here just so we can send Toast messages in case something fails
    public DatabaseHelper(Context c) {
        super(c, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
        context = c;
    }

    // Creates battle index table on creation of our database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
            Toast.makeText(context, "Failed to create the Battle Index SQL Database.", Toast.LENGTH_LONG).show();
        }
    }

    // Drops battle index table on upgrade of database (such as change in version number)
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL(DROP_TABLE);
        } catch (SQLException e) {
            Toast.makeText(context, "Failed to upgrade Battle Index SQL Database.", Toast.LENGTH_LONG).show();
        }
    }
}
