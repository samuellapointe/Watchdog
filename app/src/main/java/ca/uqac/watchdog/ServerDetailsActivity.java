package ca.uqac.watchdog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Michael Dery on 2017-02-12.
 * Displays a server's informations, such as CPU and RAM usage.
 */

public class ServerDetailsActivity extends AppCompatActivity {
    public static final String SERVER = "Server";

    // The server displayed on this page.
    Server mServer = null;

    // A reference to the service in charge of requesting and updating server informations.
    WatchdogService watchdogService = null;

    boolean mWatchdogServiceBound = false;


    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            WatchdogService.LocalBinder binder = (WatchdogService.LocalBinder) service;
            watchdogService = binder.getService();
            watchdogService.listener = new WatchdogService.WatchdogServiceListener() {
                @Override
                public void ServerUpdated(Server server) {
                    mServer = server;
                    UpdateServer();
                }
            };
            mWatchdogServiceBound = true;
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            mWatchdogServiceBound = false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverdetails);

        // Retrieve server parameter
        Intent intent = getIntent();
        if(intent != null) {
            Parcelable parcelableServer = intent.getParcelableExtra(SERVER);
            if (parcelableServer != null) {
                mServer = (Server) parcelableServer;
                UpdateServer();

                // Bind service that will periodically get and update server informations.
                Context context = this;
                Intent i = new Intent(context, WatchdogService.class);
                i.putExtra(WatchdogService.SERVER, mServer);
                bindService(i, mConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mWatchdogServiceBound) {
            unbindService(mConnection);
            mWatchdogServiceBound = false;

            watchdogService.StopSelfCustom();
        }
    }

    private void UpdateServer()
    {
        if(mServer != null) {
            // Display Ram
            final TextView textViewRam = (TextView) findViewById(R.id.valueRam);
            textViewRam.setText(String.valueOf(mServer.getRam()));

            // Display CPU
            final TextView textViewCpu = (TextView) findViewById(R.id.valueCpu);
            textViewCpu.setText(String.valueOf(mServer.getCpu()));
        }
    }
}
