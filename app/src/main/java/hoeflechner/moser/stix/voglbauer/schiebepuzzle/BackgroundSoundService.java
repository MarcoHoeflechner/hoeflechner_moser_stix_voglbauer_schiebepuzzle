package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

// Spielt Hintergrundmusik im Menü und im Puzzle-Screen
public class BackgroundSoundService extends Service
{
    private static final String TAG = "UEService";
    private Timer timer;
    private static final int delay = 500; // delay for 0,5 sec before first start
    private static final int period = 1000; // repeat check every sec.
    private MediaPlayer player;

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.background);
        player.setLooping(true); // Set looping
        player.setVolume(50,50); // damit man den Button Sound noch hört
        // Debug-Meldung
        Toast.makeText(this, "Hintergrundmusik wird gespielt", Toast.LENGTH_SHORT).show();
    }

    public void onStart(Intent intent, int startId)
    {
        // Debug-Meldung
        Toast.makeText(this, "Hintergrundmusik wird gestartet", Toast.LENGTH_SHORT).show();
        player.start();
    }

    // handles a Start command
    private void handleCommand(Intent intent) {
        Log.d(TAG, "service is starting");

        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    checkActivityForeground();
                }
            }, delay, period);
        }
    }

    protected void checkActivityForeground() {
        Log.d(TAG, "start checking for Activity in foreground");
        Intent intent = new Intent();
        //intent.setAction(MainActivity.UE_ACTION);
        sendOrderedBroadcast(intent, null, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                int result = getResultCode();

                if (result != Activity.RESULT_CANCELED) { // Activity caught it
                    Log.d(TAG, "An activity caught the broadcast, result " + result);
                    activityInForeground();
                    return;
                }
                Log.d(TAG, "No activity did catch the broadcast.");
                noActivityInForeground();
            }
        }, null, Activity.RESULT_CANCELED, null, null);
    }

    protected void activityInForeground() {
        Log.d(TAG, "starting method which gets called when an SureveillanceActivity is in foreground");

        // TODO something you want to happen when an Activity is in the foreground
    }

    protected void noActivityInForeground() {
        Log.d(TAG, "starting method which gets called when no SureveillanceActivity is in foreground");

        // TODO something you want to happen when no Activity is in the foreground
        player.pause();

        stopSelf(); // quit
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        timer.cancel();
        // Debug-Meldung
        Toast.makeText(this, "Hintergrundmusik wird gestoppt", Toast.LENGTH_SHORT).show();
        player.stop();
        player.release();
    }
}
