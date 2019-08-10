package com.jobs.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

//	Статический класс для хранения промежуточных переменных
final class Memory {

	//	Данные о текущей и предыдущей странице
	static ViewMode viewMode = ViewMode.FirstStart, previousViewMode = null;

	//	Количество клеток по ширине и высоте вмещаемые на экране
	static byte cellCountWidth = 0, cellCountHeight = 0;

	//	Размер клетки
	static int cellSize = 0;

	//	Скорость змеи
	static int speed = 5;

	//	Вычисление НОД
	static int nod(int a, int b) {
		return b == 0 ? a : nod(b, a % b);
	}

	//	Манекен змейки для страницы настроек
	static SnakeDummy dummy = new SnakeDummy(0, 0);
	//  Основная змейка
	static Snake snake = new Snake((byte) 0, (byte) 0, Color.GREEN);
	static Apple apple = new Apple((byte) 0, (byte) 0, Color.BLUE);

	//	Кисть для текста
	static Paint paint_text = new Paint();

	//	Границы для текстов
	static Rect boundOfSinglePlayerText = new Rect();
	static Rect boundOfMultiPlayerText = new Rect();

	//	Данные шрифта (вспомогательная переменная)
	private static Rect bound = new Rect();

	//	Отрисовать текст
	static void DrawText(Canvas canvas, String text, int x, int y, TextScale textScale, int color) {
		paint_text.setColor(color);
		paint_text.setTextSize((float) canvas.getHeight() / textScale.getValue());
		paint_text.getTextBounds(text, 0, text.length(), bound);
		canvas.drawText(text, x - bound.width() / 2f, y + bound.height() / 2f, paint_text);
	}

	//	Отрисовать текст и получить его границы
	static void DrawText(Canvas canvas, String text, int x, int y, TextScale textScale, int color, Rect bound) {
		paint_text.setColor(color);
		paint_text.setTextSize((float) canvas.getHeight() / textScale.getValue());
		paint_text.getTextBounds(text, 0, text.length(), bound);
		canvas.drawText(text, x - bound.width() / 2f, y + bound.height() / 2f, paint_text);
	}
}

//	Стандарты размеров текста
enum TextScale {
	// Огромный
	Huge(3),
	//	Большой
	Big(5),
	//	Средний
	Normal(7),
	//	Мелкий
	Small(10);

	//	Значение размера текста, рассчёт по формуле (getHeight() / value)
	private final int value;

	//	Конструктор для задания значения размера текста
	TextScale(int value) {
		this.value = value;
	}

	//	Получить значение размера текста
	public int getValue() {
		return value;
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
	//	Комната для многопользовательской игры
	MultiRoom,
	//	Страница для выбора комнаты в многопользовательской игре
	MultiGamePage,
	//	Страница настроек
	SettingsPage,
	//	Страница паузы во время одиночной игры
	PausePage,
	//	Страница проигрыша, после смерти змеи основного игрока
	LosePage,
	//	Страница победы, после смерти змеи соперника
	WinPage
}

//	Направления (для змейки)
enum Direction {
	//	Вверх
	Up,
	//	Вправо
	Right,
	//	Вниз
	Down,
	// Влево
	Left
}