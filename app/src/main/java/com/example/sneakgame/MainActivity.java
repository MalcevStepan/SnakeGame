package com.example.sneakgame;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    DrawSneak drawSneak;
    static Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        display = getWindowManager().getDefaultDisplay();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        drawSneak = new DrawSneak(this);
        drawSneak.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(drawSneak);
    }
}

class DrawSneak extends View {
    static int width = MainActivity.display.getWidth();
    static int height = MainActivity.display.getHeight();
    Paint paint = new Paint();
    final int cellDim = width / 30;
    String start = "Start";
    //using the text size
    float startX = (float) width / 2 - (float) width / 17;
    float startY = (float) height / 2 + (float) height / 31;
    float x1 = 0;//onTouchEvent
    float y1 = 0;//
    int sneakVelocityX = 0;
    int sneakVelocityY = 0;
    int countOfCellsY = height / cellDim;
    int countOfCellsX = width / cellDim;
    int sneakHeadX = cellDim * countOfCellsX / 2;
    int sneakHeadY = cellDim * countOfCellsY / 2;
    Random random = new Random();
    int appleX = random.nextInt(countOfCellsX) * cellDim;
    int appleY = random.nextInt(countOfCellsY) * cellDim;
    Rect apple = new Rect(appleX, appleY, appleX + cellDim, appleY + cellDim);
    boolean alive = false;
    ArrayList<Sneak> sneakCells = new ArrayList<>();

    public DrawSneak(Context context) {
        super(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sneakCells.add(new Sneak(sneakHeadX, sneakHeadY));
                    while (true) {
                        Thread.sleep(450);
                        if (alive) {
                            update();
                        }
                        post(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent m) {
                if (!alive && m.getX() >= startX && m.getX() <= startX + 2 * width / 17 && m.getY() <= startY && m.getY() >= startY - 2 * height / 31) {
                    sneakVelocityX = cellDim;
                    alive = true;
                }
                if (alive) {
                    if (m.getAction() == MotionEvent.ACTION_DOWN) {
                        x1 = m.getX();
                        y1 = m.getY();
                    }
                    if (m.getAction() == MotionEvent.ACTION_UP && (m.getX() - x1 != 0 || m.getY() - y1 != 0)) {
                        float[] v = new float[2];
                        v[0] = m.getX() - x1;
                        v[1] = m.getY() - y1;
                        if (Math.abs(v[0]) > Math.abs(v[1])) {
                            sneakVelocityX = cellDim * ((int) v[0] / (int) Math.abs(v[0]));
                            sneakVelocityY = 0;
                        } else {
                            sneakVelocityY = cellDim * ((int) v[1] / (int) Math.abs(v[1]));
                            sneakVelocityX = 0;
                        }
                    }
                }

                return true;
            }
        });
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        if (!alive) canvas.drawText(start, startX, startY, paint);
        if (alive) {
            Iterator<Sneak> iterator = sneakCells.iterator();
            while (iterator.hasNext()) {
                Sneak sneak = iterator.next();
                Rect r=new Rect(sneak.sneakX, sneak.sneakY, sneak.sneakX + cellDim, sneak.sneakY + cellDim);
                canvas.drawRect(r, paint);
                r=null;
                System.gc();
            }
            paint.setColor(Color.RED);
            canvas.drawRect(apple, paint);
            canvas.drawText(String.valueOf(sneakCells.size()), 50, 50, paint);
        }
    }

    void update() {

    }
}

class Sneak {
    int sneakX, sneakY;

    Sneak(int sneakX, int sneakY) {
        this.sneakX = sneakX;
        this.sneakY = sneakY;
    }
}