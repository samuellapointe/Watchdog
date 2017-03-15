package ca.uqac.watchdog;

import android.app.ListActivity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

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
        servers.add(new Server("Test", "https://heartbeat.haruha.ru/api.php"));
        adapter.notifyDataSetChanged();
    }
}
