package ca.uqac.watchdog;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ServerListActivity extends ListActivity {

    public static final String PREFS_NAME = "SavedServers";

    ArrayList<Server> servers = new ArrayList<Server>();
    ArrayAdapter<Server> adapter;

    // DB stuff
    ServerEntryDBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverlist);

        // Context de l'activité
        final Context context = this;

        // Créer l'adapteur
        adapter = new ServerAdapter(this,
                android.R.layout.simple_list_item_1,
                servers);
        setListAdapter(adapter);

        // Créer les objets de DB
        dbHelper = new ServerEntryDBHelper(this);

        LoadServers();
    }

    @Override
    protected void onDestroy() {
        db.close();
        dbHelper.close();
        super.onDestroy();
    }

    public void OnAddServerClick(View v) {
        final Context context = this;

        LayoutInflater li = LayoutInflater.from(context);
        View promptView = li.inflate(R.layout.prompt_addserver, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final EditText editTextServerName = (EditText) promptView.findViewById(R.id.editTextServerName);
        final EditText editTextServerURL = (EditText) promptView.findViewById(R.id.editTextServerURL);
        
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddServer(
                                editTextServerName.getText().toString(),
                                editTextServerURL.getText().toString()
                        );
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel pressed
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void AddServer(String serverName, String serverURL) {
        Server newServer = new Server(serverName, serverURL);
        servers.add(newServer);
        dbHelper.addServer(db, newServer);
        adapter.notifyDataSetChanged();
    }

    public void LoadServers() {
        db = dbHelper.getReadableDatabase();
        // Add all, otherwise the adapter loses the reference to the server list
        servers.addAll(dbHelper.getServers(db));

        // We do not need to read into the db anymore after we've loaded the servers
        db.close();
        db = dbHelper.getWritableDatabase();

        adapter.notifyDataSetChanged();
    }
    
    public void DeleteServer(Server server) {
        dbHelper.deleteServer(db, server);
        Toast.makeText(this, "Server deleted!", Toast.LENGTH_SHORT).show();
        servers.remove(server);
        adapter.notifyDataSetChanged();
    }
}
