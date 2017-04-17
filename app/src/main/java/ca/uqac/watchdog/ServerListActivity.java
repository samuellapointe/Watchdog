package ca.uqac.watchdog;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ServerListActivity extends ListActivity {

    ArrayList<Server> servers = new ArrayList<Server>();
    ArrayAdapter<Server> adapter;

    // DB stuff
    ServerEntryDBHelper dbHelper;
    SQLiteDatabase db;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverlist);

        // Pour les threads
        handler = new Handler();

        // Context de l'activité
        final Context context = this;

        // Créer l'adapteur
        adapter = new ServerAdapter(this,
                android.R.layout.simple_list_item_1,
                servers);
        setListAdapter(adapter);

        registerForContextMenu(this.getListView());

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
        final EditText editTextSshURL = (EditText) promptView.findViewById(R.id.editTextSSHURL);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddServer(
                                editTextServerName.getText().toString(),
                                editTextServerURL.getText().toString(),
                                editTextSshURL.getText().toString()
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

    public void OnEditServerClick(final Server server) {
        final Context context = this;

        LayoutInflater li = LayoutInflater.from(context);
        View promptView = li.inflate(R.layout.prompt_addserver, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final EditText editTextServerName = (EditText) promptView.findViewById(R.id.editTextServerName);
        final EditText editTextServerURL = (EditText) promptView.findViewById(R.id.editTextServerURL);
        final EditText editTextSshURL = (EditText) promptView.findViewById(R.id.editTextSSHURL);

        editTextServerName.setText(server.getDisplayName());
        editTextServerURL.setText(server.getURL());
        editTextSshURL.setText(server.getSshAddress());

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditServer(
                                server,
                                editTextServerName.getText().toString(),
                                editTextServerURL.getText().toString(),
                                editTextSshURL.getText().toString()
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu_serverlist, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e("ERROR", "bad menuInfo", e);
            return false;
        }

        Server server = servers.get(info.position);
        switch(item.getItemId()) {
            case R.id.edit:
                OnEditServerClick(server);
                return true;
            case R.id.delete:
                DeleteServer(server);
                return true;
        }

        return false;
    }

    public void AddServer(String serverName, String serverURL, String sshURL) {
        final Server newServer = new Server(serverName, serverURL, sshURL);

        new Thread(new Runnable() {
            public void run() {
                dbHelper.addServer(db, newServer);
                servers.add(newServer);
                handler.post(RefreshServers);
            }
        }).start();
    }

    public void LoadServers() {
        new Thread(new Runnable() {
            public void run() {
                db = dbHelper.getReadableDatabase();
                // Add all, otherwise the adapter loses the reference to the server list
                servers.addAll(dbHelper.getServers(db));

                // We do not need to read into the db anymore after we've loaded the servers
                db.close();
                db = dbHelper.getWritableDatabase();
                handler.post(RefreshServers);
            }
        }).start();
    }

    public void EditServer(final Server server, String serverName, String serverURL, String sshURL) {
        // Save old name
        final String oldName = server.getDisplayName();

        // Update server
        server.setDisplayName(serverName);
        server.setURL(serverURL);
        server.setSshAddress(sshURL);

        new Thread(new Runnable() {
            public void run() {
                dbHelper.updateServer(db, server, oldName);
                handler.post(RefreshServers);
            }
        }).start();
        Toast.makeText(this, "Server updated", Toast.LENGTH_SHORT).show();
    }
    
    public void DeleteServer(final Server server) {
        new Thread(new Runnable() {
            public void run() {
                dbHelper.deleteServer(db, server);
                servers.remove(server);
                handler.post(RefreshServers);
            }
        }).start();
        Toast.makeText(this, "Server deleted!", Toast.LENGTH_SHORT).show();
    }

    public Runnable RefreshServers = new Runnable() {
        public void run() {
            adapter.notifyDataSetChanged();
        }
    };
}
