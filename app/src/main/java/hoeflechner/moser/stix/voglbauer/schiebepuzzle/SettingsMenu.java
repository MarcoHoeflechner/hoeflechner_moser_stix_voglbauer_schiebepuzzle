package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SettingsMenu extends AppCompatActivity {

    //Referenz auf Ein/Ausschalter
    private Button musikButton;

    // Key, um die Variable in die nächste Activity übertragen zu können
    public static final String EXTRA_MESSAGE = "hoeflechner.moser.stix.voglbauer.schiebepuzzle.extra.MESSAGE";

    // Variable für Ein/Ausschalter
    private Boolean music = true;

    //Okay Button bringt einem zurück zur MainActivity
    private Button okayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);

        musikButton = (Button) findViewById(R.id.musik_button);
        okayButton = (Button) findViewById(R.id.return_button);

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

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //Wechselt den Text des Musikbuttons auf ein oder aus
    private void switchOnOff() {

        if(musikButton.getText().toString().equals("Ein")) {
            musikButton.setText("Aus");
        }
        else if(musikButton.getText().toString().equals("Aus")) {
            musikButton.setText("Ein");
        }
    }
}