package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class StaticFixer extends Application {

    //Attributes
    public Context mainContext;
    public MainActivity mainActivity;
    public static int randomImage;


    //-----Erster Versuch eine neue Activity in einer static Methode zu starten
    /*
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //Das Gewinner Pop-Up wird hier gestartet
    public void openPopUp() {
        Intent winIntent = new Intent(mainContext, Pop.class);
        startActivity(winIntent);
    }


    //------Die MainActivity ist static
    //------, deshalb muss die neue Activity extern gestartet werden

    //Getter & Setter
    public static Context getMainContext() {
        return mainContext;
    }

    public static void setMainContext(Context mContext) {
        StaticFixer.mainContext = mContext;
    }

    */

    //Basic Konstruktor
    public StaticFixer(Context mainContext, MainActivity mainActivity) {
        this.mainContext = mainContext;
        System.out.println("WICHTIG: "+mainContext.toString());
        this.mainActivity=mainActivity;


    }

    //Startet eine neue Activity
    public void startActivity(int randomImage, String score, String highScore, int columns) {

        Intent intent = new Intent(mainContext, Pop.class);
        intent.putExtra("random", randomImage);
        intent.putExtra("highScore", highScore);
        intent.putExtra("score", score);
        intent.putExtra("columns", columns);
        mainContext.startActivity(intent);
    }


    public void setRandomImage(int randomImage){
        this.randomImage=randomImage;
    }

    public static int getRandomImage() {
        return randomImage;
    }

    //Getter & Setter
    public Context getMainContext() {
        return mainContext;
    }

    public void setMainContext(Context mainContext) {
        this.mainContext = mainContext;
    }


    public void moveTiles(Context context, String richtung, int position, StaticFixer staticFixer){
        mainActivity.moveTiles(context,richtung, position, staticFixer);
    }
}

