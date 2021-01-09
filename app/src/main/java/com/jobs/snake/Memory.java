package com.jobs.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.jobs.snake.ext.TextScale;

import androidx.annotation.NonNull;

import java.util.Random;

//	Статический класс для хранения промежуточных переменных
public final class Memory {

	public static Typeface font;

	static void init(final @NonNull Context context) {
		font = Typeface.createFromAsset(context.getResources().getAssets(), "pixel_sans.ttf");
		paint_text.setTypeface(font);
	}

	//	Данные о текущей и предыдущей странице
	static ViewMode viewMode = ViewMode.FirstStart, previousViewMode = null;

	//	Выбранные цвета и яркость
	public static int selected_color = new Random().nextInt(24), selected_brightness = new Random().nextInt(14) + 10, score = 0;

	static int getSelected_color() {
		//	Рассчёт яркости
		int gray = 10 * selected_brightness - 57, r, g, b;

		//	Рассчёт цвета ячейки
		if (Memory.selected_color < 8) {
			r = 255 - Memory.selected_color * 32;
			g = Memory.selected_color * 32;
			b = Math.max(gray, 0);
		} else if (Memory.selected_color < 16) {
			r = Math.max(gray, 0);
			g = 255 - (Memory.selected_color - 8) * 32;
			b = (Memory.selected_color - 8) * 32;
		} else {
			r = (Memory.selected_color - 16) * 32;
			g = Math.max(gray, 0);
			b = 255 - (Memory.selected_color - 16) * 32;
		}
		r = r + gray > 255 ? 255 : Math.max(r + gray, 0);
		g = g + gray > 255 ? 255 : Math.max(g + gray, 0);
		b = b + gray > 255 ? 255 : Math.max(b + gray, 0);
		return Color.rgb(r, g, b);
	}

	//	Количество клеток по ширине и высоте вмещаемые на экране
	public static byte cellCountWidth = 0, cellCountHeight = 0;

	//	Размер клетки
	public static int cellSize = 0;

	//	Скорость змеи
	public static int speed = 5;

	//	Вычисление НОД
	public static int nod(int a, int b) {
		return b == 0 ? a : nod(b, a % b);
	}

	public static float deltaTime = 1f;

	//	Манекен змейки для страницы настроек
	public static SnakeDummy dummy = new SnakeDummy(0, 0);
	//  Основная змейка
	public static Snake snake = new Snake((byte) 0, (byte) 0, Color.GREEN);
	static Snake snakeEnemy = new Snake((byte) 0, (byte) 0, Color.RED);
	public static Apple apple = new Apple((byte) 0, (byte) 0, Color.BLUE);

	//	Кисть для текста
	static Paint paint_text = new Paint();

	//	Границы для текстов
	static Rect boundOfMultiPlayerText = new Rect();

	//	Данные шрифта (вспомогательная переменная)
	private static Rect bound = new Rect();

	//	Отрисовать текст
	public static void DrawText(Canvas canvas, String text, int x, int y, TextScale textScale, int color) {
		paint_text.setColor(color);
		paint_text.setTextSize((float) canvas.getHeight() / textScale.getValue());
		paint_text.getTextBounds(text, 0, text.length(), bound);
		canvas.drawText(text, x - bound.width() / 2f, y + bound.height() / 2f, paint_text);
	}

	//	Отрисовать текст и получить его границы
	public static void DrawText(Canvas canvas, String text, int x, int y, TextScale textScale, int color, Rect bound) {
		paint_text.setColor(color);
		paint_text.setTextSize((float) canvas.getHeight() / textScale.getValue());
		paint_text.getTextBounds(text, 0, text.length(), bound);
		canvas.drawText(text, x - bound.width() / 2f, y + bound.height() / 2f, paint_text);
	}

	static byte[] intToBytes(final int data) {
		return new byte[]{
				(byte) ((data >> 24) & 0xff),
				(byte) ((data >> 16) & 0xff),
				(byte) ((data >> 8) & 0xff),
				(byte) ((data) & 0xff),
		};
	}
}

//	Возможные страницы
enum ViewMode {

	//	Первичная страница, вызывается при первом запуске приложения для рассчёта размера клеток и их количества
	FirstStart,

	//	Вызывается при нужде перезаписи данных змейки и обнулении результата (обычно при переходе в меню)
	PreStart,

	//	Страница для перезгрузки данных, вызывается при перезапуске Activity, что бы не потерять данные об игре
	Loading,

	//	Первая страница меню, для выбора режима игры
	Menu,

	//	Комната для одиночной игры
	SingleRoom,

	//	Страница настроек
	SettingsPage,

	//	Страница паузы во время одиночной игры
	PausePage,

	//	Страница проигрыша, после смерти змеи основного игрока
	LosePage,

	//	Страница победы, после смерти змеи соперника
	WinPage
}

