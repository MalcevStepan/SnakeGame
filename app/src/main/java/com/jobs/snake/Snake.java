package com.jobs.snake;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.jobs.snake.ext.Direction;

import java.util.ArrayList;
import java.util.Random;

//	Змея
public class Snake {

	//	Кисть
	public Paint paint = new Paint();

	//	Направление
	public Direction direction;

	//	Номер направления
	private byte directionNumber;

	//	Ячейки змеи
	public ArrayList<Point> cells = new ArrayList<>();

	//	Констркутор со стартовой позицией и цветом змеи
	Snake(byte x, byte y, int color) {
		cells.add(new Point(x, y));
		paint.setColor(color);
		direction = randomDirection();
	}

	void setPosition(byte x, byte y, byte direction) {
		cells.clear();
		cells.add(new Point(x, y));
		directionNumber = direction;
		switch (directionNumber) {
			case (byte) 0:
				this.direction = Direction.Up;
			case (byte) 1:
				this.direction = Direction.Right;
			case (byte) 2:
				this.direction = Direction.Down;
			default:
				this.direction = Direction.Left;
		}
	}

	//	Очистка змейки и её случайная позиция
	public void random() {
		cells.clear();
		cells.add(new Point((byte) new Random().nextInt(Memory.cellCountWidth), (byte) new Random().nextInt(Memory.cellCountHeight)));
		direction = randomDirection();
	}

	private Direction randomDirection() {
		directionNumber = (byte) new Random().nextInt(3);
		switch (directionNumber) {
			case (byte) 0:
				return Direction.Up;
			case (byte) 1:
				return Direction.Right;
			case (byte) 2:
				return Direction.Down;
			default:
				return Direction.Left;
		}
	}

	//	Следующая клетка слева
	private Point left() {
		return new Point((byte) (cells.get(0).x - 1 >= 0 ? cells.get(0).x - 1 : cells.get(0).x + Memory.cellCountWidth - 1), cells.get(0).y);
	}

	//	Следующая клетка снизу
	private Point down() {
		return new Point(cells.get(0).x, (byte) (cells.get(0).y + 1 < Memory.cellCountHeight ? cells.get(0).y + 1 : cells.get(0).y - Memory.cellCountHeight + 1));
	}

	//	Следующая клетка справа
	private Point right() {
		return new Point((byte) (cells.get(0).x + 1 < Memory.cellCountWidth ? cells.get(0).x + 1 : cells.get(0).x - Memory.cellCountWidth + 1), cells.get(0).y);
	}

	//	Следующая клетка сверху
	private Point up() {
		return new Point(cells.get(0).x, (byte) (cells.get(0).y - 1 >= 0 ? cells.get(0).y - 1 : cells.get(0).y + Memory.cellCountHeight - 1));
	}

	private int speed = 0;

	//	Отрисовка
	public void onDraw(Canvas canvas) {

		if ((speed += Memory.deltaTime) >= Memory.speed) {

			//	Проверка направления
			switch (direction) {

				//	Если вверх, то добавляем клетку сверху
				case Up:
					directionNumber = 0;
					cells.add(0, up());
					break;

				//	Если вправо, то добавляем клетку справа
				case Right:
					directionNumber = 1;
					cells.add(0, right());
					break;

				//	Если вниз, то добавляем клетку снизу
				case Down:
					directionNumber = 2;
					cells.add(0, down());
					break;

				//	Если влево, то добавляем клетку слева
				case Left:
					directionNumber = 3;
					cells.add(0, left());
					break;
			}

			//	Проверяем, косаемся ли яблока, если нет, то укарачиваем хвост, иначе переспавним яблоко
			if (cells.get(0).x != Memory.apple.position.x || cells.get(0).y != Memory.apple.position.y)
				cells.remove(cells.size() - 1);
			else
				Memory.apple.random();

			//	Отрисовываем тело
			for (int i = 0; i < cells.size(); i++) {

				//	Проверяем не коснулась ли голова хвоста, тогда открываем страницу проигрыша
				if (i != 0 && cells.get(0).equals(cells.get(i)))
					Memory.viewMode = ViewMode.LosePage;
				canvas.drawRect(cells.get(i).x * Memory.cellSize, cells.get(i).y * Memory.cellSize, (cells.get(i).x + 1) * Memory.cellSize, (cells.get(i).y + 1) * Memory.cellSize, paint);
			}

			speed = 0;
		} else

			//	Отрисовываем тело
			for (int i = 0; i < cells.size(); i++)
				canvas.drawRect(cells.get(i).x * Memory.cellSize, cells.get(i).y * Memory.cellSize, (cells.get(i).x + 1) * Memory.cellSize, (cells.get(i).y + 1) * Memory.cellSize, paint);
	}
}
