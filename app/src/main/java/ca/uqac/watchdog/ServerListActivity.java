package ca.uqac.watchdog;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ServerListActivity extends ListActivity {

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


        // Trouver le bouton
        //final Button clickButton = (Button) findViewById(R.id.button);

        // Donner une action au bouton
        /*clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Juste pour voir que ça marche
                clickButton.setText("OK!");

                // Starter le service
                Intent i = new Intent(context, WatchdogService.class);
                i.putExtra("TestKey", "Value"); // On peut passer des données à un service
                context.startService(i);
            }
        });*/
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
        servers.add(new Server(serverName, serverURL));
        adapter.notifyDataSetChanged();
    }
}
