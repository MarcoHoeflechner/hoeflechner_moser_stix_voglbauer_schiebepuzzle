package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
    public int counter;
    public static final int FLAG_ACTIVITY_NO_HISTORY = 0;


    private  int blackPosition=8;

    private final int COLUMNS= 3;
    private  final int DIMENSIONS =COLUMNS * COLUMNS;

    private String[] tileList;

    private  PuzzleView puzzleView;

    public  int randomImage;
    public  String zeit;

    public int[]flamingoIDs=new int[8];
    public int[]graffitiIDs=new int[8];
    public int[]mountainIDs=new int[8];
    public int[]moneyIDs=new int[8];
    public int[]syntheticIDs=new int[8];

    private  int columnWidth, columnHeight;

    public final String UP="up";
    public final String DOWN="down";
    public final String LEFT="left";
    public final String RIGHT="right";

    private StaticFixer staticFixer = new StaticFixer(MainActivity.this, this);

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
        if (id == R.id.imageButton) {
            Intent intent = new Intent(this, ImageActivity.class);

            switch (randomImage){
                case 1:
                    intent.putExtra("image", R.drawable.flamingo1);
                    break;
                case 2:
                    intent.putExtra("image", R.drawable.graffiti1);
                    break;
                case 3:
                    intent.putExtra("image", R.drawable.m1);
                    break;
                case 4:
                    intent.putExtra("image", R.drawable.money1);
                    break;
                case 5:
                    intent.putExtra("image", R.drawable.part1);
                    break;
                default:
                    intent.putExtra("image", R.drawable.flamingo1);
                    break;
            }
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        counter=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Alle Drawable IDs einlesen
        readAllImages();

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
                zeit = String.valueOf(sekundenZahl[0]);
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

    public void display(Context context) {
        ArrayList<Button> buttons=new ArrayList<>();
        Button button;

        int id=getResources().getIdentifier("flamingo1", "drawable",this.getPackageName());

        int []tempArray=new int[8];

        switch (randomImage){
            case 1:
                tempArray = flamingoIDs.clone();
                break;
            case 2:
                tempArray = graffitiIDs.clone();
                break;
            case 3:
                tempArray = mountainIDs.clone();
                break;
            case 4:
                tempArray = moneyIDs.clone();
                break;
            case 5:
                tempArray = syntheticIDs.clone();
                break;
            default:
                tempArray = flamingoIDs.clone();
                break;
        }

        for (int i = 0; i < tileList.length; i++) {
            button = new Button(context);

            if(tileList[i].equals("0")) {
                button.setBackgroundResource(tempArray[0]); //Bilder einfügen
            }
            else if(tileList[i].equals("1")){
                button.setBackgroundResource(tempArray[1]);
            }
            else if(tileList[i].equals("2")){
                button.setBackgroundResource(tempArray[2]);
            }
            else if(tileList[i].equals("3")){
                button.setBackgroundResource(tempArray[3]);
            }
            else if(tileList[i].equals("4")){
                button.setBackgroundResource(tempArray[4]);
            }
            else if(tileList[i].equals("5")){
                button.setBackgroundResource(tempArray[5]);
            }
            else if(tileList[i].equals("6")){
                button.setBackgroundResource(tempArray[6]);
            }
            else if(tileList[i].equals("7")){
                button.setBackgroundResource(tempArray[7]);
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
        counter=0;
        Bundle extras=getIntent().getExtras();
        if(extras.getInt("random")!=0){
            randomImage=extras.getInt("random");
        }
        else {
            generateRandom();
        }
        puzzleView = (PuzzleView) findViewById(R.id.grid);
        puzzleView.setStaticFixer(staticFixer);
        puzzleView.setNumColumns(COLUMNS);
        tileList=new String[DIMENSIONS];
        //Arrays.fill(tileList, null);
        for (int i = 0; i < DIMENSIONS; i++) {
            tileList[i]=String.valueOf(i);
        }
    }

    private void swap(Context context, int position, int swap, StaticFixer staticFixer){

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
            //staticFixer.startActivity(randomImage);

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

    private boolean isSolved()
    {
        boolean solved=false;

        for (int i = 0; i < tileList.length; i++) {
            System.out.println(String.valueOf(i));
            if (tileList[i].equals(String.valueOf(i))) {
                solved=true;
            }
            else{
                solved=false;
                break;
            }
        }
        if(counter==5){
            Long tmp=sharedPreferences.getLong("playTime", Double.doubleToRawLongBits(100L));
            playTimeDouble=Double.longBitsToDouble(tmp);

            staticFixer.startActivity(randomImage,zeit, Double.toString(playTimeDouble));

        }
        counter++;
        return solved;
    }

    public void moveTiles(Context context, String richtung, int position, StaticFixer staticFixer){



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

    public void readAllImages(){
        flamingoIDs[0]=getResources().getIdentifier("flamingo1", "drawable",this.getPackageName());
        flamingoIDs[1]=getResources().getIdentifier("flamingo2", "drawable",this.getPackageName());
        flamingoIDs[2]=getResources().getIdentifier("flamingo3", "drawable",this.getPackageName());
        flamingoIDs[3]=getResources().getIdentifier("flamingo4", "drawable",this.getPackageName());
        flamingoIDs[4]=getResources().getIdentifier("flamingo5", "drawable",this.getPackageName());
        flamingoIDs[5]=getResources().getIdentifier("flamingo6", "drawable",this.getPackageName());
        flamingoIDs[6]=getResources().getIdentifier("flamingo7", "drawable",this.getPackageName());
        flamingoIDs[7]=getResources().getIdentifier("flamingo8", "drawable",this.getPackageName());

        graffitiIDs[0]=getResources().getIdentifier("graffiti1", "drawable",this.getPackageName());
        graffitiIDs[1]=getResources().getIdentifier("graffiti2", "drawable",this.getPackageName());
        graffitiIDs[2]=getResources().getIdentifier("graffiti3", "drawable",this.getPackageName());
        graffitiIDs[3]=getResources().getIdentifier("graffiti4", "drawable",this.getPackageName());
        graffitiIDs[4]=getResources().getIdentifier("graffiti5", "drawable",this.getPackageName());
        graffitiIDs[5]=getResources().getIdentifier("graffiti6", "drawable",this.getPackageName());
        graffitiIDs[6]=getResources().getIdentifier("graffiti7", "drawable",this.getPackageName());
        graffitiIDs[7]=getResources().getIdentifier("graffiti8", "drawable",this.getPackageName());

        mountainIDs[0]=getResources().getIdentifier("m1", "drawable",this.getPackageName());
        mountainIDs[1]=getResources().getIdentifier("m2", "drawable",this.getPackageName());
        mountainIDs[2]=getResources().getIdentifier("m3", "drawable",this.getPackageName());
        mountainIDs[3]=getResources().getIdentifier("m4", "drawable",this.getPackageName());
        mountainIDs[4]=getResources().getIdentifier("m5", "drawable",this.getPackageName());
        mountainIDs[5]=getResources().getIdentifier("m6", "drawable",this.getPackageName());
        mountainIDs[6]=getResources().getIdentifier("m7", "drawable",this.getPackageName());
        mountainIDs[7]=getResources().getIdentifier("m8", "drawable",this.getPackageName());

        moneyIDs[0]=getResources().getIdentifier("money1", "drawable",this.getPackageName());
        moneyIDs[1]=getResources().getIdentifier("money2", "drawable",this.getPackageName());
        moneyIDs[2]=getResources().getIdentifier("money3", "drawable",this.getPackageName());
        moneyIDs[3]=getResources().getIdentifier("money4", "drawable",this.getPackageName());
        moneyIDs[4]=getResources().getIdentifier("money5", "drawable",this.getPackageName());
        moneyIDs[5]=getResources().getIdentifier("money6", "drawable",this.getPackageName());
        moneyIDs[6]=getResources().getIdentifier("money7", "drawable",this.getPackageName());
        moneyIDs[7]=getResources().getIdentifier("money8", "drawable",this.getPackageName());

        syntheticIDs[0]=getResources().getIdentifier("part1", "drawable",this.getPackageName());
        syntheticIDs[1]=getResources().getIdentifier("part2", "drawable",this.getPackageName());
        syntheticIDs[2]=getResources().getIdentifier("part3", "drawable",this.getPackageName());
        syntheticIDs[3]=getResources().getIdentifier("part4", "drawable",this.getPackageName());
        syntheticIDs[4]=getResources().getIdentifier("part5", "drawable",this.getPackageName());
        syntheticIDs[5]=getResources().getIdentifier("part6", "drawable",this.getPackageName());
        syntheticIDs[6]=getResources().getIdentifier("part7", "drawable",this.getPackageName());
        syntheticIDs[7]=getResources().getIdentifier("part8", "drawable",this.getPackageName());

    }

    //Chooses a random image
    public void generateRandom(){
        int min = 1;
        int max = 5;

        Random random = new Random();

        randomImage = random.nextInt(max + min) + min;
        staticFixer.setRandomImage(randomImage);
    }



}