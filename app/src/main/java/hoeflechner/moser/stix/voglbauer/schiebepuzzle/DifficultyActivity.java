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
        Intent intent = new Intent(this, DifficultyActivity.class);
        // Musiksteuerung
        intent.putExtra("random", 0);
        intent.putExtra(EXTRA_MESSAGE, music);
        startActivity(intent);
    }

    // Startet das normale Level
    public void openNormalLevel(View view)
    {
        // TODO: Richtige Klasse beim Intent starten dort, die Daten mit getExtra rausholen
        Intent intent = new Intent(this, DifficultyActivity.class);
        // Musiksteuerung
        intent.putExtra("random", 0);
        intent.putExtra(EXTRA_MESSAGE, music);
        startActivity(intent);
    }

    // Startet das schwere Level
    public void openHardLevel(View view)
    {
        // TODO: Richtige Klasse beim Intent starten dort, die Daten mit getExtra rausholen
        Intent intent = new Intent(this, DifficultyActivity.class);
        // Musiksteuerung
        intent.putExtra("random", 0);
        intent.putExtra(EXTRA_MESSAGE, music);
        startActivity(intent);
    }
}