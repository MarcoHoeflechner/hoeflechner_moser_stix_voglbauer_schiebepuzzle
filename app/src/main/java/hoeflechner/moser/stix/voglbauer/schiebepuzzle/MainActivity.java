package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Hintergrund-Musik
    private Boolean music;
    private MediaPlayer mp;
    private static MediaPlayer soundEffectPlayer;
    private static Long startTime;
    private static double startTimeDouble;
    private static double playTimeDouble;

    private static int blackPosition=8;

    private static final int COLUMNS= 3;
    private static final int DIMENSIONS =COLUMNS * COLUMNS;

    private static String[] tileList;

    private static PuzzleView erkennungView;

    private static int columnWidth, columnHeight;

    public static final String UP="up";
    public static final String DOWN="down";
    public static final String LEFT="left";
    public static final String RIGHT="right";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Bestzeit dauerhaft speichern
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static boolean[] countTime = new boolean[1];

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences
        sharedPreferences = getSharedPreferences("TimeValue", 0);
        editor = sharedPreferences.edit();
        // Spielzeit wird als Long abgespeichert und in double umgewandelt
        Long tmp = sharedPreferences.getLong("playTime", Double.doubleToRawLongBits(100L));
        playTimeDouble = Double.longBitsToDouble(tmp);
        System.out.println(playTimeDouble);
        System.out.println("PlayTime Highscore: " + playTimeDouble);

        // Hintergrundmusik
        Intent intent = getIntent();
        music = intent.getExtras().getBoolean(MenuActivity.EXTRA_MESSAGE);

        // Soundeffekte
        soundEffectPlayer = MediaPlayer.create(this,R.raw.blop);
        soundEffectPlayer.setVolume(100,100);

        // Musik nur starten, wenn sie nicht in den Einstellungen deaktiviert wurde
        if (music)
        {
            mp = MediaPlayer.create(this,R.raw.background);
            mp.setVolume(50,50);
            mp.setLooping(true);
            mp.start();
        }

        init();

        scramble();

        setDimensions();

        // Zeitstempel, um die Zeit nach der das Puzzle fertig gestellt wurde, zu ermitteln
        startTime = System.currentTimeMillis();
        startTimeDouble = startTime.doubleValue();
        System.out.println(startTimeDouble);

        // Jede Sekunde hochzählen
        final Handler handler = new Handler();
        final int delay = 1000;
        int[] sekundenZahl = new int[1];
        sekundenZahl[0] = 1;
        int[] minutenZahl = new int[1];
        minutenZahl[0] = 0;
        boolean[] minuten = new boolean[1];
        minuten[0] = false;
        countTime[0] = true;

        // Jede Sekunde die Sekundenzahl hochzählen
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String zeit = String.valueOf(sekundenZahl[0]);
                // In Sekunden und Minuten anzeigen
                if (sekundenZahl[0] == 60)
                {
                    minutenZahl[0]++;
                    sekundenZahl[0] = 0;
                    minuten[0] = true;
                }

                if (minuten[0])
                {
                    zeit = minutenZahl[0] + ":" + sekundenZahl[0];
                }

                // Erste Zehn Sekunden nach neuer Minute
                if (minuten[0] && sekundenZahl[0] < 10)
                {
                    zeit = minutenZahl[0] + ":0" + sekundenZahl[0];
                }

                if (countTime[0])
                {
                    // Titel der ActionBar festlegen
                    getSupportActionBar().setTitle(zeit);
                    sekundenZahl[0]++;
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    // Wird aufgerufen wenn die App verlassen, jedoch nicht vollständig geschlossen wird
    @Override
    protected void onPause()
    {
        super.onPause();
        countTime[0] = false;
        if (music)
        {
            mp.pause();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        countTime[0] = true;
        if (music)
        {
            mp.start();
        }
    }

    private void setDimensions() {
        ViewTreeObserver vto = erkennungView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                erkennungView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = erkennungView.getWidth();
                int displayHeight = erkennungView.getHeight();

                int statusbarHeight = getStatusBarHeight(getApplicationContext());
                int requiredHeight = displayHeight - statusbarHeight;

                columnWidth = displayWidth / COLUMNS;
                columnHeight = requiredHeight / COLUMNS;

                display(getApplicationContext());
            }
        });
    }

    private int getStatusBarHeight(Context context){
        int result=0;
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if(resourceId > 0){
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    private static void display(Context context) {
        ArrayList<Button> buttons=new ArrayList<>();
        Button button;

        for (int i = 0; i < tileList.length; i++) {
            button = new Button(context);

            if(tileList[i].equals("0")) {
                button.setBackgroundResource(R.drawable.m1); //Bilder einfügen
            }
            else if(tileList[i].equals("1")){
                button.setBackgroundResource(R.drawable.m2);
            }
            else if(tileList[i].equals("2")){
                button.setBackgroundResource(R.drawable.m3);
            }
            else if(tileList[i].equals("3")){
                button.setBackgroundResource(R.drawable.m4);
            }
            else if(tileList[i].equals("4")){
                button.setBackgroundResource(R.drawable.m5);
            }
            else if(tileList[i].equals("5")){
                button.setBackgroundResource(R.drawable.m6);
            }
            else if(tileList[i].equals("6")){
                button.setBackgroundResource(R.drawable.m7);
            }
            else if(tileList[i].equals("7")){
                button.setBackgroundResource(R.drawable.m8);
            }
            else if(tileList[i].equals("8")){
                button.setBackgroundResource(R.drawable.black_image);
            }

            buttons.add(button);
        }

        erkennungView.setAdapter(new CustomAdapter(buttons, columnWidth, columnHeight));
    }

    private void scramble() {
        int index;
        String temp;
        Random random=new Random();

        for (int i = tileList.length-2; i > 0; i--) {
            index = random.nextInt(i+1);
            temp = tileList[index];
            tileList[index] = tileList[i];
            tileList[i]=temp;
        }
    }

    public void init(){
        erkennungView = (PuzzleView) findViewById(R.id.grid);
        erkennungView.setNumColumns(COLUMNS);
        tileList=new String[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            tileList[i]=String.valueOf(i);
        }
    }

    private static void swap(Context context, int position, int swap){
        // Soundeffekt abspielen
        soundEffectPlayer.start();
        String newPosition = tileList[position+swap];
        tileList[position+swap]= tileList[position];
        tileList[position]=newPosition;
        display(context);

       if( isSolved())
       {
           //Toast.makeText(context,"Puzzle gelöst!", Toast.LENGTH_SHORT).show();

           // Spielzeit berechnen und speichern
           Long currentPlayTime = System.currentTimeMillis();
           double currentPlayTimeDouble = currentPlayTime.doubleValue();
           System.out.println(currentPlayTimeDouble);
           System.out.println(startTimeDouble);
           double endTimeDouble = (currentPlayTimeDouble - startTimeDouble) / 1000;
           System.out.println("Spielzeit: " + String.format("%.2f", endTimeDouble));
           System.out.println(playTimeDouble);

           if (endTimeDouble < playTimeDouble)
           {
               // Spielzeit speichern
               System.out.println("Spielzeit wird gespeichert ...");
               long playTimeLong = Double.doubleToRawLongBits(endTimeDouble);
               editor.putLong("playTime", playTimeLong);
               editor.commit();
           }
       }
    }

    private static boolean isSolved()
    {
        boolean solved=true;

        /*for (int i = 0; i < tileList.length; i++) {
            if (tileList[i].equals(String.valueOf(i))) {
                solved=true;
            }
            else{
                solved=false;
                break;
            }
        }*/

        return solved;
    }

    public static void moveTiles(Context context, String richtung, int position){



    //Tiles oben links
        if(position == 0){
            if(richtung.equals(RIGHT) &&blackPosition==1){
                swap(context, position, 1);
                blackPosition=0;
            }
            else if(richtung.equals(DOWN)&&blackPosition==3){
                swap(context, position, COLUMNS);
                blackPosition=0;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }
        else if (position==blackPosition){
            Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
        }

        //Tiles oben mitte
        else if(position > 0 && position < COLUMNS-1){
            if(richtung.equals(LEFT) && blackPosition==0){
                swap(context, position, -1);
                blackPosition=1;
            }
            else if(richtung.equals(DOWN)&&blackPosition==4){
                swap(context, position, COLUMNS);
                blackPosition=1;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==2){
                swap(context, position, 1);
                blackPosition=1;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles oben rechts
        else if(position == COLUMNS-1){
            if(richtung.equals(LEFT)&&blackPosition==1){
                swap(context, position, -1);
                blackPosition=2;
            }
            else if(richtung.equals(DOWN)&& blackPosition==5){
                swap(context, position, COLUMNS);
                blackPosition=2;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles mitte links
        else if(position > COLUMNS-1 && position < DIMENSIONS-COLUMNS && position%COLUMNS == 0 ){
            if(richtung.equals(UP)&&blackPosition==0){
                swap(context, position, -COLUMNS);

                blackPosition=3;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==4){
                swap(context, position, 1);
                blackPosition=3;
            }
            else if(richtung.equals(DOWN)&&blackPosition==6){
                swap(context, position, COLUMNS);
                blackPosition=3;

            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles rechts mitte
        else if(position == COLUMNS*2 -1){
            if(richtung.equals(UP)&&blackPosition==2){
                swap(context, position, -COLUMNS);
                blackPosition=5;
            }
            else if(richtung.equals(LEFT) &&blackPosition==4){
                swap(context, position, -1);
                blackPosition=5;
            }
            else if(richtung.equals(DOWN)&&blackPosition==8){
                    swap(context, position, COLUMNS);
                    blackPosition=5;

            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }
        //Tiles unten rechts
        else if(position==COLUMNS*3-1){
            if(richtung.equals(UP)&&blackPosition==5){
                swap(context, position, -COLUMNS);
                blackPosition=8;
            }
            else if(richtung.equals(LEFT) &&blackPosition==7){
                swap(context, position, -1);
                blackPosition=8;
            }
            else if(richtung.equals(DOWN)&&blackPosition==8){

                    Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);

            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles unten links
        else if(position == DIMENSIONS - COLUMNS){
            if(richtung.equals(UP)&&blackPosition==3){
                swap(context, position, -COLUMNS);
                blackPosition=6;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==7){
                swap(context, position, 1);
                blackPosition=6;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles unten mitte
        else if(position < DIMENSIONS - 1 && position>DIMENSIONS-COLUMNS){
            if(richtung.equals(UP)&&blackPosition==4){
                swap(context, position, -COLUMNS);
                blackPosition=7;
            }
            else if(richtung.equals(LEFT)&&blackPosition==6){
                swap(context, position, -1);
                blackPosition=7;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==8){
                swap(context, position, 1);
                blackPosition=7;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles mitte
        else{
            if(richtung.equals(UP)&&blackPosition==1){
                swap(context, position, -COLUMNS);
                blackPosition=4;
            }
            else if(richtung.equals(LEFT)&&blackPosition==3){
                swap(context, position, -1);
                blackPosition=4;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==5){
                swap(context, position, 1);
                blackPosition=4;
            }
            else if(richtung.equals(DOWN)&&blackPosition==7) {
                swap(context, position, COLUMNS);
                blackPosition=4;
            }
        }

    }

}