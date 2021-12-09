package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DifficultyActivity extends AppCompatActivity {

    // Hintergrund-Musik
    private boolean music;

    // Key, um die Variable in die nächste Activity übertragen zu können
    public static final String EXTRA_MESSAGE = "hoeflechner.moser.stix.voglbauer.schiebepuzzle.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        // Hintergrundmusik
        Intent intent = getIntent();
        music = intent.getExtras().getBoolean(MenuActivity.EXTRA_MESSAGE);
    }

    // Startet das leichte Level
    public void openEasyLevel(View view)
    {
        // TODO: Richtige Klasse beim Intent starten dort, die Daten mit getExtra rausholen
        Intent intent = new Intent(this, MainActivity.class);
        // Musiksteuerung
        intent.putExtra("random", 0);
        intent.putExtra(EXTRA_MESSAGE, music);
        intent.putExtra("columns",3);
        startActivity(intent);

        //TODO: send 3x3
    }

    // Startet das normale Level
    public void openNormalLevel(View view)
    {
        // TODO: Richtige Klasse beim Intent starten dort, die Daten mit getExtra rausholen
        Intent intent = new Intent(this, MainActivity.class);
        // Musiksteuerung
        intent.putExtra("random", 0);
        intent.putExtra(EXTRA_MESSAGE, music);
        intent.putExtra("columns",4);
        startActivity(intent);

        //TODO: send 4x4
    }

    // Startet das schwere Level
    public void openHardLevel(View view)
    {
        // TODO: Richtige Klasse beim Intent starten dort, die Daten mit getExtra rausholen
        Intent intent = new Intent(this, MainActivity.class);
        // Musiksteuerung
        intent.putExtra("random", 0);
        intent.putExtra(EXTRA_MESSAGE, music);
        intent.putExtra("columns",5);
        startActivity(intent);

        //TODO: send 5x5
    }
}