package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.preference.PreferenceManager;
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
    public int counter;
    public static final int FLAG_ACTIVITY_NO_HISTORY = 0;


    private int blackPosition = 0;

    private int columns = 3;
    private int dimensions = columns * columns;

    private String[] tileList;

    private  PuzzleView puzzleView;

    public  int randomImage;
    public  String zeit;

    public int[]flamingoIDs;
    public int[]graffitiIDs;
    public int[]mountainIDs;
    public int[]moneyIDs;
    public int[] mustangIDs;

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

            intent.putExtra("image", randomImage);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        counter=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        // Spielzeit wird als Long abgespeichert und in double umgewandelt
        playTime = sharedPreferences.getLong("playTime", 0);
        System.out.println("PlayTime: " + playTime);

        // Hintergrundmusik
        Intent intent = getIntent();
        music = intent.getExtras().getBoolean(MenuActivity.EXTRA_MESSAGE);

        //Schwierigkeitsgrad wird gesetzt, indem der mitgegebende Wert der DifficultyActivity als "columns" definiert wird
        columns = intent.getExtras().getInt("columns");
        dimensions = columns * columns;

        blackPosition = dimensions-1;

        //Die Tile-Arrays werden in die passende Größe gebracht
        flamingoIDs = new int[dimensions-1];
        graffitiIDs = new int[dimensions-1];
        mountainIDs = new int[dimensions-1];
        moneyIDs = new int[dimensions-1];
        mustangIDs = new int[dimensions-1];

        //Alle Drawable IDs einlesen
        readAllImages();

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

        setdimensions();

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

    private void setdimensions() {

        ViewTreeObserver vto = puzzleView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                puzzleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = puzzleView.getWidth();
                int displayHeight = puzzleView.getHeight();

                int statusbarHeight = getStatusBarHeight(getApplicationContext());
                int requiredHeight = displayHeight - statusbarHeight;

                columnWidth = displayWidth / columns;
                columnHeight = requiredHeight / columns;

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


        int[] tempArray = new int[dimensions-1];

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
                tempArray = mustangIDs.clone();
                break;
            default:
                tempArray = flamingoIDs.clone();
                break;
        }

        if(dimensions==9) {
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
        }
        else if(dimensions==16) {
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
                    button.setBackgroundResource(tempArray[8]);
                }
                else if(tileList[i].equals("9")) {
                    button.setBackgroundResource(tempArray[9]); //Bilder einfügen
                }
                else if(tileList[i].equals("10")){
                    button.setBackgroundResource(tempArray[10]);
                }
                else if(tileList[i].equals("11")){
                    button.setBackgroundResource(tempArray[11]);
                }
                else if(tileList[i].equals("12")){
                    button.setBackgroundResource(tempArray[12]);
                }
                else if(tileList[i].equals("13")){
                    button.setBackgroundResource(tempArray[13]);
                }
                else if(tileList[i].equals("14")){
                    button.setBackgroundResource(tempArray[14]);
                }
                else if(tileList[i].equals("15")){
                    button.setBackgroundResource(R.drawable.black_image);
                }

                buttons.add(button);
            }
        }
        else if(dimensions==25) {
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
                    button.setBackgroundResource(tempArray[8]);
                }
                else if(tileList[i].equals("9")) {
                    button.setBackgroundResource(tempArray[9]); //Bilder einfügen
                }
                else if(tileList[i].equals("10")){
                    button.setBackgroundResource(tempArray[10]);
                }
                else if(tileList[i].equals("11")){
                    button.setBackgroundResource(tempArray[11]);
                }
                else if(tileList[i].equals("12")){
                    button.setBackgroundResource(tempArray[12]);
                }
                else if(tileList[i].equals("13")){
                    button.setBackgroundResource(tempArray[13]);
                }
                else if(tileList[i].equals("14")){
                    button.setBackgroundResource(tempArray[14]);
                }
                else if(tileList[i].equals("15")){
                    button.setBackgroundResource(tempArray[15]);
                }
                else if(tileList[i].equals("16")){
                    button.setBackgroundResource(tempArray[16]);
                }
                else if(tileList[i].equals("17")){
                    button.setBackgroundResource(tempArray[17]);
                }
                else if(tileList[i].equals("18")){
                    button.setBackgroundResource(tempArray[18]);
                }
                else if(tileList[i].equals("19")){
                    button.setBackgroundResource(tempArray[19]);
                }
                else if(tileList[i].equals("20")){
                    button.setBackgroundResource(tempArray[20]);
                }
                else if(tileList[i].equals("21")){
                    button.setBackgroundResource(tempArray[21]);
                }
                else if(tileList[i].equals("22")){
                    button.setBackgroundResource(tempArray[22]);
                }
                else if(tileList[i].equals("23")){
                    button.setBackgroundResource(tempArray[23]);
                }
                else if(tileList[i].equals("24")){
                    button.setBackgroundResource(R.drawable.black_image);
                }

                buttons.add(button);
            }
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
        puzzleView.setNumColumns(columns);
        tileList=new String[dimensions];
        //Arrays.fill(tileList, null);
        for (int i = 0; i < dimensions; i++) {
            tileList[i]=String.valueOf(i);
        }
    }


    private void swap(Context context, int position, int swap, StaticFixer staticFixer){

        //Haptisches Feedback
        puzzleView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

        // Soundeffekt abspielen
        soundEffectPlayer.start();

        //Bewegen der einzelnen Spielfelder
        String newPosition = tileList[position+swap];

        tileList[position+swap]= tileList[position];
        tileList[position]=newPosition;

        display(context);



        //Überprüfung, ob das Puzzle gelöst wurde
        if(isSolved())
        {
            openWinPopUp(context);
        }
    }

    private void openWinPopUp(Context context) {
        // Spielzeit berechnen und speichern
        Long currentPlayTime = (System.currentTimeMillis() - startTime) / 1000;
        String playTimeString = currentPlayTime.toString();
        System.out.println("Spielzeit: " + playTimeString);

        if (currentPlayTime < playTime)
        {
            // Spielzeit speichern
            editor.putLong("playTime", currentPlayTime);
            editor.commit();
        }

        staticFixer.startActivity(randomImage,zeit, Double.toString(playTime), columns);
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

            openWinPopUp(getApplicationContext());
            /*Long tmp=sharedPreferences.getLong("playTime", Double.doubleToRawLongBits(100L));
            playTimeDouble=Double.longBitsToDouble(tmp); */

            //staticFixer.startActivity(randomImage,zeit, Double.toString(playTimeDouble), columns);

        }
        counter++;
        return solved;
    }

    //TODO: 3 unterschiedliche moveTiles für 3 unterschiedliche Schwierigkeiten
    //TODO: ODER 3 unterschiedliche Fälle also if-Verzweigungen machen für 3 unterschiedliche Schwierigkeiten -- eher das hier

    public void moveTiles(Context context, String richtung, int position, StaticFixer staticFixer){

    if(dimensions==9) {

        //1x1
        if(position == 0){
            if(richtung.equals(RIGHT) &&blackPosition==1){
                swap(context, position, 1, staticFixer);
                blackPosition=0;
            }
            else if(richtung.equals(DOWN)&&blackPosition==3){
                swap(context, position, columns, staticFixer);
                blackPosition=0;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }
        else if (position==blackPosition){
            Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
        }

        //1x2
        else if(position==1){
            if(richtung.equals(LEFT) && blackPosition==0){
                swap(context, position, -1, staticFixer);
                blackPosition=1;
            }
            else if(richtung.equals(DOWN)&&blackPosition==4){
                swap(context, position, columns, staticFixer);
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

        //1x3
        else if(position==2){
            if(richtung.equals(LEFT)&&blackPosition==1){
                swap(context, position, -1, staticFixer);
                blackPosition=2;
            }
            else if(richtung.equals(DOWN)&& blackPosition==5){
                swap(context, position, columns, staticFixer);
                blackPosition=2;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //2x1
        else if(position==3){
            if(richtung.equals(UP)&&blackPosition==0){
                swap(context, position, -columns, staticFixer);

                blackPosition=3;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==4){
                swap(context, position, 1, staticFixer);
                blackPosition=3;
            }
            else if(richtung.equals(DOWN)&&blackPosition==6){
                swap(context, position, columns, staticFixer);
                blackPosition=3;

            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //2x2
        else if(position==4){
            if(richtung.equals(UP)&&blackPosition==1){
                swap(context, position, -columns, staticFixer);
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
                swap(context, position, columns, staticFixer);
                blackPosition=4;
            }
        }

        //2x3
        else if(position==5){
            if(richtung.equals(UP)&&blackPosition==2){
                swap(context, position, -columns, staticFixer);
                blackPosition=5;
            }
            else if(richtung.equals(LEFT) &&blackPosition==4){
                swap(context, position, -1, staticFixer);
                blackPosition=5;
            }
            else if(richtung.equals(DOWN)&&blackPosition==8){
                swap(context, position, columns, staticFixer);
                blackPosition=5;

            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //3x1
        else if(position==6){
            if(richtung.equals(UP)&&blackPosition==3){
                swap(context, position, -columns, staticFixer);
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

        //3x2
        else if(position==7){
            if(richtung.equals(UP)&&blackPosition==4){
                swap(context, position, -columns, staticFixer);
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

        //3x3
        else if(position==8){
            if(richtung.equals(UP)&&blackPosition==5){
                swap(context, position, -columns, staticFixer);
                blackPosition=8;
            }
            else if(richtung.equals(LEFT) &&blackPosition==7){
                swap(context, position, -1, staticFixer);
                blackPosition=8;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }


    }
    else if(dimensions==16) {
        //TODO: ------Tiles erste Reihe-----
        //Tiles erste Reihe erste Spalte 1x1
        if(position == 0){
            if(richtung.equals(RIGHT) &&blackPosition==1){
                swap(context, position, 1, staticFixer);
                blackPosition=0;
            }
            else if(richtung.equals(DOWN)&&blackPosition==4){
                swap(context, position, columns, staticFixer);
                blackPosition=0;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }
        else if (position==blackPosition){
            Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
        }

        //Tiles erste Reihe zweite Spalte 1x2
        else if(position == columns -3){
            if(richtung.equals(LEFT) && blackPosition==0){
                swap(context, position, -1, staticFixer);
                blackPosition=1;
            }
            else if(richtung.equals(DOWN)&&blackPosition==5){
                swap(context, position, columns, staticFixer);
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

        //Tiles erste Reihe dritte Spalte 1x3
        else if(position == columns -2){
            if(richtung.equals(LEFT)&&blackPosition==1){
                swap(context, position, -1, staticFixer);
                blackPosition=2;
            }
            else if(richtung.equals(DOWN)&& blackPosition==6){
                swap(context, position, columns, staticFixer);
                blackPosition=2;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==3){
                swap(context, position, 1, staticFixer);
                blackPosition=2;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //TODO: Tiles erste Reihe vierte Spalte 1x4
        else if(position == columns -1){
            if(richtung.equals(LEFT)&&blackPosition==2){
                swap(context, position, -1, staticFixer);
                blackPosition=3;
            }
            else if(richtung.equals(DOWN)&& blackPosition==7){
                swap(context, position, columns, staticFixer);
                blackPosition=3;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }




        //TODO: ------Tiles zweite Reihe-----
        //Tiles zweite Reihe erste Spalte 2x1
        else if(position == 4){
            if(richtung.equals(UP)&&blackPosition==0){
                swap(context, position, -columns, staticFixer);

                blackPosition=4;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==5){
                swap(context, position, 1, staticFixer);
                blackPosition=4;
            }
            else if(richtung.equals(DOWN)&&blackPosition==8){
                swap(context, position, columns, staticFixer);
                blackPosition=4;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles zweite Reihe zweite Spalte 2x2
        else if (position == 5){
            if(richtung.equals(UP)&&blackPosition==1){
                swap(context, position, -columns, staticFixer);
                blackPosition=5;
            }
            else if(richtung.equals(LEFT)&&blackPosition==4){
                swap(context, position, -1, staticFixer);
                blackPosition=5;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==6){
                swap(context, position, 1, staticFixer);
                blackPosition=5;
            }
            else if(richtung.equals(DOWN)&&blackPosition==9) {
                swap(context, position, columns, staticFixer);
                blackPosition=5;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles zweite Reihe dritte Spalte 2x3
        else if (position == 6){
            if(richtung.equals(UP)&&blackPosition==2){
                swap(context, position, -columns, staticFixer);
                blackPosition=6;
            }
            else if(richtung.equals(LEFT)&&blackPosition==5){
                swap(context, position, -1, staticFixer);
                blackPosition=6;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==7){
                swap(context, position, 1, staticFixer);
                blackPosition=6;
            }
            else if(richtung.equals(DOWN)&&blackPosition==10) {
                swap(context, position, columns, staticFixer);
                blackPosition=6;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //TODO: Tiles zweite Reihe vierte Spalte 2x4
        else if(position == 7){
            if(richtung.equals(UP)&&blackPosition==3){
                swap(context, position, -columns, staticFixer);
                blackPosition=7;
            }
            else if(richtung.equals(LEFT) &&blackPosition==6){
                swap(context, position, -1, staticFixer);
                blackPosition=7;
            }
            else if(richtung.equals(DOWN)&&blackPosition==11){
                swap(context, position, columns, staticFixer);
                blackPosition=7;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }




        //TODO: ------Tiles dritte Reihe-----
        //Tiles dritte Reihe erste Spalte 3x1
        else if(position==8){
            if(richtung.equals(UP)&&blackPosition==4){
                swap(context, position, -columns, staticFixer);
                blackPosition=8;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==9){
                swap(context, position, 1, staticFixer);
                blackPosition=8;
            }
            else if(richtung.equals(DOWN)&&blackPosition==12){
                swap(context, position, columns, staticFixer);
                blackPosition=8;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles dritte Reihe zweite Spalte 3x2
        else if(position==9){
            if(richtung.equals(UP)&&blackPosition==5){
                swap(context, position, -columns, staticFixer);
                blackPosition=9;
            }
            else if(richtung.equals(LEFT)&&blackPosition==8){
                swap(context, position, -1, staticFixer);
                blackPosition=9;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==10){
                swap(context, position, 1, staticFixer);
                blackPosition=9;
            }
            else if(richtung.equals(DOWN)&&blackPosition==13) {
                swap(context, position, columns, staticFixer);
                blackPosition=9;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles dritte Reihe dritte Spalte 3x3
        else if(position==10){
            if(richtung.equals(UP)&&blackPosition==6){
                swap(context, position, -columns, staticFixer);
                blackPosition=10;
            }
            else if(richtung.equals(LEFT)&&blackPosition==9){
                swap(context, position, -1, staticFixer);
                blackPosition=10;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==11){
                swap(context, position, 1, staticFixer);
                blackPosition=10;
            }
            else if(richtung.equals(DOWN)&&blackPosition==14) {
                swap(context, position, columns, staticFixer);
                blackPosition=10;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //TODO: Tiles dritte Reihe vierte Spalte 3x4
        else if(position==11){
            if(richtung.equals(UP)&&blackPosition==7){
                swap(context, position, -columns, staticFixer);
                blackPosition=11;
            }
            else if(richtung.equals(LEFT)&&blackPosition==10){
                swap(context, position, -1, staticFixer);
                blackPosition=11;
            }
            else if(richtung.equals(DOWN)&&blackPosition==15) {
                swap(context, position, columns, staticFixer);
                blackPosition=11;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }




        //TODO: ------Tiles vierte Reihe-----
        //Tiles vierte Reihe erste Spalte 4x1
        else if(position==12){
            if(richtung.equals(UP)&&blackPosition==8){
                swap(context, position, -columns, staticFixer);
                blackPosition=12;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==13){
                swap(context, position, 1, staticFixer);
                blackPosition=12;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles vierte Reihe zweite Spalte 4x2
        else if(position==13){
            if(richtung.equals(UP)&&blackPosition==9){
                swap(context, position, -columns, staticFixer);
                blackPosition=13;
            }
            else if(richtung.equals(LEFT)&&blackPosition==12){
                swap(context, position, -1, staticFixer);
                blackPosition=13;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==14){
                swap(context, position, 1, staticFixer);
                blackPosition=13;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles vierte Reihe dritte Spalte 4x3
        else if(position==14){
            if(richtung.equals(UP)&&blackPosition==10){
                swap(context, position, -columns, staticFixer);
                blackPosition=14;
            }
            else if(richtung.equals(LEFT)&&blackPosition==13){
                swap(context, position, -1, staticFixer);
                blackPosition=14;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==15){
                swap(context, position, 1, staticFixer);
                blackPosition=14;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //TODO: Tiles vierte Reihe vierte Spalte 4x4
        else if(position==15){
            if(richtung.equals(UP)&&blackPosition==11){
                swap(context, position, -columns, staticFixer);
                blackPosition=15;
            }
            else if(richtung.equals(LEFT)&&blackPosition==14){
                swap(context, position, -1, staticFixer);
                blackPosition=15;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }
    }
    else if(dimensions==25) {

        //TODO: ------Tiles erste Reihe-----
        //Tiles erste Reihe erste Spalte 1x1
        if(position == 0){
            if(richtung.equals(RIGHT) &&blackPosition==1){
                swap(context, position, 1, staticFixer);
                blackPosition=0;
            }
            else if(richtung.equals(DOWN)&&blackPosition==5){
                swap(context, position, columns, staticFixer);
                blackPosition=0;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }
        else if (position==blackPosition){
            Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
        }

        //Tiles erste Reihe zweite Spalte 1x2
        else if(position==1){
            if(richtung.equals(LEFT) && blackPosition==0){
                swap(context, position, -1, staticFixer);
                blackPosition=1;
            }
            else if(richtung.equals(DOWN)&&blackPosition==6){
                swap(context, position, columns, staticFixer);
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

        //Tiles erste Reihe dritte Spalte 1x3
        else if(position==2){
            if(richtung.equals(LEFT)&&blackPosition==1){
                swap(context, position, -1, staticFixer);
                blackPosition=2;
            }
            else if(richtung.equals(DOWN)&& blackPosition==7){
                swap(context, position, columns, staticFixer);
                blackPosition=2;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==3){
                swap(context, position, 1, staticFixer);
                blackPosition=2;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles erste Reihe vierte Spalte 1x4
        else if(position==3){
            if(richtung.equals(LEFT)&&blackPosition==2){
                swap(context, position, -1, staticFixer);
                blackPosition=3;
            }
            else if(richtung.equals(DOWN)&& blackPosition==8){
                swap(context, position, columns, staticFixer);
                blackPosition=3;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==4){
                swap(context, position, 1, staticFixer);
                blackPosition=3;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles erste Reihe fünfte Spalte 1x5
        else if(position==4){
            if(richtung.equals(LEFT)&&blackPosition==3){
                swap(context, position, -1, staticFixer);
                blackPosition=4;
            }
            else if(richtung.equals(DOWN)&& blackPosition==9){
                swap(context, position, columns, staticFixer);
                blackPosition=4;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }




        //TODO: ------Tiles zweite Reihe-----
        //Tiles zweite Reihe erste Spalte 2x1
        else if(position == 5){
            if(richtung.equals(UP)&&blackPosition==0){
                swap(context, position, -columns, staticFixer);

                blackPosition=5;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==6){
                swap(context, position, 1, staticFixer);
                blackPosition=5;
            }
            else if(richtung.equals(DOWN)&&blackPosition==10){
                swap(context, position, columns, staticFixer);
                blackPosition=5;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles zweite Reihe zweite Spalte 2x2
        else if (position == 6){
            if(richtung.equals(UP)&&blackPosition==1){
                swap(context, position, -columns, staticFixer);
                blackPosition=6;
            }
            else if(richtung.equals(LEFT)&&blackPosition==5){
                swap(context, position, -1, staticFixer);
                blackPosition=6;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==7){
                swap(context, position, 1, staticFixer);
                blackPosition=6;
            }
            else if(richtung.equals(DOWN)&&blackPosition==11) {
                swap(context, position, columns, staticFixer);
                blackPosition=6;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles zweite Reihe dritte Spalte 2x3
        else if (position == 7){
            if(richtung.equals(UP)&&blackPosition==2){
                swap(context, position, -columns, staticFixer);
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
            else if(richtung.equals(DOWN)&&blackPosition==12) {
                swap(context, position, columns, staticFixer);
                blackPosition=7;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles zweite Reihe vierte Spalte 2x4
        else if(position == 8){
            if(richtung.equals(UP)&&blackPosition==3){
                swap(context, position, -columns, staticFixer);
                blackPosition=8;
            }
            else if(richtung.equals(LEFT) &&blackPosition==7){
                swap(context, position, -1, staticFixer);
                blackPosition=8;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==9){
                swap(context, position, 1, staticFixer);
                blackPosition=8;
            }
            else if(richtung.equals(DOWN)&&blackPosition==13){
                swap(context, position, columns, staticFixer);
                blackPosition=8;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles zweite Reihe fünfte Spalte 2x5
        else if(position == 9){
            if(richtung.equals(UP)&&blackPosition==4){
                swap(context, position, -columns, staticFixer);
                blackPosition=9;
            }
            else if(richtung.equals(LEFT) &&blackPosition==8){
                swap(context, position, -1, staticFixer);
                blackPosition=9;
            }
            else if(richtung.equals(DOWN)&&blackPosition==14){
                swap(context, position, columns, staticFixer);
                blackPosition=9;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }




        //TODO: ------Tiles dritte Reihe-----
        //Tiles dritte Reihe erste Spalte 3x1
        else if(position==10){
            if(richtung.equals(UP)&&blackPosition==5){
                swap(context, position, -columns, staticFixer);
                blackPosition=10;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==11){
                swap(context, position, 1, staticFixer);
                blackPosition=10;
            }
            else if(richtung.equals(DOWN)&&blackPosition==15){
                swap(context, position, columns, staticFixer);
                blackPosition=10;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles dritte Reihe zweite Spalte 3x2
        else if(position==11){
            if(richtung.equals(UP)&&blackPosition==6){
                swap(context, position, -columns, staticFixer);
                blackPosition=11;
            }
            else if(richtung.equals(LEFT)&&blackPosition==10){
                swap(context, position, -1, staticFixer);
                blackPosition=11;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==12){
                swap(context, position, 1, staticFixer);
                blackPosition=11;
            }
            else if(richtung.equals(DOWN)&&blackPosition==16) {
                swap(context, position, columns, staticFixer);
                blackPosition=11;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles dritte Reihe dritte Spalte 3x3
        else if(position==12){
            if(richtung.equals(UP)&&blackPosition==7){
                swap(context, position, -columns, staticFixer);
                blackPosition=12;
            }
            else if(richtung.equals(LEFT)&&blackPosition==11){
                swap(context, position, -1, staticFixer);
                blackPosition=12;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==13){
                swap(context, position, 1, staticFixer);
                blackPosition=12;
            }
            else if(richtung.equals(DOWN)&&blackPosition==17) {
                swap(context, position, columns, staticFixer);
                blackPosition=12;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles dritte Reihe vierte Spalte 3x4
        else if(position==13){
            if(richtung.equals(UP)&&blackPosition==8){
                swap(context, position, -columns, staticFixer);
                blackPosition=11;
            }
            else if(richtung.equals(LEFT)&&blackPosition==12){
                swap(context, position, -1, staticFixer);
                blackPosition=11;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==14){
                swap(context, position, 1, staticFixer);
                blackPosition=11;
            }
            else if(richtung.equals(DOWN)&&blackPosition==18) {
                swap(context, position, columns, staticFixer);
                blackPosition=11;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles dritte Reihe fünfte Spalte 3x5
        else if(position==14){
            if(richtung.equals(UP)&&blackPosition==9){
                swap(context, position, -columns, staticFixer);
                blackPosition=14;
            }
            else if(richtung.equals(LEFT)&&blackPosition==13){
                swap(context, position, -1, staticFixer);
                blackPosition=14;
            }
            else if(richtung.equals(DOWN)&&blackPosition==19) {
                swap(context, position, columns, staticFixer);
                blackPosition=14;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }




        //TODO: ------Tiles vierte Reihe-----
        //Tiles vierte Reihe erste Spalte 4x1
        else if(position==15){
            if(richtung.equals(UP)&&blackPosition==10){
                swap(context, position, -columns, staticFixer);
                blackPosition=15;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==16){
                swap(context, position, 1, staticFixer);
                blackPosition=15;
            }
            else if(richtung.equals(DOWN)&&blackPosition==20) {
                swap(context, position, columns, staticFixer);
                blackPosition=15;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles vierte Reihe zweite Spalte 4x2
        else if(position==16){
            if(richtung.equals(UP)&&blackPosition==11){
                swap(context, position, -columns, staticFixer);
                blackPosition=16;
            }
            else if(richtung.equals(LEFT)&&blackPosition==15){
                swap(context, position, -1, staticFixer);
                blackPosition=16;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==17){
                swap(context, position, 1, staticFixer);
                blackPosition=16;
            }
            else if(richtung.equals(DOWN)&&blackPosition==21) {
                swap(context, position, columns, staticFixer);
                blackPosition=16;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles vierte Reihe dritte Spalte 4x3
        else if(position==17){
            if(richtung.equals(UP)&&blackPosition==12){
                swap(context, position, -columns, staticFixer);
                blackPosition=17;
            }
            else if(richtung.equals(LEFT)&&blackPosition==16){
                swap(context, position, -1, staticFixer);
                blackPosition=17;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==18){
                swap(context, position, 1, staticFixer);
                blackPosition=17;
            }
            else if(richtung.equals(DOWN)&&blackPosition==22) {
                swap(context, position, columns, staticFixer);
                blackPosition=17;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles vierte Reihe vierte Spalte 4x4
        else if(position==18){
            if(richtung.equals(UP)&&blackPosition==13){
                swap(context, position, -columns, staticFixer);
                blackPosition=18;
            }
            else if(richtung.equals(LEFT)&&blackPosition==17){
                swap(context, position, -1, staticFixer);
                blackPosition=18;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==19){
                swap(context, position, 1, staticFixer);
                blackPosition=18;
            }
            else if(richtung.equals(DOWN)&&blackPosition==23) {
                swap(context, position, columns, staticFixer);
                blackPosition=18;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles vierte Reihe fünfte Spalte 4x5
        else if(position==19){
            if(richtung.equals(UP)&&blackPosition==14){
                swap(context, position, -columns, staticFixer);
                blackPosition=19;
            }
            else if(richtung.equals(LEFT)&&blackPosition==18){
                swap(context, position, -1, staticFixer);
                blackPosition=19;
            }
            else if(richtung.equals(DOWN)&&blackPosition==24) {
                swap(context, position, columns, staticFixer);
                blackPosition=19;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }




        //TODO: ------Tiles fünfte Reihe-----
        //Tiles fünfte Reihe erste Spalte 5x1
        else if(position==20){
            if(richtung.equals(UP)&&blackPosition==15){
                swap(context, position, -columns, staticFixer);
                blackPosition=20;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==21){
                swap(context, position, 1, staticFixer);
                blackPosition=20;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles fünfte Reihe zweite Spalte 5x2
        else if(position==21){
            if(richtung.equals(UP)&&blackPosition==16){
                swap(context, position, -columns, staticFixer);
                blackPosition=21;
            }
            else if(richtung.equals(LEFT)&&blackPosition==20){
                swap(context, position, -1, staticFixer);
                blackPosition=21;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==22){
                swap(context, position, 1, staticFixer);
                blackPosition=21;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles fünfte Reihe dritte Spalte 5x3
        else if(position==22){
            if(richtung.equals(UP)&&blackPosition==17){
                swap(context, position, -columns, staticFixer);
                blackPosition=22;
            }
            else if(richtung.equals(LEFT)&&blackPosition==21){
                swap(context, position, -1, staticFixer);
                blackPosition=22;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==23){
                swap(context, position, 1, staticFixer);
                blackPosition=22;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles fünfte Reihe vierte Spalte 5x4
        else if(position==23){
            if(richtung.equals(UP)&&blackPosition==18){
                swap(context, position, -columns, staticFixer);
                blackPosition=23;
            }
            else if(richtung.equals(LEFT)&&blackPosition==22){
                swap(context, position, -1, staticFixer);
                blackPosition=23;
            }
            else if(richtung.equals(RIGHT)&&blackPosition==24){
                swap(context, position, 1, staticFixer);
                blackPosition=23;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }

        //Tiles fünfte Reihe fünfte Spalte 5x5
        else if(position==24){
            if(richtung.equals(UP)&&blackPosition==19){
                swap(context, position, -columns, staticFixer);
                blackPosition=24;
            }
            else if(richtung.equals(LEFT)&&blackPosition==23){
                swap(context, position, -1, staticFixer);
                blackPosition=24;
            }
            else {
                Toast.makeText(context, "Bewegung ungültig", Toast.LENGTH_SHORT);
            }
        }
    }

    }

    public void pauseGame()
    {
        Intent intent = new Intent(this, PauseMenuActivity.class);
        intent.putExtra("columns", columns);
        startActivity(intent);
    }

    //TODO: 3 unterschiedliche readAllImages für 3 unterschiedliche Schwierigkeiten
    public void readAllImages(){

        if(dimensions==9){
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

            mustangIDs[0]=getResources().getIdentifier("part1", "drawable",this.getPackageName());
            mustangIDs[1]=getResources().getIdentifier("part2", "drawable",this.getPackageName());
            mustangIDs[2]=getResources().getIdentifier("part3", "drawable",this.getPackageName());
            mustangIDs[3]=getResources().getIdentifier("part4", "drawable",this.getPackageName());
            mustangIDs[4]=getResources().getIdentifier("part5", "drawable",this.getPackageName());
            mustangIDs[5]=getResources().getIdentifier("part6", "drawable",this.getPackageName());
            mustangIDs[6]=getResources().getIdentifier("part7", "drawable",this.getPackageName());
            mustangIDs[7]=getResources().getIdentifier("part8", "drawable",this.getPackageName());
        }
        else if(dimensions==16){
            flamingoIDs[0]=getResources().getIdentifier("flamingo_4x4_01", "drawable",this.getPackageName());
            flamingoIDs[1]=getResources().getIdentifier("flamingo_4x4_02", "drawable",this.getPackageName());
            flamingoIDs[2]=getResources().getIdentifier("flamingo_4x4_03", "drawable",this.getPackageName());
            flamingoIDs[3]=getResources().getIdentifier("flamingo_4x4_04", "drawable",this.getPackageName());
            flamingoIDs[4]=getResources().getIdentifier("flamingo_4x4_05", "drawable",this.getPackageName());
            flamingoIDs[5]=getResources().getIdentifier("flamingo_4x4_06", "drawable",this.getPackageName());
            flamingoIDs[6]=getResources().getIdentifier("flamingo_4x4_07", "drawable",this.getPackageName());
            flamingoIDs[7]=getResources().getIdentifier("flamingo_4x4_08", "drawable",this.getPackageName());
            flamingoIDs[8]=getResources().getIdentifier("flamingo_4x4_09", "drawable",this.getPackageName());
            flamingoIDs[9]=getResources().getIdentifier("flamingo_4x4_10", "drawable",this.getPackageName());
            flamingoIDs[10]=getResources().getIdentifier("flamingo_4x4_11", "drawable",this.getPackageName());
            flamingoIDs[11]=getResources().getIdentifier("flamingo_4x4_12", "drawable",this.getPackageName());
            flamingoIDs[12]=getResources().getIdentifier("flamingo_4x4_13", "drawable",this.getPackageName());
            flamingoIDs[13]=getResources().getIdentifier("flamingo_4x4_14", "drawable",this.getPackageName());
            flamingoIDs[14]=getResources().getIdentifier("flamingo_4x4_15", "drawable",this.getPackageName());

            graffitiIDs[0]=getResources().getIdentifier("grafitti_4x4_01", "drawable",this.getPackageName());
            graffitiIDs[1]=getResources().getIdentifier("grafitti_4x4_02", "drawable",this.getPackageName());
            graffitiIDs[2]=getResources().getIdentifier("grafitti_4x4_03", "drawable",this.getPackageName());
            graffitiIDs[3]=getResources().getIdentifier("grafitti_4x4_04", "drawable",this.getPackageName());
            graffitiIDs[4]=getResources().getIdentifier("grafitti_4x4_05", "drawable",this.getPackageName());
            graffitiIDs[5]=getResources().getIdentifier("grafitti_4x4_06", "drawable",this.getPackageName());
            graffitiIDs[6]=getResources().getIdentifier("grafitti_4x4_07", "drawable",this.getPackageName());
            graffitiIDs[7]=getResources().getIdentifier("grafitti_4x4_08", "drawable",this.getPackageName());
            graffitiIDs[8]=getResources().getIdentifier("grafitti_4x4_09", "drawable",this.getPackageName());
            graffitiIDs[9]=getResources().getIdentifier("grafitti_4x4_10", "drawable",this.getPackageName());
            graffitiIDs[10]=getResources().getIdentifier("grafitti_4x4_11", "drawable",this.getPackageName());
            graffitiIDs[11]=getResources().getIdentifier("grafitti_4x4_12", "drawable",this.getPackageName());
            graffitiIDs[12]=getResources().getIdentifier("grafitti_4x4_13", "drawable",this.getPackageName());
            graffitiIDs[13]=getResources().getIdentifier("grafitti_4x4_14", "drawable",this.getPackageName());
            graffitiIDs[14]=getResources().getIdentifier("grafitti_4x4_15", "drawable",this.getPackageName());

            mountainIDs[0]=getResources().getIdentifier("mountain_4x4_01", "drawable",this.getPackageName());
            mountainIDs[1]=getResources().getIdentifier("mountain_4x4_02", "drawable",this.getPackageName());
            mountainIDs[2]=getResources().getIdentifier("mountain_4x4_03", "drawable",this.getPackageName());
            mountainIDs[3]=getResources().getIdentifier("mountain_4x4_04", "drawable",this.getPackageName());
            mountainIDs[4]=getResources().getIdentifier("mountain_4x4_05", "drawable",this.getPackageName());
            mountainIDs[5]=getResources().getIdentifier("mountain_4x4_06", "drawable",this.getPackageName());
            mountainIDs[6]=getResources().getIdentifier("mountain_4x4_07", "drawable",this.getPackageName());
            mountainIDs[7]=getResources().getIdentifier("mountain_4x4_08", "drawable",this.getPackageName());
            mountainIDs[8]=getResources().getIdentifier("mountain_4x4_09", "drawable",this.getPackageName());
            mountainIDs[9]=getResources().getIdentifier("mountain_4x4_10", "drawable",this.getPackageName());
            mountainIDs[10]=getResources().getIdentifier("mountain_4x4_11", "drawable",this.getPackageName());
            mountainIDs[11]=getResources().getIdentifier("mountain_4x4_12", "drawable",this.getPackageName());
            mountainIDs[12]=getResources().getIdentifier("mountain_4x4_13", "drawable",this.getPackageName());
            mountainIDs[13]=getResources().getIdentifier("mountain_4x4_14", "drawable",this.getPackageName());
            mountainIDs[14]=getResources().getIdentifier("mountain_4x4_15", "drawable",this.getPackageName());

            moneyIDs[0]=getResources().getIdentifier("money_4x4_01", "drawable",this.getPackageName());
            moneyIDs[1]=getResources().getIdentifier("money_4x4_02", "drawable",this.getPackageName());
            moneyIDs[2]=getResources().getIdentifier("money_4x4_03", "drawable",this.getPackageName());
            moneyIDs[3]=getResources().getIdentifier("money_4x4_04", "drawable",this.getPackageName());
            moneyIDs[4]=getResources().getIdentifier("money_4x4_05", "drawable",this.getPackageName());
            moneyIDs[5]=getResources().getIdentifier("money_4x4_06", "drawable",this.getPackageName());
            moneyIDs[6]=getResources().getIdentifier("money_4x4_07", "drawable",this.getPackageName());
            moneyIDs[7]=getResources().getIdentifier("money_4x4_08", "drawable",this.getPackageName());
            moneyIDs[8]=getResources().getIdentifier("money_4x4_09", "drawable",this.getPackageName());
            moneyIDs[9]=getResources().getIdentifier("money_4x4_10", "drawable",this.getPackageName());
            moneyIDs[10]=getResources().getIdentifier("money_4x4_11", "drawable",this.getPackageName());
            moneyIDs[11]=getResources().getIdentifier("money_4x4_12", "drawable",this.getPackageName());
            moneyIDs[12]=getResources().getIdentifier("money_4x4_13", "drawable",this.getPackageName());
            moneyIDs[13]=getResources().getIdentifier("money_4x4_14", "drawable",this.getPackageName());
            moneyIDs[14]=getResources().getIdentifier("money_4x4_15", "drawable",this.getPackageName());

            mustangIDs[0]=getResources().getIdentifier("gt3502_4x4_01", "drawable",this.getPackageName());
            mustangIDs[1]=getResources().getIdentifier("gt3502_4x4_02", "drawable",this.getPackageName());
            mustangIDs[2]=getResources().getIdentifier("gt3502_4x4_03", "drawable",this.getPackageName());
            mustangIDs[3]=getResources().getIdentifier("gt3502_4x4_04", "drawable",this.getPackageName());
            mustangIDs[4]=getResources().getIdentifier("gt3502_4x4_05", "drawable",this.getPackageName());
            mustangIDs[5]=getResources().getIdentifier("gt3502_4x4_06", "drawable",this.getPackageName());
            mustangIDs[6]=getResources().getIdentifier("gt3502_4x4_07", "drawable",this.getPackageName());
            mustangIDs[7]=getResources().getIdentifier("gt3502_4x4_08", "drawable",this.getPackageName());
            mustangIDs[8]=getResources().getIdentifier("gt3502_4x4_09", "drawable",this.getPackageName());
            mustangIDs[9]=getResources().getIdentifier("gt3502_4x4_10", "drawable",this.getPackageName());
            mustangIDs[10]=getResources().getIdentifier("gt3502_4x4_11", "drawable",this.getPackageName());
            mustangIDs[11]=getResources().getIdentifier("gt3502_4x4_12", "drawable",this.getPackageName());
            mustangIDs[12]=getResources().getIdentifier("gt3502_4x4_13", "drawable",this.getPackageName());
            mustangIDs[13]=getResources().getIdentifier("gt3502_4x4_14", "drawable",this.getPackageName());
            mustangIDs[14]=getResources().getIdentifier("gt3502_4x4_15", "drawable",this.getPackageName());
        }
        else if(dimensions==25){

            flamingoIDs[0]=getResources().getIdentifier("flamingo_5x5_01", "drawable",this.getPackageName());
            flamingoIDs[1]=getResources().getIdentifier("flamingo_5x5_02", "drawable",this.getPackageName());
            flamingoIDs[2]=getResources().getIdentifier("flamingo_5x5_03", "drawable",this.getPackageName());
            flamingoIDs[3]=getResources().getIdentifier("flamingo_5x5_04", "drawable",this.getPackageName());
            flamingoIDs[4]=getResources().getIdentifier("flamingo_5x5_05", "drawable",this.getPackageName());
            flamingoIDs[5]=getResources().getIdentifier("flamingo_5x5_06", "drawable",this.getPackageName());
            flamingoIDs[6]=getResources().getIdentifier("flamingo_5x5_07", "drawable",this.getPackageName());
            flamingoIDs[7]=getResources().getIdentifier("flamingo_5x5_08", "drawable",this.getPackageName());
            flamingoIDs[8]=getResources().getIdentifier("flamingo_5x5_09", "drawable",this.getPackageName());
            flamingoIDs[9]=getResources().getIdentifier("flamingo_5x5_10", "drawable",this.getPackageName());
            flamingoIDs[10]=getResources().getIdentifier("flamingo_5x5_11", "drawable",this.getPackageName());
            flamingoIDs[11]=getResources().getIdentifier("flamingo_5x5_12", "drawable",this.getPackageName());
            flamingoIDs[12]=getResources().getIdentifier("flamingo_5x5_13", "drawable",this.getPackageName());
            flamingoIDs[13]=getResources().getIdentifier("flamingo_5x5_14", "drawable",this.getPackageName());
            flamingoIDs[14]=getResources().getIdentifier("flamingo_5x5_15", "drawable",this.getPackageName());
            flamingoIDs[15]=getResources().getIdentifier("flamingo_5x5_16", "drawable",this.getPackageName());
            flamingoIDs[16]=getResources().getIdentifier("flamingo_5x5_17", "drawable",this.getPackageName());
            flamingoIDs[17]=getResources().getIdentifier("flamingo_5x5_18", "drawable",this.getPackageName());
            flamingoIDs[18]=getResources().getIdentifier("flamingo_5x5_19", "drawable",this.getPackageName());
            flamingoIDs[19]=getResources().getIdentifier("flamingo_5x5_20", "drawable",this.getPackageName());
            flamingoIDs[20]=getResources().getIdentifier("flamingo_5x5_21", "drawable",this.getPackageName());
            flamingoIDs[21]=getResources().getIdentifier("flamingo_5x5_22", "drawable",this.getPackageName());
            flamingoIDs[22]=getResources().getIdentifier("flamingo_5x5_23", "drawable",this.getPackageName());
            flamingoIDs[23]=getResources().getIdentifier("flamingo_5x5_24", "drawable",this.getPackageName());


            graffitiIDs[0]=getResources().getIdentifier("grafitti_5x5_01", "drawable",this.getPackageName());
            graffitiIDs[1]=getResources().getIdentifier("grafitti_5x5_02", "drawable",this.getPackageName());
            graffitiIDs[2]=getResources().getIdentifier("grafitti_5x5_03", "drawable",this.getPackageName());
            graffitiIDs[3]=getResources().getIdentifier("grafitti_5x5_04", "drawable",this.getPackageName());
            graffitiIDs[4]=getResources().getIdentifier("grafitti_5x5_05", "drawable",this.getPackageName());
            graffitiIDs[5]=getResources().getIdentifier("grafitti_5x5_06", "drawable",this.getPackageName());
            graffitiIDs[6]=getResources().getIdentifier("grafitti_5x5_07", "drawable",this.getPackageName());
            graffitiIDs[7]=getResources().getIdentifier("grafitti_5x5_08", "drawable",this.getPackageName());
            graffitiIDs[8]=getResources().getIdentifier("grafitti_5x5_09", "drawable",this.getPackageName());
            graffitiIDs[9]=getResources().getIdentifier("grafitti_5x5_10", "drawable",this.getPackageName());
            graffitiIDs[10]=getResources().getIdentifier("grafitti_5x5_11", "drawable",this.getPackageName());
            graffitiIDs[11]=getResources().getIdentifier("grafitti_5x5_12", "drawable",this.getPackageName());
            graffitiIDs[12]=getResources().getIdentifier("grafitti_5x5_13", "drawable",this.getPackageName());
            graffitiIDs[13]=getResources().getIdentifier("grafitti_5x5_14", "drawable",this.getPackageName());
            graffitiIDs[14]=getResources().getIdentifier("grafitti_5x5_15", "drawable",this.getPackageName());
            graffitiIDs[15]=getResources().getIdentifier("grafitti_5x5_16", "drawable",this.getPackageName());
            graffitiIDs[16]=getResources().getIdentifier("grafitti_5x5_17", "drawable",this.getPackageName());
            graffitiIDs[17]=getResources().getIdentifier("grafitti_5x5_18", "drawable",this.getPackageName());
            graffitiIDs[18]=getResources().getIdentifier("grafitti_5x5_19", "drawable",this.getPackageName());
            graffitiIDs[19]=getResources().getIdentifier("grafitti_5x5_20", "drawable",this.getPackageName());
            graffitiIDs[20]=getResources().getIdentifier("grafitti_5x5_21", "drawable",this.getPackageName());
            graffitiIDs[21]=getResources().getIdentifier("grafitti_5x5_22", "drawable",this.getPackageName());
            graffitiIDs[22]=getResources().getIdentifier("grafitti_5x5_23", "drawable",this.getPackageName());
            graffitiIDs[23]=getResources().getIdentifier("grafitti_5x5_24", "drawable",this.getPackageName());

            mountainIDs[0]=getResources().getIdentifier("mountain_5x5_01", "drawable",this.getPackageName());
            mountainIDs[1]=getResources().getIdentifier("mountain_5x5_02", "drawable",this.getPackageName());
            mountainIDs[2]=getResources().getIdentifier("mountain_5x5_03", "drawable",this.getPackageName());
            mountainIDs[3]=getResources().getIdentifier("mountain_5x5_04", "drawable",this.getPackageName());
            mountainIDs[4]=getResources().getIdentifier("mountain_5x5_05", "drawable",this.getPackageName());
            mountainIDs[5]=getResources().getIdentifier("mountain_5x5_06", "drawable",this.getPackageName());
            mountainIDs[6]=getResources().getIdentifier("mountain_5x5_07", "drawable",this.getPackageName());
            mountainIDs[7]=getResources().getIdentifier("mountain_5x5_08", "drawable",this.getPackageName());
            mountainIDs[8]=getResources().getIdentifier("mountain_5x5_09", "drawable",this.getPackageName());
            mountainIDs[9]=getResources().getIdentifier("mountain_5x5_10", "drawable",this.getPackageName());
            mountainIDs[10]=getResources().getIdentifier("mountain_5x5_11", "drawable",this.getPackageName());
            mountainIDs[11]=getResources().getIdentifier("mountain_5x5_12", "drawable",this.getPackageName());
            mountainIDs[12]=getResources().getIdentifier("mountain_5x5_13", "drawable",this.getPackageName());
            mountainIDs[13]=getResources().getIdentifier("mountain_5x5_14", "drawable",this.getPackageName());
            mountainIDs[14]=getResources().getIdentifier("mountain_5x5_15", "drawable",this.getPackageName());
            mountainIDs[15]=getResources().getIdentifier("mountain_5x5_16", "drawable",this.getPackageName());
            mountainIDs[16]=getResources().getIdentifier("mountain_5x5_17", "drawable",this.getPackageName());
            mountainIDs[17]=getResources().getIdentifier("mountain_5x5_18", "drawable",this.getPackageName());
            mountainIDs[18]=getResources().getIdentifier("mountain_5x5_19", "drawable",this.getPackageName());
            mountainIDs[19]=getResources().getIdentifier("mountain_5x5_20", "drawable",this.getPackageName());
            mountainIDs[20]=getResources().getIdentifier("mountain_5x5_21", "drawable",this.getPackageName());
            mountainIDs[21]=getResources().getIdentifier("mountain_5x5_22", "drawable",this.getPackageName());
            mountainIDs[22]=getResources().getIdentifier("mountain_5x5_23", "drawable",this.getPackageName());
            mountainIDs[23]=getResources().getIdentifier("mountain_5x5_24", "drawable",this.getPackageName());

            moneyIDs[0]=getResources().getIdentifier("money_5x5_01", "drawable",this.getPackageName());
            moneyIDs[1]=getResources().getIdentifier("money_5x5_02", "drawable",this.getPackageName());
            moneyIDs[2]=getResources().getIdentifier("money_5x5_03", "drawable",this.getPackageName());
            moneyIDs[3]=getResources().getIdentifier("money_5x5_04", "drawable",this.getPackageName());
            moneyIDs[4]=getResources().getIdentifier("money_5x5_05", "drawable",this.getPackageName());
            moneyIDs[5]=getResources().getIdentifier("money_5x5_06", "drawable",this.getPackageName());
            moneyIDs[6]=getResources().getIdentifier("money_5x5_07", "drawable",this.getPackageName());
            moneyIDs[7]=getResources().getIdentifier("money_5x5_08", "drawable",this.getPackageName());
            moneyIDs[8]=getResources().getIdentifier("money_5x5_09", "drawable",this.getPackageName());
            moneyIDs[9]=getResources().getIdentifier("money_5x5_10", "drawable",this.getPackageName());
            moneyIDs[10]=getResources().getIdentifier("money_5x5_11", "drawable",this.getPackageName());
            moneyIDs[11]=getResources().getIdentifier("money_5x5_12", "drawable",this.getPackageName());
            moneyIDs[12]=getResources().getIdentifier("money_5x5_13", "drawable",this.getPackageName());
            moneyIDs[13]=getResources().getIdentifier("money_5x5_14", "drawable",this.getPackageName());
            moneyIDs[14]=getResources().getIdentifier("money_5x5_15", "drawable",this.getPackageName());
            moneyIDs[15]=getResources().getIdentifier("money_5x5_16", "drawable",this.getPackageName());
            moneyIDs[16]=getResources().getIdentifier("money_5x5_17", "drawable",this.getPackageName());
            moneyIDs[17]=getResources().getIdentifier("money_5x5_18", "drawable",this.getPackageName());
            moneyIDs[18]=getResources().getIdentifier("money_5x5_19", "drawable",this.getPackageName());
            moneyIDs[19]=getResources().getIdentifier("money_5x5_20", "drawable",this.getPackageName());
            moneyIDs[20]=getResources().getIdentifier("money_5x5_21", "drawable",this.getPackageName());
            moneyIDs[21]=getResources().getIdentifier("money_5x5_22", "drawable",this.getPackageName());
            moneyIDs[22]=getResources().getIdentifier("money_5x5_23", "drawable",this.getPackageName());
            moneyIDs[23]=getResources().getIdentifier("money_5x5_24", "drawable",this.getPackageName());

            mustangIDs[0]=getResources().getIdentifier("gt3502_5x5_01", "drawable",this.getPackageName());
            mustangIDs[1]=getResources().getIdentifier("gt3502_5x5_02", "drawable",this.getPackageName());
            mustangIDs[2]=getResources().getIdentifier("gt3502_5x5_03", "drawable",this.getPackageName());
            mustangIDs[3]=getResources().getIdentifier("gt3502_5x5_04", "drawable",this.getPackageName());
            mustangIDs[4]=getResources().getIdentifier("gt3502_5x5_05", "drawable",this.getPackageName());
            mustangIDs[5]=getResources().getIdentifier("gt3502_5x5_06", "drawable",this.getPackageName());
            mustangIDs[6]=getResources().getIdentifier("gt3502_5x5_07", "drawable",this.getPackageName());
            mustangIDs[7]=getResources().getIdentifier("gt3502_5x5_08", "drawable",this.getPackageName());
            mustangIDs[8]=getResources().getIdentifier("gt3502_5x5_09", "drawable",this.getPackageName());
            mustangIDs[9]=getResources().getIdentifier("gt3502_5x5_10", "drawable",this.getPackageName());
            mustangIDs[10]=getResources().getIdentifier("gt3502_5x5_11", "drawable",this.getPackageName());
            mustangIDs[11]=getResources().getIdentifier("gt3502_5x5_12", "drawable",this.getPackageName());
            mustangIDs[12]=getResources().getIdentifier("gt3502_5x5_13", "drawable",this.getPackageName());
            mustangIDs[13]=getResources().getIdentifier("gt3502_5x5_14", "drawable",this.getPackageName());
            mustangIDs[14]=getResources().getIdentifier("gt3502_5x5_15", "drawable",this.getPackageName());
            mustangIDs[15]=getResources().getIdentifier("gt3502_5x5_16", "drawable",this.getPackageName());
            mustangIDs[16]=getResources().getIdentifier("gt3502_5x5_17", "drawable",this.getPackageName());
            mustangIDs[17]=getResources().getIdentifier("gt3502_5x5_18", "drawable",this.getPackageName());
            mustangIDs[18]=getResources().getIdentifier("gt3502_5x5_19", "drawable",this.getPackageName());
            mustangIDs[19]=getResources().getIdentifier("gt3502_5x5_20", "drawable",this.getPackageName());
            mustangIDs[20]=getResources().getIdentifier("gt3502_5x5_21", "drawable",this.getPackageName());
            mustangIDs[21]=getResources().getIdentifier("gt3502_5x5_22", "drawable",this.getPackageName());
            mustangIDs[22]=getResources().getIdentifier("gt3502_5x5_23", "drawable",this.getPackageName());
            mustangIDs[23]=getResources().getIdentifier("gt3502_5x5_24", "drawable",this.getPackageName());
        }
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