package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;

public class PauseMenuActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause_menu);
    }

    public void resumeGame(View view)
    {
        finish();
    }

    public void restartGame(View view)
    {
        Intent prevIntent = getIntent();
        int columns = prevIntent.getExtras().getInt("columns");

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("random", 0);
        intent.putExtra(MenuActivity.EXTRA_MESSAGE, MenuActivity.music);
        intent.putExtra("columns", columns);

        startActivity(intent);
    }

    public void returnToMenu(View view)
    {
        Intent intent=new Intent(getApplicationContext(), MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void exitGame(View view)
    {
        finishAffinity();
        System.exit(0);
    }
}