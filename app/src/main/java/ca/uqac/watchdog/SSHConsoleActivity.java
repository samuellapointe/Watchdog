package ca.uqac.watchdog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
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
    private TextView commandeTextView;
    private TextView commandResults;
    private SSHThread sshThread;
    private Session session = null;
    private Channel channel = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sshconsole);
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        sshURL = getIntent().getStringExtra("sshURL");
        commandeTextView = (TextView) findViewById(R.id.editTextCommand);
        commandResults = (TextView) findViewById(R.id.commandResults);


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

        final Handler i = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what == 0)
                    Toast.makeText(SSHConsoleActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                if(msg.what == 1)
                    commandResults.setText((String) msg.obj);
                    //Toast.makeText(SSHConsoleActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();

            }
        };

        sshThread = new SSHThread(username,password,sshURL,h);

        new Thread(sshThread).start();

        Button sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session == null)
                    session = sshThread.getSession();

                ExecuteCommandThread execCThread = new ExecuteCommandThread(session,commandeTextView.getText().toString(),i);

                new Thread(execCThread).start();

                commandeTextView.setText("");
            }
        });


    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
