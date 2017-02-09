package ca.uqac.watchdog;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Context de l'activité
        final Context context = this;

        // Trouver le bouton
        final Button clickButton = (Button) findViewById(R.id.button);

        // Donner une action au bouton
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Juste pour voir que ça marche
                clickButton.setText("OK!");

                // Starter le service
                Intent i = new Intent(context, WatchdogService.class);
                i.putExtra("TestKey", "Value"); // On peut passer des données à un service
                context.startService(i);
            }
        });
    }
}
