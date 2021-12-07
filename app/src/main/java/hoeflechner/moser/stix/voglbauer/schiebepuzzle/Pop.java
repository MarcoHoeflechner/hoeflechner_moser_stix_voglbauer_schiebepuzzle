package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class Pop extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winner_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8),(int) (height*.8));
        TextView mScoreView=findViewById(R.id.scoreText);
        TextView mHighScoreView=findViewById(R.id.highscoreText);
        mScoreView.setText(getIntent().getExtras().getString("score")+" Sekunden");
        mHighScoreView.setText(getIntent().getExtras().getString("highScore")+" Sekunden");
    }

    public void nextGame(View view)
    {
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("random", 0);
        intent.putExtra(MenuActivity.EXTRA_MESSAGE, MenuActivity.music);
        startActivity(intent);
    }
    public void retryGame(View view)
    {

        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("random", StaticFixer.randomImage);
        intent.putExtra(MenuActivity.EXTRA_MESSAGE, MenuActivity.music);
        startActivity(intent);

    }

    public void popToMenu(View view)
    {
        Intent intent=new Intent(getApplicationContext(), MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
