package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {

    private ImageButton playButton;
    private ImageButton settingsButton;

    // Key, um die Variable in die nächste Activity übertragen zu können
    public static final String EXTRA_MESSAGE = "hoeflechner.moser.stix.voglbauer.schiebepuzzle.extra.MESSAGE";

    // Variable für Ein/Ausschalter
    private Boolean music;

    public static final int TEXT_REQUEST = 1;

    // Musikvariable dauerhaft speichern
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // SharedPreferences
        sharedPreferences = getSharedPreferences("MusicValue", 0);
        editor = sharedPreferences.edit();

        // Der Musikwert wird abgerufen, wenn es noch keinen gibt, wird der defaultwert, hier false verwendet
        music = sharedPreferences.getBoolean("MusicValue", true);

        playButton = (ImageButton) findViewById(R.id.playButton);
        settingsButton = (ImageButton) findViewById(R.id.settings_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToDifficultyChoosing();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSettings();
            }
        });
    }

    private void switchToSettings() {
        Intent intent = new Intent(this, SettingsMenu.class);
        intent.putExtra(EXTRA_MESSAGE, music);
        // Damit ein Wert zurückgegeben werden kann
        startActivityForResult(intent, TEXT_REQUEST);
    }

    private void switchToDifficultyChoosing() {
        Intent intent = new Intent(this, DifficultyActivity.class);
        // Musiksteuerung
        intent.putExtra(EXTRA_MESSAGE, music);
        startActivity(intent);
    }

    // Boolean-Variable ggf. bearbeiten
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                music = data.getExtras().getBoolean(SettingsMenu.EXTRA_REPLY);
                System.out.println("Musik: " + music);

                // Variable abspeichern
                editor.putBoolean("MusicValue", music);
                editor.commit();
            }
        }

    }
}