package com.jobs.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


final class Memory {

	static ViewMode viewMode = ViewMode.PreStart;
	static State currentState;
	static GameView gameView;

	static byte cellCountWidth = 0, cellCountHeight = 0;

	static int cellSize = 0;

	static byte STATE = 0;
	static byte DIRECTION = 1;
	static byte APPLE = 2;

	static int nod(int a, int b) {
		return b == 0 ? a : nod(b, a % b);
	}

	static SnakeDummy dummy = new SnakeDummy(20, 16);
	static Snake snake = new Snake((byte) 0, (byte) 0, Color.GREEN);
	static Snake snakeEnemy = new Snake((byte) 0, (byte) 0, Color.RED);
	static Apple apple = new Apple((byte) 0, (byte) 0, Color.BLUE);

	//Кисти
	static Paint paint_text = new Paint();

	static Rect boundOfSinglePlayerText = new Rect();
	static Rect boundOfMultiPlayerText = new Rect();
	static Rect boundOfFastGame = new Rect();
	static Rect boundOfListRoom = new Rect();
	//Данные шрифта (вспомогательная переменная)
	private static Rect bound = new Rect();

	//Отрисовать текст
	static void DrawText(Canvas canvas, String text, int x, int y, TextScale textScale, int color) {
		paint_text.setColor(color);
		paint_text.setTextSize((float) canvas.getHeight() / textScale.getValue());
		paint_text.getTextBounds(text, 0, text.length(), bound);
		canvas.drawText(text, x - bound.width() / 2f, y + bound.height() / 2f, paint_text);
	}

	static void DrawText(Canvas canvas, String text, int x, int y, TextScale textScale, int color, Rect bound) {
		paint_text.setColor(color);
		paint_text.setTextSize((float) canvas.getHeight() / textScale.getValue());
		paint_text.getTextBounds(text, 0, text.length(), bound);
		canvas.drawText(text, x - bound.width() / 2f, y + bound.height() / 2f, paint_text);
	}

}

enum TextScale {
	Huge(3), Big(5), Normal(7), Small(10);

	private final int value;

	TextScale(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

enum State {
	Exited((byte) 0), Ready((byte) 1);

	private final byte value;

	State(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}

enum ViewMode {
	PreStart, Menu, SingleRoom, MultiRoom, MultiGamePage, SettignsPage, PausePage, LosePage, WinPage, ListOfRooms, Connecting
}

enum Direction {
	Up, Right, Down, Left
}