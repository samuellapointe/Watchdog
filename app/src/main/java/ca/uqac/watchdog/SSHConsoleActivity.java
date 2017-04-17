package ca.uqac.watchdog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * Created by David on 2017-04-17.
 */

public class SSHConsoleActivity extends Activity {
    private String username;
    private String password;
    private String sshURL;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sshconsole);
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        sshURL = getIntent().getStringExtra("sshURL");

        final Handler h = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what == 0) {
                    Toast.makeText(SSHConsoleActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
                    RelativeLayout consoleLayout = (RelativeLayout) findViewById(R.id.consoleLayout);

                    consoleLayout.setVisibility(View.VISIBLE);
                }

                if(msg.what == 1)
                {
                    setResult(2);
                    finish();
                }

            }
        };
        Session session = null;

        SSHThread sshThread = new SSHThread(username,password,sshURL,h);

        new Thread(sshThread).start();

    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
