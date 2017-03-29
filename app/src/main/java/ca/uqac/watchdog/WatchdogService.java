package ca.uqac.watchdog;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sam on 2017-02-09.
 * Service qui obtient les informations d'un serveur à intervalles réguliers.
 */

public class WatchdogService extends Service {
    public static final String SERVER = "Server";

    // Object that listens this service.
    public WatchdogServiceListener listener = null;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    ScheduledExecutorService scheduleTaskExecutor;

    private RequestQueue mRequestQueue;

    int serverRequestFrequencyInSeconds = 10;

    Server server = null;

    public class LocalBinder extends Binder {
        WatchdogService getService() {
            // Return this instance of LocalService so clients can call public methods
            return WatchdogService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        String url = "";

        // Retrieve server parameter
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                server = bundle.getParcelable(WatchdogService.SERVER);
                url = server.getURL();
            }
        }

        final StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        // Debug message to see server response.
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                        // Parse response {"cpu":"1.70","mem":"328","memcap":512}
                        List<String> parts = Arrays.asList(response.split("\""));
                        if(parts.size() >= 11) {
                            server.setCpu(Double.parseDouble(parts.get(3)));
                            server.setRam(Double.parseDouble(parts.get(7)));
                        }
                        else
                        {
                            // Response couldn't be parsed.
                            server.setCpu(-1);
                            server.setRam(-1);
                        }

                        // Update listener with new information.
                        ListenerServerUpdated();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

                        // Set invalid value to make it clear that data is incorrect.
                        server.setCpu(-1);
                        server.setRam(-1);

                        // Update listener with new information.
                        ListenerServerUpdated();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("method", "pulse");
                return params;
            }
        };

        // Run every interval
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                mRequestQueue.add(strRequest);
            }
        }, 0, serverRequestFrequencyInSeconds, TimeUnit.SECONDS);

        return mBinder;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        // Init task scheduler
        scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    private void ListenerServerUpdated()
    {
        if(listener != null) {
            listener.ServerUpdated(server);
        }
    }

    public interface WatchdogServiceListener {
        void ServerUpdated(Server server);
    }

    public void StopSelfCustom() {
        if(scheduleTaskExecutor != null) {
            // We need to stop scheduled task before stopping,
            // otherwise service will remain active
            // even when unbound.
            scheduleTaskExecutor.shutdownNow();
        }
        this.stopSelf();
    }
}