package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SettingsMenu extends AppCompatActivity {

    //Referenz auf Ein/Ausschalter
    private Button musikButton;

    //Okay Button bringt einem zurück zur MainActivity
    private Button okayButton;

    // Musiksteuerung
    private Boolean music;
    public static final String EXTRA_REPLY = "hoeflechner.moser.stix.voglbauer.schiebepuzzle.extra.REPLY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);

        musikButton = (Button) findViewById(R.id.musik_button);
        okayButton = (Button) findViewById(R.id.return_button);

        // Musiksteuerung
        Intent intent = getIntent();
        music = intent.getExtras().getBoolean(MenuActivity.EXTRA_MESSAGE);

        // Button an Variable anpassen
        if (music)
        {
            musikButton.setText("Aus");
        }

        if (!music)
        {
            musikButton.setText("Ein");
        }

        musikButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchOnOff();
            }
        });
    }

    //Wechselt den Text des Musikbuttons auf ein oder aus
    private void switchOnOff()
    {
        if(musikButton.getText().toString().equals("Ein")) {
            musikButton.setText("Aus");
            music = true;
        }

        else if(musikButton.getText().toString().equals("Aus")) {
            musikButton.setText("Ein");
            music = false;
        }
    }

    public void launchMenuActivity(View view)
    {
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, music);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}