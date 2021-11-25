package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
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
    private static Long playTime;
    private static double startTimeDouble;
    private static double playTimeDouble;

    private static int blackPosition=8;

    private static final int COLUMNS= 3;
    private static final int DIMENSIONS =COLUMNS * COLUMNS;

    private static String[] tileList;

    private static PuzzleView puzzleView;

    private static int columnWidth, columnHeight;

    public static final String UP="up";
    public static final String DOWN="down";
    public static final String LEFT="left";
    public static final String RIGHT="right";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private StaticFixer staticFixer = new StaticFixer(MainActivity.this);

    // Bestzeit dauerhaft speichern
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static boolean[] countTime = new boolean[1];

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            pauseGame();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences
        sharedPreferences = getSharedPreferences("TimeValue", 0);
        editor = sharedPreferences.edit();
        playTime = sharedPreferences.getLong("playTime", 0);
        System.out.println("PlayTime: " + playTime);

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

        init(staticFixer);

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

        //Win PopUp öffnen
        staticFixer.startActivity();
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
        ViewTreeObserver vto = puzzleView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                puzzleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = puzzleView.getWidth();
                int displayHeight = puzzleView.getHeight();

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

        puzzleView.setAdapter(new CustomAdapter(buttons, columnWidth, columnHeight));
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

    public void init(StaticFixer staticFixer){
        puzzleView = (PuzzleView) findViewById(R.id.grid);
        puzzleView.setStaticFixer(staticFixer);
        puzzleView.setNumColumns(COLUMNS);
        tileList=new String[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            tileList[i]=String.valueOf(i);
        }
    }

    private static void swap(Context context, int position, int swap, StaticFixer staticFixer){

        //Haptisches Feedback
        puzzleView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

        // Soundeffekt abspielen
        soundEffectPlayer.start();
        String newPosition = tileList[position+swap];
        tileList[position+swap]= tileList[position];
        tileList[position]=newPosition;
        display(context);



        //TODO: Pop hier öffnen
        //Überprüfung, ob das Puzzle gelöst wurde
        if(isSolved())
        {
            Toast.makeText(context,"Puzzle gelöst!", Toast.LENGTH_SHORT).show();

            //Win PopUp öffnen
            staticFixer.startActivity();

           // Spielzeit berechnen und speichern
           Long currentPlayTime = System.currentTimeMillis()/1000 - startTime;
           String playTimeString = currentPlayTime.toString();
           System.out.println("Spielzeit: " + playTimeString);

           if (currentPlayTime < playTime)
           {
               // Spielzeit speichern
               editor.putLong("playTime", currentPlayTime);
               editor.commit();
           }
       }
    }

    private static boolean isSolved()
    {
        boolean solved=false;

        for (int i = 0; i < tileList.length; i++) {
            if (tileList[i].equals(String.valueOf(i))) {
                solved=true;
            }
            else{
                solved=false;
                break;
            }
        }

        return solved;
    }

    public static void moveTiles(Context context, String richtung, int position, StaticFixer staticFixer){



    //Tiles oben links
        if(position == 0){
            if(richtung.equals(RIGHT) &&blackPosition==1){
                swap(context, position, 1, staticFixer);
                blackPosition=0;
            }
            else if(richtung.equals(DOWN)&&blackPosition==3){
                swap(context, position, COLUMNS, staticFixer);
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
                swap(context, position, -1, staticFixer);
                blackPosition=1;
            }
            else if(richtung.equals(DOWN)&&blackPosition==4){
                swap(context, position, COLUMNS, staticFixer);
                blackPosition=1;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==2){
                swap(context, position, 1, staticFixer);
                blackPosition=1;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles oben rechts
        else if(position == COLUMNS-1){
            if(richtung.equals(LEFT)&&blackPosition==1){
                swap(context, position, -1, staticFixer);
                blackPosition=2;
            }
            else if(richtung.equals(DOWN)&& blackPosition==5){
                swap(context, position, COLUMNS, staticFixer);
                blackPosition=2;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles mitte links
        else if(position > COLUMNS-1 && position < DIMENSIONS-COLUMNS && position%COLUMNS == 0 ){
            if(richtung.equals(UP)&&blackPosition==0){
                swap(context, position, -COLUMNS, staticFixer);

                blackPosition=3;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==4){
                swap(context, position, 1, staticFixer);
                blackPosition=3;
            }
            else if(richtung.equals(DOWN)&&blackPosition==6){
                swap(context, position, COLUMNS, staticFixer);
                blackPosition=3;

            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles rechts mitte
        else if(position == COLUMNS*2 -1){
            if(richtung.equals(UP)&&blackPosition==2){
                swap(context, position, -COLUMNS, staticFixer);
                blackPosition=5;
            }
            else if(richtung.equals(LEFT) &&blackPosition==4){
                swap(context, position, -1, staticFixer);
                blackPosition=5;
            }
            else if(richtung.equals(DOWN)&&blackPosition==8){
                    swap(context, position, COLUMNS, staticFixer);
                    blackPosition=5;

            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }
        //Tiles unten rechts
        else if(position==COLUMNS*3-1){
            if(richtung.equals(UP)&&blackPosition==5){
                swap(context, position, -COLUMNS, staticFixer);
                blackPosition=8;
            }
            else if(richtung.equals(LEFT) &&blackPosition==7){
                swap(context, position, -1, staticFixer);
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
                swap(context, position, -COLUMNS, staticFixer);
                blackPosition=6;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==7){
                swap(context, position, 1, staticFixer);
                blackPosition=6;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles unten mitte
        else if(position < DIMENSIONS - 1 && position>DIMENSIONS-COLUMNS){
            if(richtung.equals(UP)&&blackPosition==4){
                swap(context, position, -COLUMNS, staticFixer);
                blackPosition=7;
            }
            else if(richtung.equals(LEFT)&&blackPosition==6){
                swap(context, position, -1, staticFixer);
                blackPosition=7;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==8){
                swap(context, position, 1, staticFixer);
                blackPosition=7;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles mitte
        else{
            if(richtung.equals(UP)&&blackPosition==1){
                swap(context, position, -COLUMNS, staticFixer);
                blackPosition=4;
            }
            else if(richtung.equals(LEFT)&&blackPosition==3){
                swap(context, position, -1, staticFixer);
                blackPosition=4;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==5){
                swap(context, position, 1, staticFixer);
                blackPosition=4;
            }
            else if(richtung.equals(DOWN)&&blackPosition==7) {
                swap(context, position, COLUMNS, staticFixer);
                blackPosition=4;
            }
        }
    }

    public void pauseGame()
    {
        Intent intent = new Intent(this, PauseMenuActivity.class);
        startActivity(intent);
    }

}