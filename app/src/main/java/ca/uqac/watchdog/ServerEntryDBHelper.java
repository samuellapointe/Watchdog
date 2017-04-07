package ca.uqac.watchdog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ca.uqac.watchdog.ServerContract.ServerEntry;

/**
 * Created by Sam on 2017-04-07.
 * As seen on https://developer.android.com/training/basics/data-storage/databases.html
 */

public class ServerEntryDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3; // Changer si on change les infos de la table
    public static final String DATABASE_NAME = "Servers.db";

    // Some commands
    private static final String SQL_CREATE_ENTRIES = "" +
            "CREATE TABLE " + ServerEntry.TABLE_NAME + " (" +
            ServerEntry._ID + " INTEGER PRIMARY KEY," +
            ServerEntry.COLUMN_NAME_NAME + " TEXT," +
            ServerEntry.COLUMN_NAME_URL + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "" +
            "DROP TABLE IF EXISTS " + ServerEntry.TABLE_NAME;

    /************************
     * Functions start here *
     ***********************/
    public ServerEntryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // Quand on change de version, vider la DB et recommencer
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // Même chose pour le downgrade
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // Sam's helper functions!

    // Add server to db, return ID
    public long addServer(SQLiteDatabase db, Server server) {
        ContentValues values = new ContentValues();
        values.put(ServerEntry.COLUMN_NAME_NAME, server.getDisplayName());
        values.put(ServerEntry.COLUMN_NAME_URL, server.getURL());
        long newRowId = db.insert(ServerEntry.TABLE_NAME, null, values);
        return newRowId;
    }

    public ArrayList<Server> getServers(SQLiteDatabase db) {
        String[] projection = {
                ServerEntry._ID,
                ServerEntry.COLUMN_NAME_NAME,
                ServerEntry.COLUMN_NAME_URL
        };

        String sortOrder = ServerEntry.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = db.query(
                ServerEntry.TABLE_NAME,
                projection,
                null, // No selection
                null, // No selection args
                null, // No groups
                null, // No filters
                sortOrder
        );

        ArrayList<Server> servers = new ArrayList<Server>();
        while (cursor.moveToNext()) {
            Server server = new Server(
                    cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_URL))
            );
            servers.add(server);
        }

        return servers;
    }

    public void deleteServer(SQLiteDatabase db, Server server) {
        String selection = ServerEntry.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = { server.getDisplayName() };
        db.delete(ServerEntry.TABLE_NAME, selection, selectionArgs);
    }


}
