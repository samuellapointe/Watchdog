package ca.uqac.watchdog;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sam on 2017-02-09.
 * Service qui permet de faire jouer un son toutes les x secondes
 */

public class WatchdogService extends Service {
    // Pour jouer notre son de test
    MediaPlayer mp;
    ScheduledExecutorService scheduleTaskExecutor;
    private RequestQueue mRequestQueue;
    // Fréquence en secondes
    int frequency = 60;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        // Init task scheduler
        scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
        // Init mediaplayer
        mp = MediaPlayer.create(this, R.raw.testsound);
        mp.setLooping(false);
    }

    @Override
    public void onDestroy()
    {
        mp.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = "https://heartbeat.haruha.ru/api.php";

        final StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
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
        }, 0, frequency, TimeUnit.SECONDS);
        return Service.START_NOT_STICKY;
    }
}
