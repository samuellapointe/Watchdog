package ca.uqac.watchdog;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

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

    // Fréquence en secondes
    int frequency = 15;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
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
        // Run every interval
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                testTask();
            }
        }, 0, frequency, TimeUnit.SECONDS);
        return Service.START_NOT_STICKY;
    }

    private void testTask() {
        mp.start();
    }

}
