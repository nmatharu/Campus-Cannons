package iat359.nmatharu.campuscannons.battleIndex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MyDatabase {

    // declares our database object, the context, and a helper to create/upgrade tables in our database
    // Not sure why the DatabaseHelper should be final, tried to look up information about it and couldn't
    // find anything but didn't want to potentially mess up database so left it from Helmine's example
    private SQLiteDatabase db;
    private Context context;
    private final DatabaseHelper helper;

    // Constructor
    public MyDatabase(Context c) {
        context = c;
        helper = new DatabaseHelper(context);
    }

    // Method for inserting a row into the table
    public long insertData(int playerLevel, int enemyLevel, String battleResult, String dateTime) {

        // Get a database object we can write to
        db = helper.getWritableDatabase();

        // ContentValues object contains all the information about the row we want to add and
        // we put all of the values in
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.PLAYER_LEVEL, playerLevel);
        contentValues.put(DatabaseConstants.ENEMY_LEVEL, enemyLevel);
        contentValues.put(DatabaseConstants.BATTLE_RESULT, battleResult);
        contentValues.put(DatabaseConstants.DATE_TIME, dateTime);

        // Insert it into the table
        // Returns a long that will be -1 if the insertion failed
        return db.insert(DatabaseConstants.TABLE_NAME, null, contentValues);
    }

    // Returns cursor with all the data in the table (no selection specification in the .query method
    public Cursor getData() {

        SQLiteDatabase db = helper.getWritableDatabase();

        // Gets all columns
        String[] columns = {
                DatabaseConstants.UID,
                DatabaseConstants.PLAYER_LEVEL,
                DatabaseConstants.ENEMY_LEVEL,
                DatabaseConstants.BATTLE_RESULT,
                DatabaseConstants.DATE_TIME
        };

        return db.query(DatabaseConstants.TABLE_NAME, columns,
                null, null, null, null, null, null);
    }

    // Method which will return an ArrayList of strings for the RecyclerViewAdapter to use which
    // only contains the rows that have a certain battle result (query)
    public ArrayList<String> getSelectedBattleResult(String bResult) {

        SQLiteDatabase db = helper.getWritableDatabase();

        // Same as getting all data, but...
        String[] columns = {
                DatabaseConstants.UID,
                DatabaseConstants.PLAYER_LEVEL,
                DatabaseConstants.ENEMY_LEVEL,
                DatabaseConstants.BATTLE_RESULT,
                DatabaseConstants.DATE_TIME
        };

        // We only select those that have a column of battle result that is equal to the passed in string
        String selection = DatabaseConstants.BATTLE_RESULT + "='" + bResult + "'";
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection,
                null, null, null, null);

        ArrayList<String> rows = new ArrayList<>();

        // Go through all the rows in the table, adding strings to the arraylist with &s so that the
        // RecyclerViewAdapter can break it up
        while(cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(DatabaseConstants.PLAYER_LEVEL);
            int index2 = cursor.getColumnIndex(DatabaseConstants.ENEMY_LEVEL);
            int index3 = cursor.getColumnIndex(DatabaseConstants.BATTLE_RESULT);
            int index4 = cursor.getColumnIndex(DatabaseConstants.DATE_TIME);

            int playerLevel = cursor.getInt(index1);
            int enemyLevel = cursor.getInt(index2);
            String battleResult = cursor.getString(index3);
            String dateTime = cursor.getString(index4);

            rows.add(playerLevel + "&" + enemyLevel + "&" + battleResult + "&" + dateTime);
        }

        return rows;
    }

    // Exact same as above, just querying the player level instead of battle result
    public ArrayList<String> getSelectedPlayerLevel(String pLevel) {

        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {
                DatabaseConstants.UID,
                DatabaseConstants.PLAYER_LEVEL,
                DatabaseConstants.ENEMY_LEVEL,
                DatabaseConstants.BATTLE_RESULT,
                DatabaseConstants.DATE_TIME
        };

        String selection = DatabaseConstants.PLAYER_LEVEL + "='" + pLevel + "'";
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection,
                null, null, null, null);

        ArrayList<String> rows = new ArrayList<>();

        while(cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(DatabaseConstants.PLAYER_LEVEL);
            int index2 = cursor.getColumnIndex(DatabaseConstants.ENEMY_LEVEL);
            int index3 = cursor.getColumnIndex(DatabaseConstants.BATTLE_RESULT);
            int index4 = cursor.getColumnIndex(DatabaseConstants.DATE_TIME);

            int playerLevel = cursor.getInt(index1);
            int enemyLevel = cursor.getInt(index2);
            String battleResult = cursor.getString(index3);
            String dateTime = cursor.getString(index4);

            rows.add(playerLevel + "&" + enemyLevel + "&" + battleResult + "&" + dateTime);
        }

        return rows;
    }

    // Exact same as above, just querying the enemy level instead of player level
    public ArrayList<String> getSelectedEnemyLevel(String eLevel) {

        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {
                DatabaseConstants.UID,
                DatabaseConstants.PLAYER_LEVEL,
                DatabaseConstants.ENEMY_LEVEL,
                DatabaseConstants.BATTLE_RESULT,
                DatabaseConstants.DATE_TIME
        };

        String selection = DatabaseConstants.ENEMY_LEVEL + "='" + eLevel + "'";
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection,
                null, null, null, null);

        ArrayList<String> rows = new ArrayList<>();

        while(cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(DatabaseConstants.PLAYER_LEVEL);
            int index2 = cursor.getColumnIndex(DatabaseConstants.ENEMY_LEVEL);
            int index3 = cursor.getColumnIndex(DatabaseConstants.BATTLE_RESULT);
            int index4 = cursor.getColumnIndex(DatabaseConstants.DATE_TIME);

            int playerLevel = cursor.getInt(index1);
            int enemyLevel = cursor.getInt(index2);
            String battleResult = cursor.getString(index3);
            String dateTime = cursor.getString(index4);

            rows.add(playerLevel + "&" + enemyLevel + "&" + battleResult + "&" + dateTime);
        }

        return rows;
    }

    // Method that deletes an item from the SQL database that has a specific date and time
    public void deleteItem(String dateTime) {

        SQLiteDatabase db = helper.getWritableDatabase();

        // Says that rows we want to delete have dateTimes that are in this ArrayList, only one in this case
        String[] whereArgs = { dateTime };

        // Delete all rows that have the same dateTime as the one passed in
        db.delete(DatabaseConstants.TABLE_NAME, DatabaseConstants.DATE_TIME + "=?", whereArgs);
    }

}
