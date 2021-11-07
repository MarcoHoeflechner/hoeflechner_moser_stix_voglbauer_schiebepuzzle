package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {

    private ImageButton playButton;
    private ImageButton settingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        playButton = (ImageButton) findViewById(R.id.playButton);
        settingsButton = (ImageButton) findViewById(R.id.settings_button);


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToPlayground();
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
        startActivity(intent);
    }

    private void switchToPlayground() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}