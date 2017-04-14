package ca.uqac.watchdog;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class SSHConnectActivity extends AppCompatActivity {
    // Left drawer
    private String[] drawerElements;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sshconnect);

        Intent intent = getIntent();
        final TextView textViewServerUrl = (TextView) findViewById(R.id.serverurl_textbox);
        textViewServerUrl.setText(String.valueOf(intent.getStringExtra("hostname")));
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.serverdetails_toolbar);
        setSupportActionBar(toolbar);

        // Setup left drawer
        drawerElements = getResources().getStringArray(R.array.SSH_drawer_items);
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
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            if(position == 0)
            {
                onBackPressed();
            }


            drawerLayout.closeDrawer(drawerList);

        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
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
