package com.jobs.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

final class Memory {

    static boolean isAlive = false, isFirst = true;

    static byte cellCountWidth = 0, cellCountHeight = 0;

    static int cellSize = 0;

    static int nod(int a, int b) {
        return b == 0 ? a : nod(b, a % b);
    }

    static int serverPort;
    static int serverIP;

    static Snake snake = new Snake((byte)0, (byte)0, Color.GREEN);
    static Apple apple = new Apple((byte)0, (byte)0, Color.BLUE);

    //Кисти
    private static Paint paint_text = new Paint();



    static void setBoundOfSinglePlayerText() {
        boundOfSinglePlayerText = bound;
    }

    static void setBoundOfMultiPlayerText() {
        boundOfMultiPlayerText = bound;
    }

    static Rect boundOfSinglePlayerText = new Rect();
    static Rect boundOfMultiPlayerText = new Rect();
    //Данные шрифта (вспомогательная переменная)
    private static Rect bound = new Rect();

    //Отрисовать текст
    static void DrawText(Canvas canvas, String text, int x, int y, TextScale textScale, int color) {
        paint_text.setColor(color);
        paint_text.setTextSize((float) canvas.getHeight() / textScale.getValue());
        paint_text.getTextBounds(text, 0, text.length(), bound);
        canvas.drawText(text, x - bound.width() / 2f, y + bound.height() / 2f, paint_text);
    }

}

enum TextScale {
    Huge(5), Big(7), Normal(10), Small(16);

    private final int value;

    TextScale(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

enum Direction {
    Up, Right, Down, Left
}