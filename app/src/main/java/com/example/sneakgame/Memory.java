package com.example.sneakgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

final class Memory {

    static boolean isAlive = false, isFirst = true;

    static int cellSize = 0, cellCountWidth = 0, cellCountHeight = 0;

    static int nod(int a, int b) {
        return b == 0 ? a : nod(b, a % b);
    }

    static Snake snake = new Snake(0, 0, Color.GREEN);
    static Apple apple = new Apple(0, 0, Color.RED);

    //Кисти
    private static Paint paint_text = new Paint();
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