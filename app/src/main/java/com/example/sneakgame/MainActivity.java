package com.example.sneakgame;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {
    DrawSneak drawSneak;
    static Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        display = getWindowManager().getDefaultDisplay();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        drawSneak = new DrawSneak(this);
        drawSneak.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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
    int sneakHeadX = cellDim *countOfCellsX / 2;
    int sneakHeadY = cellDim * countOfCellsY / 2;
    Random random = new Random();
    int appleX = random.nextInt(countOfCellsX) * cellDim;
    int appleY = random.nextInt(countOfCellsY) * cellDim;
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
                        Thread.sleep(20);
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
                if (!alive) {
                    sneakVelocityX = cellDim;
                    alive = true;
                } else {
                    if (m.getAction() == MotionEvent.ACTION_DOWN) {
                        x1 = m.getX();
                        y1 = m.getY();
                    }
                    if (m.getAction() == MotionEvent.ACTION_UP) {
                        float[] v = new float[2];
                        v[0] = m.getX() - x1;
                        v[1] = m.getY() - y1;
                        if (Math.abs(v[0]) > Math.abs(v[1])) {
                            if (v[0] != 0 && sneakVelocityX == 0) {
                                sneakVelocityX = cellDim * (int) (v[0] / Math.abs(v[0]));
                                sneakVelocityY = 0;
                            }
                        } else {
                            if (v[1] != 0 && sneakVelocityY == 0) {
                                sneakVelocityY = cellDim * (int) (v[1] / Math.abs(v[1]));
                                sneakVelocityX = 0;
                            }
                        }
                    }
                }

                return true;
            }
        });
    }

    int r = 0;

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(sneakHeadY),100,600,paint);
        canvas.drawText(String.valueOf(sneakHeadX),100,600,paint);
        if (!alive) canvas.drawText(start, startX, startY, paint);
        else {
            if (++r >= 10) {
                sneakCells.add(0, new Sneak((sneakCells.get(0).sneakX + sneakVelocityX) < 0 ? (sneakCells.get(0).sneakX + sneakVelocityX) + countOfCellsX * cellDim : (sneakCells.get(0).sneakX + sneakVelocityX) > canvas.getWidth() ? (sneakCells.get(0).sneakX + sneakVelocityX) - countOfCellsX * cellDim : (sneakCells.get(0).sneakX + sneakVelocityX), (sneakCells.get(0).sneakY + sneakVelocityY) < 0 ? (sneakCells.get(0).sneakY + sneakVelocityY) + countOfCellsY * cellDim : (sneakCells.get(0).sneakY + sneakVelocityY) > canvas.getHeight() ? (sneakCells.get(0).sneakY + sneakVelocityY) - countOfCellsY * cellDim : (sneakCells.get(0).sneakY + sneakVelocityY)));
                if (sneakCells.get(0).sneakX != appleX || sneakCells.get(0).sneakY != appleY)
                    sneakCells.remove(sneakCells.size() - 1);
                else {
                    appleX = random.nextInt(countOfCellsX) * cellDim;
                    appleY = random.nextInt(countOfCellsY) * cellDim;
                }
                r -= 10;
            }

            for (int i = 0; i < (sneakCells.size() - 1 > 0 ? sneakCells.size() - 1 : sneakCells.size()); i++) {
                if (sneakCells.get(i).equals(sneakCells.get(0)) && i != 0) {
                    startX=(float)width/5;
                    start = "You are dead. Replay?";
                    alive = false;
                }
                if (sneakCells.size() > 1 && i == 0)
                    paint.setAlpha((int) (255 * (r / 10f)));
                canvas.drawRect(new Rect(sneakCells.get(i).sneakX, sneakCells.get(i).sneakY, sneakCells.get(i).sneakX + cellDim, sneakCells.get(i).sneakY + cellDim), paint);
                paint.setAlpha(255);
            }
            if (sneakCells.size() > 1)
                canvas.drawRect(new Rect(sneakCells.get(sneakCells.size() - 1).sneakX - (sneakCells.get(sneakCells.size() - 1).sneakX - sneakCells.get(sneakCells.size() - 2).sneakX) * r / 10, sneakCells.get(sneakCells.size() - 1).sneakY - (sneakCells.get(sneakCells.size() - 1).sneakY - sneakCells.get(sneakCells.size() - 2).sneakY) * r / 10, sneakCells.get(sneakCells.size() - 1).sneakX - (sneakCells.get(sneakCells.size() - 1).sneakX - sneakCells.get(sneakCells.size() - 2).sneakX) * r / 10 + cellDim, sneakCells.get(sneakCells.size() - 1).sneakY - (sneakCells.get(sneakCells.size() - 1).sneakY - sneakCells.get(sneakCells.size() - 2).sneakY) * r / 10 + cellDim), paint);
            paint.setColor(Color.RED);
            canvas.drawRect(new Rect(appleX, appleY, appleX + cellDim, appleY + cellDim), paint);
            canvas.drawText(String.valueOf(sneakCells.size()), 50, 80, paint);
        }
    }
}

class Sneak {
    int sneakX, sneakY;

    Sneak(int sneakX, int sneakY) {
        this.sneakX = sneakX;
        this.sneakY = sneakY;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Sneak) && sneakX == ((Sneak) obj).sneakX && sneakY == ((Sneak) obj).sneakY;
    }
}