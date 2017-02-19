package ca.uqac.watchdog;

import android.os.Bundle;
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

        final TextView textViewRam = (TextView) findViewById(R.id.valueRam);
        textViewRam.setText("999999 GB");
    }

}
