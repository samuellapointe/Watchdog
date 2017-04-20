package ca.uqac.watchdog;

import android.os.Handler;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by David on 2017-04-17.
 */

public class SSHThread extends Thread {
    Session session = null;
    String username;
    String password;
    String sshURL;
    Handler h;
    Channel channel = null;

    SSHThread(String username, String password, String sshURL, Handler h)
    {
        this.username = username;
        this.password = password;
        this.sshURL = sshURL;
        this.h = h;
    }

    public void run()
    {
        JSch jsch = new JSch();

        try {
            session = jsch.getSession(username, sshURL, 22);

            session.setPassword(password);

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);

            session.connect();


            if(session.isConnected())
                h.sendEmptyMessage(0);
            else
                h.sendEmptyMessage(1);


        }

        catch (JSchException e) {
        }
    }

    public Session getSession(){return session;}
    public Channel getChannel() {return channel; }
}
