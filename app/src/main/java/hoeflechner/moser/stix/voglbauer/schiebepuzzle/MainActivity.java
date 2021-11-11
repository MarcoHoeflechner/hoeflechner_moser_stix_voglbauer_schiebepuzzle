package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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

        init();

        scramble();

        setDimensions();

        // Zeitstempel, um die Zeit nach der das Puzzle fertig gestellt wurde, zu ermitteln
        startTime = System.currentTimeMillis()/1000;
    }

    // Wird aufgerufen wenn die App verlassen, jedoch nicht vollständig geschlossen wird
    @Override
    protected void onPause()
    {
        super.onPause();
        if (music)
        {
            mp.pause();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
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