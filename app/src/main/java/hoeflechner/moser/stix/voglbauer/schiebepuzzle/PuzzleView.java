package hoeflechner.moser.stix.voglbauer.schiebepuzzle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.GridView;

public class PuzzleView extends GridView {
    private GestureDetector gDetector;
    private boolean mFlingConfirmed = false;
    private float mTouchX;
    private float mTouchY;
    private static MediaPlayer mp;
    private Activity mainActivity;

    private final int SWIPE_MIN_DISTANCE = 0;
    private final int SWIPE_MAX_OFF_PATH = 250;
    private final int SWIPE_THRESHOLD_VELOCITY = 0;

    private StaticFixer staticFixer;

    public PuzzleView(Context context) {
        super(context);
        init(context);
    }

    public PuzzleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PuzzleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) // API 21
    public PuzzleView(Context context, AttributeSet attrs, int defStyleAttr,
                      int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context)
    {
        // Sound Player initialisieren
        mp = MediaPlayer.create(getContext(), R.raw.blop);
        gDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
        {

            @Override
            public boolean onDown(MotionEvent event) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                // Sound abspielen
                mp.setVolume(100,100);
                mp.start();

                final int position = PuzzleView.this.pointToPosition
                        (Math.round(e1.getX()), Math.round(e1.getY()));

                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
                            || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
                        return false;
                    }
                    if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
                        staticFixer.moveTiles(context, "up", position, staticFixer);
                    } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {
                        staticFixer.moveTiles(context, "down", position, staticFixer);
                    }
                } else {
                    if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                        return false;
                    }
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                        staticFixer.moveTiles(context, "left", position, staticFixer);
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                        staticFixer.moveTiles(context, "right", position, staticFixer);
                    }
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        gDetector.onTouchEvent(ev);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mFlingConfirmed = false;
        } else if (action == MotionEvent.ACTION_DOWN) {
            mTouchX = ev.getX();
            mTouchY = ev.getY();
        } else {

            if (mFlingConfirmed) {
                return true;
            }

            float dX = (Math.abs(ev.getX() - mTouchX));
            float dY = (Math.abs(ev.getY() - mTouchY));
            if ((dX > SWIPE_MIN_DISTANCE) || (dY > SWIPE_MIN_DISTANCE)) {
                mFlingConfirmed = true;
                return true;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return gDetector.onTouchEvent(ev);
    }

    //Getter & Setter
    public StaticFixer getStaticFixer() {
        return staticFixer;
    }

    public void setStaticFixer(StaticFixer staticFixer) {
        this.staticFixer = staticFixer;
    }
}