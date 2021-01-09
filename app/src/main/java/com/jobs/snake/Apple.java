package com.jobs.snake;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;

public class Apple {

	//	Позиция яблока
	Point position;

	//	Кисть
	private final Paint paint = new Paint();

	//	Конструктор с позицией яблока и его цветом
	Apple(byte x, byte y, int color) {

		//	Задаём позицию
		position = new Point(x, y);

		//	Задаём цвет
		paint.setColor(color);
	}

	void setPosition(byte x, byte y) {
		position.x = x;
		position.y = y;
	}

	//	Случайная позиция
	public void random() {

		//	Задаём новую позицию, сгенерированную рандомом
		position.x = (byte) new Random().nextInt(Memory.cellCountWidth);
		position.y = (byte) new Random().nextInt(Memory.cellCountHeight);
		//	Перегенерируем если оно заспавнилось на хвосте змеи
		if (randomCheck()) random();
	}

	//	Проверка случайного спавна (если яблоко в хвосте змеи, то выдаём true, иначе false)
	private boolean randomCheck() {
		for (int i = 0; i < Memory.snake.cells.size(); i++)
			if (position.equals(Memory.snake.cells.get(i)))
				return true;
		return false;
	}

	//	Отрисовка
	public void onDraw(Canvas canvas) {
		canvas.drawRect(position.x * Memory.cellSize, position.y * Memory.cellSize, (position.x + 1) * Memory.cellSize, (position.y + 1) * Memory.cellSize, paint);
	}
}
