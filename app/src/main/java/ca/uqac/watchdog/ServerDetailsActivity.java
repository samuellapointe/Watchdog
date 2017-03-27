package ca.uqac.watchdog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Michael Dery on 2017-02-12.
 * Activité qui affiche les informations d'un serveur.
 */

public class ServerDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverdetails);

        // Retrieve server parameter
        Server server = null;
        Intent intent = getIntent();
        if(intent != null) {
            Parcelable parcelableServer = intent.getParcelableExtra("Server");
            if(parcelableServer != null) {
                server = (Server) parcelableServer;
            }
        }

        if(server != null) {
            // Display Ram
            final TextView textViewRam = (TextView) findViewById(R.id.valueRam);
            textViewRam.setText(String.valueOf(server.getRam()));

            // Display CPU
            final TextView textViewCpu = (TextView) findViewById(R.id.valueCpu);
            textViewCpu.setText(String.valueOf(server.getCpu()));
        }
    }

}
