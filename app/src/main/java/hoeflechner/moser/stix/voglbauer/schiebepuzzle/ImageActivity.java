package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView imageView;
        imageView=(ImageView) findViewById(R.id.sampleImage);
        //Funktioniert noch nicht ganz
        //imageView.setImageDrawable((Drawable) getIntent().getExtras().get("image"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
    }

    public void resumeGamefromImage(View view)
    {
        finish();
    }
}