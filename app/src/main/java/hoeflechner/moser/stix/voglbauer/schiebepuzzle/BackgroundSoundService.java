package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

// Spielt Hintergrundmusik im Menü und im Puzzle-Screen
public class BackgroundSoundService extends Service
{
    private static final String TAG = null;
    MediaPlayer player;
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
        player.setVolume(80,80); // damit man den Button Sound noch hört
        // Debug-Meldung
        Toast.makeText(this, "Hintergrundmusik wird gespielt", Toast.LENGTH_SHORT).show();
    }

    /*public int onStartCommand(Intent intent, int flags, int startId)
    {
        player.start();
        return 1;
    }*/

    public void onStart(Intent intent, int startId)
    {
        // Debug-Meldung
        Toast.makeText(this, "Hintergrundmusik wird gestartet", Toast.LENGTH_SHORT).show();
        player.start();
    }

    public IBinder onUnBind(Intent arg0)
    {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop()
    {

    }

    @Override
    public void onDestroy()
    {
        // Debug-Meldung
        Toast.makeText(this, "Hintergrundmusik wird gestoppt", Toast.LENGTH_SHORT).show();
        player.stop();
        player.release();
    }
}
