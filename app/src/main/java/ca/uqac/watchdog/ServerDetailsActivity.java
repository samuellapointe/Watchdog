package ca.uqac.watchdog;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Michael Dery on 2017-02-12.
 * Displays a server's informations, such as CPU and RAM usage.
 */

public class ServerDetailsActivity extends AppCompatActivity {
    public static final String SERVER = "Server";

    private double percentageUsage;
    // The server displayed on this page.
    Server mServer = null;

    // A reference to the service in charge of requesting and updating server informations.
    WatchdogService watchdogService = null;

    boolean mWatchdogServiceBound = false;

    // Left drawer
    private String[] drawerElements;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    public void closeGraph(View v){
        Fragment f = getFragmentManager().findFragmentByTag("graphFragment");
        if (f != null) {
            DialogFragment df = (DialogFragment) f;
            df.dismiss();
        }
    }

    public void displayGraphRAM(View v){
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = graphFragment.newInstance(mServer.getURL(), "MB", "mem" , 4, 1,"Mémoire");
        newFragment.show(getFragmentManager(), "graphFragment");
    }
    public void displayGraphCPU(View v){
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = graphFragment.newInstance(mServer.getURL(), "%", "cpu" , 4, 1, "CPU");
        newFragment.show(getFragmentManager(), "graphFragment");
    }

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

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.serverdetails_toolbar);
        setSupportActionBar(toolbar);

        // Setup left drawer
        drawerElements = getResources().getStringArray(R.array.drawer_items);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerElements));
        drawerList.setItemChecked(0, true);

        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Drawer toggle button
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
                ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Do something fun
            }

            public void onDrawerOpened(View drawerView) {
                drawerList.bringToFront();
                drawerLayout.requestLayout();
                super.onDrawerOpened(drawerView);
                // Do something cool
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Show a test notification if server name is "Server" for presentation.
        if(mServer != null)
        {
            if(mServer.getDisplayName().compareToIgnoreCase("Server123") == 0)
            {
                PromptNotificationServerNotResponding();
            }
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            if(position == 1)
            {
                Intent i;
                i = new Intent(getApplicationContext(), SSHConnectActivity.class);
                i.putExtra("hostname",mServer.getSshAddress());
                startActivityForResult(i,0);
                drawerLayout.closeDrawer(drawerList);
            }
            if(position == 2)
            {
                onBackPressed();
                drawerLayout.closeDrawer(drawerList);
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 0) {
            drawerList.setItemChecked(0, true);
        }
        if(resultCode == 1) {
            finish();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            //Display server name
            final TextView textViewServerName = (TextView) findViewById(R.id.nomServer);
            textViewServerName.setText(String.valueOf(mServer.getDisplayName()));

            // Display Ram in use
            final TextView textViewRam = (TextView) findViewById(R.id.valueRam);
            textViewRam.setText(String.valueOf(mServer.getRam()) + " MB");

            //Display Ram Capacity
            final TextView textViewRamCap = (TextView) findViewById(R.id.ramCapacity);
            textViewRamCap.setText(String.valueOf(mServer.getRamCap()) + " MB");

            //Display percentage of used Ram
            percentageUsage = (mServer.getRam()/mServer.getRamCap())*100;
            final TextView textViewRamPerc = (TextView) findViewById(R.id.ramPercentage);
            textViewRamPerc.setText(String.format("%.2f",percentageUsage) + "%");

            // Display CPU
            final TextView textViewCpu = (TextView) findViewById(R.id.valueCpu);
            textViewCpu.setText(String.valueOf(mServer.getCpu()) + "%");
        }
    }

    private void PromptNotificationServerNotResponding() {
        // Create notification text.
        String notificationMessage = "Server not responding";
        if (mServer != null) {
            notificationMessage = mServer.getDisplayName() + ": " + notificationMessage;
        }

        // Create notification sound.
        Uri uriSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create notification action (open current server activity).
        // Preserve navigation: "Back" goes to Server list.
        Intent resultIntent = new Intent(this, ServerDetailsActivity.class);
        resultIntent.putExtra(SERVER, mServer);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ServerListActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification.
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("Watchdog")
                        .setContentText(notificationMessage)
                        .setSound(uriSound)
                        .setContentIntent(resultPendingIntent);

        // Send notification to notification manager.
        int notificationId = 001;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
