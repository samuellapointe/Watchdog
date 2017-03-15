package ca.uqac.watchdog;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ServerListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverlist);

        // Context de l'activité
        final Context context = this;

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


        // Bouton de détails du serveur.
        /*final Button buttonServerDetails = (Button) findViewById(R.id.button2);

        // Action au clic du bouton de détails du serveur
        buttonServerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aller à la page des détails du serveur
                Intent i = new Intent(context, ServerDetailsActivity.class);
                startActivity(i);
            }
        });*/
    }
}
