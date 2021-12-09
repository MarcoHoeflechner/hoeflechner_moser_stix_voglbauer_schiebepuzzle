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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView imageView;
        imageView=(ImageView) findViewById(R.id.sampleImage);

        int randomImage=1;
        randomImage=(int) this.getIntent().getExtras().get("image");
        switch (randomImage){
            case 1:
                imageView.setImageResource(R.drawable.flamingo);
                break;
            case 2:
                imageView.setImageResource(R.drawable.graffiti);
                break;
            case 3:
                imageView.setImageResource(R.drawable.mountain);
                break;
            case 4:
                imageView.setImageResource(R.drawable.cash);
                break;
            case 5:
                imageView.setImageResource(R.drawable.mustang);
                break;
            default:
                imageView.setImageResource(R.drawable.flamingo);
                break;
        }
    }

    public void resumeGamefromImage(View view)
    {
        finish();
    }
}