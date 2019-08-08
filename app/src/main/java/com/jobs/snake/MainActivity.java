package com.jobs.snake;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(new GameView(this));
    }

    @Override
    public void onBackPressed() {
        if (Memory.isAlive) {
            Memory.isPause = true;
            Memory.isAlive = false;
        } else if (!Memory.isFirst) {
            Memory.isFirst = true;
            Memory.isPause = false;
        } else finish();
    }
}

class GameView extends View {
    public GameView(final Context context) {
        super(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(100);
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
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private float x1 = 0, y1 = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        super.onTouchEvent(m);
        if (!Memory.isAlive) {
            if (m.getY() >= getHeight() / 2 - Memory.boundOfSinglePlayerText.height() / 2 && m.getY() <= getHeight() / 2 + Memory.boundOfSinglePlayerText.height() / 2 && m.getX() >= getWidth() / 2 - Memory.boundOfSinglePlayerText.width() / 2 && m.getX() <= getWidth() / 2 + Memory.boundOfSinglePlayerText.width() / 2)
                Memory.isAlive = !Memory.isFirst;
        } else
            switch (m.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = m.getX();
                    y1 = m.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float v1 = m.getX() - x1, v2 = m.getY() - y1;
                    if (Math.abs(v1) > Math.abs(v2)) {
                        if (v1 != 0 && (Memory.snake.direction == Direction.Up || Memory.snake.direction == Direction.Down))
                            Memory.snake.direction = v1 > 0 ? Direction.Right : Direction.Left;
                    } else if (v2 != 0 && (Memory.snake.direction == Direction.Left || Memory.snake.direction == Direction.Right))
                        Memory.snake.direction = v2 > 0 ? Direction.Down : Direction.Up;
                    break;
            }
        return true;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        if (!Memory.isAlive) {
            if (Memory.isFirst) {
                Memory.cellSize = Memory.nod(getWidth(), getHeight()) / 4;
                Memory.cellCountWidth = (byte) (getWidth() / Memory.cellSize);
                Memory.cellCountHeight = (byte) (getHeight() / Memory.cellSize);
                Memory.snake.random();
                Memory.apple.random();
                Memory.isFirst = false;
            }
            if (Memory.isPause)
                Memory.DrawText(canvas, getContext().getResources().getString(R.string.continue_game), getWidth() / 2, getHeight() / 2, TextScale.Normal, Color.WHITE, Memory.boundOfSinglePlayerText);
            else {
                Memory.DrawText(canvas, getContext().getResources().getString(R.string.single_player_mode), getWidth() / 2, getHeight() / 2, TextScale.Normal, Color.WHITE, Memory.boundOfSinglePlayerText);
                Memory.DrawText(canvas, getContext().getResources().getString(R.string.multi_player_mode), getWidth() / 2, getHeight() / 2 + (Memory.boundOfSinglePlayerText.bottom - Memory.boundOfSinglePlayerText.top), TextScale.Normal, Color.WHITE, Memory.boundOfMultiPlayerText);
            }
        } else {
            Memory.snake.onDraw(canvas);
            Memory.apple.onDraw(canvas);
            Memory.DrawText(canvas, String.valueOf(Memory.snake.cells.size()), 50, 50, TextScale.Small, Color.YELLOW);
        }
    }
}

class Apple {
    Point position;
    private Paint paint = new Paint();

    Apple(byte x, byte y, int color) {
        position = new Point(x, y);
        paint.setColor(color);
    }

    void random() {
        position.x = (byte) new Random().nextInt(Memory.cellCountWidth);
        position.y = (byte) new Random().nextInt(Memory.cellCountHeight);
        if (randomCheck()) random();
    }

    private boolean randomCheck() {
        for (int i = 0; i < Memory.snake.cells.size(); i++)
            if (position.equals(Memory.snake.cells.get(i)))
                return true;
        return false;
    }

    void onDraw(Canvas canvas) {
        canvas.drawRect(position.x * Memory.cellSize, position.y * Memory.cellSize, (position.x + 1) * Memory.cellSize, (position.y + 1) * Memory.cellSize, paint);
    }
}

class Snake {
    private Paint paint = new Paint();
    Direction direction;
    static byte directionNumber;
    ArrayList<Point> cells = new ArrayList<>();

    Snake(byte x, byte y, int color) {
        cells.add(new Point(x, y));
        paint.setColor(color);
        direction = randomDirection();
    }

    void random() {
        cells.clear();
        cells.add(new Point((byte) new Random().nextInt(Memory.cellCountWidth), (byte) new Random().nextInt(Memory.cellCountHeight)));
        direction = randomDirection();
    }

    private Direction randomDirection() {
        switch (new Random().nextInt(3)) {
            case 0:
                directionNumber = 0;
                return Direction.Up;
            case 1:
                directionNumber = 1;
                return Direction.Right;
            case 2:
                directionNumber = 2;
                return Direction.Down;
            default:
                directionNumber = 3;
                return Direction.Left;
        }
    }

    private Point left() {
        return new Point((byte) (cells.get(0).x - 1 >= 0 ? cells.get(0).x - 1 : cells.get(0).x + Memory.cellCountWidth - 1), cells.get(0).y);
    }

    private Point down() {
        return new Point(cells.get(0).x, (byte) (cells.get(0).y + 1 < Memory.cellCountHeight ? cells.get(0).y + 1 : cells.get(0).y - Memory.cellCountHeight + 1));
    }

    private Point right() {
        return new Point((byte) (cells.get(0).x + 1 < Memory.cellCountWidth ? cells.get(0).x + 1 : cells.get(0).x - Memory.cellCountWidth + 1), cells.get(0).y);
    }

    private Point up() {
        return new Point(cells.get(0).x, (byte) (cells.get(0).y - 1 >= 0 ? cells.get(0).y - 1 : cells.get(0).y + Memory.cellCountHeight - 1));
    }

    void onDraw(Canvas canvas) {
        switch (direction) {
            case Up:
                directionNumber = 0;
                cells.add(0, up());
                break;
            case Right:
                directionNumber = 1;
                cells.add(0, right());
                break;
            case Down:
                directionNumber = 2;
                cells.add(0, down());
                break;
            case Left:
                directionNumber = 3;
                cells.add(0, left());
                break;
        }
        if (cells.get(0).x != Memory.apple.position.x || cells.get(0).y != Memory.apple.position.y)
            cells.remove(cells.size() - 1);
        else
            Memory.apple.random();
        for (int i = 0; i < cells.size(); i++) {
            if (i != 0 && cells.get(0).equals(cells.get(i))) {
                Memory.isAlive = false;
                Memory.isFirst = true;
            }
            canvas.drawRect(cells.get(i).x * Memory.cellSize, cells.get(i).y * Memory.cellSize, (cells.get(i).x + 1) * Memory.cellSize, (cells.get(i).y + 1) * Memory.cellSize, paint);
        }
    }
}