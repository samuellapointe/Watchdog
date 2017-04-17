package ca.uqac.watchdog;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.List;
import java.util.Properties;

public class SSHConnectActivity extends AppCompatActivity {
    // Left drawer
    private String[] drawerElements;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private int posPressed = -1;
    private TextView usernameTextView;
    private TextView passwordTextView;
    private TextView serveraddressTextView;
    private boolean connexionSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sshconnect);

        Intent intent = getIntent();
        usernameTextView = (TextView) findViewById(R.id.username_textbox);
        passwordTextView = (TextView) findViewById(R.id.password_textbox);
        serveraddressTextView = (TextView) findViewById(R.id.serverurl_textbox);

        serveraddressTextView.setText(String.valueOf(intent.getStringExtra("hostname")));

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.serverdetails_toolbar);
        setSupportActionBar(toolbar);

        // Setup left drawer
        drawerElements = getResources().getStringArray(R.array.drawer_items);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerElements));

        drawerList.setItemChecked(1, true);

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

        Button connectButton = (Button) findViewById(R.id.ConnectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SSHConnectActivity.this,SSHConsoleActivity.class);
                i.putExtra("username",usernameTextView.getText().toString());
                i.putExtra("password",passwordTextView.getText().toString());
                i.putExtra("sshURL",serveraddressTextView.getText().toString());

                startActivityForResult(i,0);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 2) {
            Toast.makeText(SSHConnectActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
        }

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            if(position == 0)
            {
                posPressed = 0;
                onBackPressed();
            }
            if(position == 2)
            {
                posPressed = 2;
                onBackPressed();
            }

            drawerLayout.closeDrawer(drawerList);
        }
    }

    @Override
    public void onBackPressed() {
        if(posPressed == 0)
            setResult(0);
        if(posPressed == 2)
            setResult(1);
        super.onBackPressed();
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


}
