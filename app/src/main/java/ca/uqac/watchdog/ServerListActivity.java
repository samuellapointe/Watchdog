package ca.uqac.watchdog;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

        LoadServers();
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
        AddServerToSavedServers(newServer);
        adapter.notifyDataSetChanged();
    }

    public void AddLoadedServer(Server server) {
        servers.add(server);
        adapter.notifyDataSetChanged();
    }

    public void LoadServers() {
        SharedPreferences savedServers = getSharedPreferences(PREFS_NAME, 0);
        String savedServersString = savedServers.getString("servers", "");

        String serverStrings[] = savedServersString.split("#");
        for (int i = 0; i < serverStrings.length; i++) {
            if (serverStrings[i].length() > 1) {
                Server newServer = new Server(serverStrings[i]);
                AddLoadedServer(newServer);
            }
        }
    }

    public void AddServerToSavedServers(Server server) {
        // Save servers as strings
        String serverString = server.toSaveString();

        // Get saved servers
        SharedPreferences savedServers = getSharedPreferences(PREFS_NAME, 0);
        String savedServersString = savedServers.getString("servers", "");

        // Add server separator if needed
        if (savedServersString.length() != 0) {
            savedServersString += "#";
        }

        // Save changes
        savedServersString += serverString;
        SharedPreferences.Editor editor = savedServers.edit();
        editor.putString("servers", savedServersString);
        editor.commit();
    }
}
