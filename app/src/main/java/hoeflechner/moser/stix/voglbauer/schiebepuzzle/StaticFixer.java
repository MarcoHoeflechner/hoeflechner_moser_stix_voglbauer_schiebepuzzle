package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class StaticFixer extends Application {

    //Attributes
    private Context mainContext;


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
    public StaticFixer(Context mainContext) {
        this.mainContext = mainContext;
        System.out.println("WICHTIG: "+mainContext.toString());

    }

    //Startet eine neue Activity
    public void startActivity() {

        Intent intent = new Intent(mainContext, Pop.class);
        mainContext.startActivity(intent);
    }

    //Getter & Setter
    public Context getMainContext() {
        return mainContext;
    }

    public void setMainContext(Context mainContext) {
        this.mainContext = mainContext;
    }
}

