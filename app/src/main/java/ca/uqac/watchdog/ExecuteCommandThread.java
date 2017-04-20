package ca.uqac.watchdog;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by David on 2017-04-19.
 */

public class ExecuteCommandThread extends Thread {

    Session session;
    String command;
    Handler h;

    ExecuteCommandThread(Session session, String command,Handler h)
    {
        this.session = session;
        this.command = command;
        this.h = h;
    }

    public void run()
    {
        String result = "";
        try {
            Channel channel = session.openChannel("exec");

            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);

            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in=channel.getInputStream();

            channel.connect();

            byte[] tmp=new byte[1024];
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    result = new String(tmp, 0, i);
                }
                if(channel.isClosed()){
                    if(in.available()>0) continue;
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }

            Message msg = Message.obtain();
            msg.obj = result;
            msg.what = 1;
            h.sendMessage(msg);
        }
        catch (Exception e) {
            Message myMessage = Message.obtain();
            myMessage.obj = e;
            myMessage.what = 0;
            h.sendMessage(myMessage);
        }
    }
}
