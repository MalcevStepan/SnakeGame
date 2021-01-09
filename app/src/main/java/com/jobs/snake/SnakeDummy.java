package com.jobs.snake;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.jobs.snake.ext.Direction;

import java.util.ArrayList;

//	Манекен змеи
public class SnakeDummy {

	//	Кисть
	public Paint paint = new Paint();

	//	Направление движения змеи
	private Direction direction;

	//	Части тела змеи
	private final ArrayList<Point> cells = new ArrayList<>();

	//	Позиция клетки
	private final Point position;

	//	Индекс анимации
	private int index = 0;

	//	Конструктор с позицией клетки (верхняя левая граница)
	SnakeDummy(int x, int y) {

		//	Задаём позицию
		position = new Point((byte) x, (byte) y);

		//	Задаём начальное направление
		direction = Direction.Left;
	}

	//	Изменение координат позиции
	public void setPosition(byte x, byte y) {

		//	Задаём новые координаты
		position.x = x;
		position.y = y;

		//	Очищаем хвост змеи
		cells.clear();

		//	Начинаем анимацию с начала
		index = 0;

		//	Добавляем элементы змеи в правый нижний угол
		for (int i = 0; i < 12; i++)
			cells.add(new Point((byte) (x + 12), (byte) (y + 3)));
	}

	//	Последующая левая клетка
	private Point left() {
		return new Point((byte) (cells.get(0).x - 1 >= position.x ? cells.get(0).x - 1 : cells.get(0).x + 12), cells.get(0).y);
	}

	//	Последующая нижняя клетка
	private Point down() {
		return new Point(cells.get(0).x, (byte) (cells.get(0).y + 1));
	}

	//	Последующая верхняя клетка
	private Point up() {
		return new Point(cells.get(0).x, (byte) (cells.get(0).y - 1));
	}

	private int speed = 0;

	//	Отрисовка манекена
	public void onDraw(Canvas canvas) {

		if ((speed += Memory.deltaTime) >= Memory.speed) {

			//	Проверяем направление
			switch (direction) {

				//	Если вверх, то добавляем клетку сверху
				case Up:
					cells.add(0, up());
					break;

				//	Если вниз, то добавляем клетку снизу
				case Down:
					cells.add(0, down());
					break;

				//	Если влево, то добавляем клетку слева
				case Left:
					cells.add(0, left());
					break;
			}

			//	Удаляем конец змеи
			cells.remove(cells.size() - 1);

			//	Отрисовываем все клетки
			for (int i = 0; i < cells.size(); i++)
				canvas.drawRect(cells.get(i).x * Memory.cellSize, cells.get(i).y * Memory.cellSize, (cells.get(i).x + 1) * Memory.cellSize, (cells.get(i).y + 1) * Memory.cellSize, paint);

			//	Проверка кадра, для выбора направления (для анимации)
			switch (++index) {
				case 2:
				case 14:
					direction = Direction.Up;
					break;
				case 5:
				case 11:
				case 17:
				case 23:
					direction = Direction.Left;
					break;
				case 8:
				case 20:
					direction = Direction.Down;
					break;
				case 24:
					index = 0;
					break;
			}

			speed -= Memory.speed;
		} else
			for (int i = 0; i < cells.size(); i++)
				canvas.drawRect(cells.get(i).x * Memory.cellSize, cells.get(i).y * Memory.cellSize, (cells.get(i).x + 1) * Memory.cellSize, (cells.get(i).y + 1) * Memory.cellSize, paint);
	}
}
