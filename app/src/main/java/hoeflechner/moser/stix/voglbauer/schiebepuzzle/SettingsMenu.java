package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SettingsMenu extends AppCompatActivity {

    //Referenz auf Ein/Ausschalter
    private Button musikButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);

        musikButton = (Button) findViewById(R.id.musik_button);

        musikButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchOnOff();
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