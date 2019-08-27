package com.jobs.snake;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;

//  Стандартное Activity
public class MainActivity extends Activity {

	//  Создание Activity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//  Скрытие SystemUI
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		//  Проверяем первичный запуск, если запуск не в первый раз, то пропускаем страницу рассчётов
		if (Memory.viewMode != ViewMode.FirstStart && Memory.viewMode != ViewMode.PreStart)
			Memory.viewMode = ViewMode.Loading;

		//  Помещаем холст на Activity
		setContentView(new GameView(this));
	}

	//  Переход на паузу (при сворачивании игры)
	@Override
	protected void onPause() {
		super.onPause();

		//	Если игрок в игре, то ставим игру на паузу
		if (Memory.viewMode == ViewMode.SingleRoom)
			Memory.viewMode = ViewMode.PausePage;
	}

	//  При уничтожении Activity (при блокировке экрана)
	@Override
	protected void onDestroy() {
		super.onDestroy();

		//	Если игрок в игре, то ставим игру на паузу
		if (Memory.viewMode == ViewMode.SingleRoom)
			Memory.viewMode = ViewMode.PausePage;

		//	Запоминаем последнюю страницу
		Memory.previousViewMode = Memory.viewMode;
	}

	//  Нажатие BackButton на NavBar
	@Override
	public void onBackPressed() {

		//	Проверка открытой страницы
		switch (Memory.viewMode) {

			//	Если игрок в игре, то ставим игру на паузу
			case SingleRoom:

				//	Если в мултиплеере, то очищаем последние данные
			case MultiRoom:
				Memory.viewMode = ViewMode.PausePage;
				break;

			//	Если игрок на странице статистики проигрыша, то очищаем последние данные
			case LosePage:
				Memory.viewMode = ViewMode.PreStart;
				break;

			//	Если игрок на паузе, то выводим данные проигрыша
			case PausePage:
				Memory.viewMode = ViewMode.LosePage;
				break;

			//	Если в меню, то выходим из игры
			case Menu:
				finish();
				break;

			//	Если в настройках, то возвращаемся в меню
			case SettingsPage:
				Memory.viewMode = ViewMode.Menu;
				break;
		}
	}
}

//  Игровой холст
class GameView extends View {

	//  Конструктор с аргументом под контекст
	public GameView(final Context context) {
		super(context);

		//  Новый поток для отрисовки содержимого на холсте
		new Thread(() -> {
			try {

				//	Бесконечный цикл отрисовки
				while (true) {

					//	Пауза между кадрами
					Thread.sleep(1);

					//	Отрисовка холста
					if (Memory.viewMode != ViewMode.MultiRoom)
						post(this::invalidate);
				}
			} catch (InterruptedException e) {

				//	Выводим ошибку при её наличии
				e.printStackTrace();
			}

		}).start();    //	Запускаем поток
	}

	//  Разрешение кликать по холсту
	@Override
	public boolean performClick() {
		super.performClick();
		return true;
	}

	//  Координаты начала косания пальца
	private float x1 = 0, y1 = 0;
	private byte number = 0, count = 0;
	private ArrayList<MultiSnake> snakes = new ArrayList<>();
	private Apple apple = new Apple((byte) 0, (byte) 0, Color.YELLOW);

	//	Аннотация
	//	Косания холста
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent m) {
		super.onTouchEvent(m);

		//	Проверка открытой страницы
		switch (Memory.viewMode) {

			//	Если меню
			case Menu:

				//	Если палец отпускается
				if (m.getActionMasked() == MotionEvent.ACTION_UP) {

					//	Проверка позиции косания с кнопкой Одиночной игры, если косается, то открытие комнаты одиночной игры
					if (m.getY() >= getHeight() / 2 - Memory.boundOfSinglePlayerText.height() / 2 && m.getY() <= getHeight() / 2 + Memory.boundOfSinglePlayerText.height() / 2 && m.getX() >= getWidth() / 2 - Memory.boundOfSinglePlayerText.width() / 2 && m.getX() <= getWidth() / 2 + Memory.boundOfSinglePlayerText.width() / 2)
						Memory.viewMode = ViewMode.SingleRoom;
					else

						//	Проверка нажатия на левый верхний угол, для открытия настроек
						if (m.getY() <= 100 && m.getX() <= 100)
							Memory.viewMode = ViewMode.SettingsPage;
						else {
							Net.sendMessage(new byte[]{(byte) 1, Memory.cellCountWidth, Memory.cellCountHeight});
							Net.sendMessage(new byte[]{2, (byte) (Memory.snake.paint.getColor() >>> 24), (byte) (Memory.snake.paint.getColor() >>> 16), (byte) (Memory.snake.paint.getColor() >>> 8), (byte) Memory.snake.paint.getColor()});
							Net.sendMessage(new byte[]{3});
							Memory.viewMode = ViewMode.MultiRoom;
							new Thread(() -> {
								boolean isLose = false;
								while (!isLose) {
									byte[] data = Net.getMessage();
									switch (data[0]) {
										case 1:
											Memory.cellCountWidth = data[1];
											Memory.cellCountHeight = data[2];
											Memory.cellSize = Math.min(getWidth() / data[1], getHeight() / data[2]);
											break;
										case 2:
											for (int i = 0; i < count; i++)
												snakes.add(new MultiSnake((data[(i * 4) + 1] << 24) & 0xff000000 | (data[(i * 4) + 2] << 16) & 0x00ff0000 | (data[(i * 4) + 3] << 8) & 0x0000ff00 | (data[(i * 4) + 4]) & 0x000000ff));
											break;
										case 3:
											number = data[1];
											count = data[2];
											break;
										case 4:
											for (int i = 0; i < count; i++)
												snakes.get(i).Update(new Point(data[(i * 2) + 1], data[(i * 2) + 2]));
											break;
										case 5:
											apple.setPosition(data[1], data[2]);
											break;
										case 6:
											snakes.get(data[1]).isAdded = true;
											apple.setPosition(data[2], data[3]);
											break;
										case 8:
											Memory.viewMode = ViewMode.MultiRoom;
											isLose = true;
											break;
									}
									post(this::invalidate);
								}
							}).start();
						}
				}
				break;

			//	Если пауза
			case PausePage:

				//	Если палец отпускается
				if (m.getActionMasked() == MotionEvent.ACTION_UP) {

					//	Проверка позиции косания с кнопкой продолжения игры, если косается, то открытие комнаты игры
					if (m.getY() >= getHeight() / 2 - Memory.boundOfSinglePlayerText.height() / 2 && m.getY() <= getHeight() / 2 + Memory.boundOfSinglePlayerText.height() / 2 && m.getX() >= getWidth() / 2 - Memory.boundOfSinglePlayerText.width() / 2 && m.getX() <= getWidth() / 2 + Memory.boundOfSinglePlayerText.width() / 2)
						Memory.viewMode = ViewMode.SingleRoom;

					//	Проверка нажатия на левый верхний угол, для выхода из одиночной игры
					if (m.getY() <= 50 + Memory.boundOfSinglePlayerText.height() && m.getX() <= 50 + Memory.boundOfSinglePlayerText.width())
						Memory.viewMode = ViewMode.LosePage;
				}
				break;

			//	Если комната одиночной игры
			case SingleRoom:

				//	Проверка действия
				switch (m.getActionMasked()) {

					//	Первое косание
					case MotionEvent.ACTION_DOWN:

						//	Записываем координаты
						x1 = m.getX();
						y1 = m.getY();
						break;

					//	Отрывание косания
					case MotionEvent.ACTION_UP:

						//	Проврка нажатия на левый верхний угол, для выхода на паузу
						if (y1 <= 50 + Memory.boundOfSinglePlayerText.height() && x1 <= 50 + Memory.boundOfSinglePlayerText.width())
							Memory.viewMode = ViewMode.PausePage;

						//	Получаем растояние пройденное пальцем
						float v1 = m.getX() - x1, v2 = m.getY() - y1;

						//	Проверка по какой из осей растояние пройдено больше, в ту сторону и изменяем направление
						if (Math.abs(v1) > Math.abs(v2)) {
							if (v1 != 0 && (Memory.snake.direction == Direction.Up || Memory.snake.direction == Direction.Down))
								Memory.snake.direction = v1 > 0 ? Direction.Right : Direction.Left;
						} else if (v2 != 0 && (Memory.snake.direction == Direction.Left || Memory.snake.direction == Direction.Right))
							Memory.snake.direction = v2 > 0 ? Direction.Down : Direction.Up;
						break;
				}
				break;

			case MultiRoom:

				//	Проверка действия
				switch (m.getActionMasked()) {

					//	Первое косание
					case MotionEvent.ACTION_DOWN:

						//	Записываем координаты
						x1 = m.getX();
						y1 = m.getY();
						break;

					//	Отрывание косания
					case MotionEvent.ACTION_UP:

						//	Проврка нажатия на левый верхний угол, для выхода на паузу
						if (y1 <= 50 + Memory.boundOfSinglePlayerText.height() && x1 <= 50 + Memory.boundOfSinglePlayerText.width())
							Memory.viewMode = ViewMode.PausePage;

						//	Получаем растояние пройденное пальцем
						float v1 = m.getX() - x1, v2 = m.getY() - y1;

						if (Math.abs(v1) > Math.abs(v2)) {
							if (v1 != 0 && (snakes.get(number).direction == 0 || snakes.get(number).direction == 2))
								snakes.get(number).direction = v1 > 0 ? (byte) 1 : (byte) 3;
						} else if (v2 != 0 && (snakes.get(number).direction == 3 || snakes.get(number).direction == 1))
							snakes.get(number).direction = v2 > 0 ? (byte) 2 : (byte) 0;

						//	Проверка по какой из осей растояние пройдено больше, в ту сторону и изменяем направление

						Net.sendMessage(new byte[]{4, number, snakes.get(number).direction});
						break;
				}
				break;

			//	Если страница проигрыша
			case LosePage:

				//	Отрывание косания
				if (m.getActionMasked() == MotionEvent.ACTION_UP)

					//	Проврка нажатия на левый верхний угол, для выхода со страницы
					if (m.getY() <= 50 + Memory.boundOfSinglePlayerText.height() && m.getX() <= 50 + Memory.boundOfSinglePlayerText.width())
						Memory.viewMode = ViewMode.PreStart;
				break;

			//	Если страница настроек
			case SettingsPage:

				//	Отрывание косания
				if (m.getActionMasked() == MotionEvent.ACTION_UP) {

					//	Проврка нажатия на левый верхний угол, для выхода со страницы
					if (m.getY() <= 50 + Memory.boundOfSinglePlayerText.height() && m.getX() <= 50 + Memory.boundOfSinglePlayerText.width())
						Memory.viewMode = ViewMode.Menu;

					sliderClick = SliderClick.None;
				}

				//	Рассчитываем ширину и высоту прямоугольника
				int cube_color_width = getWidth() / 30, cube_color_height = getHeight() / 36;

				//	Рассчитываем позицию слайдеров выбора цвета на экране
				int x = getWidth() - cube_color_width * 8, y = (getHeight() - (cube_color_height * 23 + cube_color_width)) / 2;

				//	Узнаём какой слайдер был нажат, если быд
				if (m.getActionMasked() == MotionEvent.ACTION_DOWN)
					if (m.getX() > x && m.getY() > y && m.getX() < x + cube_color_width && m.getY() < y + cube_color_height * 23 + cube_color_width)
						sliderClick = SliderClick.Color;
					else if (m.getX() > x + cube_color_width * 2 && m.getX() < x + cube_color_width * 3 && m.getY() > y && m.getY() < y + cube_color_height * 23 + cube_color_width)
						sliderClick = SliderClick.Brightness;
					else if (m.getX() > x + cube_color_width * 4 && m.getX() < x + cube_color_width * 5 && m.getY() > y && m.getY() < y + cube_color_height * 23 + cube_color_width)
						sliderClick = SliderClick.Speed;

				//	При перемещении косания проверяем какой из слайдеров используется и вычисляем выбранный цвет и яркость
				if (m.getActionMasked() == MotionEvent.ACTION_MOVE)
					if (m.getY() > y && m.getY() < y + cube_color_height * 23 + cube_color_width)
						switch (sliderClick) {
							case Color:
								Memory.selected_color = (int) ((m.getY() - y) / (cube_color_height * 23 + cube_color_width) * 24);
								break;

							case Brightness:
								Memory.selected_brightness = (int) ((m.getY() - y) / (cube_color_height * 23 + cube_color_width) * 24);
								break;

							case Speed:
								Memory.speed = (int) ((m.getY() - y) / (cube_color_height * 23 + cube_color_width) * 24);
								break;
						}
				break;
		}

		// Возвращаем true для повторного анализа
		return true;
	}

	//	Кисти для настроек
	Paint paint = new Paint(), paint_stroke = new Paint();

	//	На какой слайдер нажато
	SliderClick sliderClick = SliderClick.None;

	Thread firstConnection = new Thread(() -> {
		try {

			Net.address = InetAddress.getByName("94.103.94.112");
			Net.socket = new DatagramSocket();
			Net.socket.send(new DatagramPacket(new byte[]{0, Memory.cellCountWidth, Memory.cellCountHeight}, 3, Net.address, Net.port));
			DatagramPacket res = new DatagramPacket(new byte[4], 4);
			Net.socket.receive(res);
			byte[] rno = res.getData();
			Net.port = (rno[3] << 24) & 0xff000000 | (rno[2] << 16) & 0x00ff0000 | (rno[1] << 8) & 0x0000ff00 | (rno[0]) & 0x000000ff;
			Log.e("Net", "new port is " + Net.port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	});

	//	Яркость
	int brightness() {
		return 18 * Memory.selected_brightness - 177;
	}

	float endTime = System.nanoTime();

	//	Отрисовка
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float startTime = endTime;

		//	Заполняем задний фон чёрным цветом
		canvas.drawColor(Color.BLACK);

		//	Проверка открытой страницы
		switch (Memory.viewMode) {

			//	Первичный запуск
			case FirstStart:

				//	Вычисляем размер клетки
				Memory.cellSize = Memory.nod(getWidth(), getHeight());
				Memory.cellSize /= Math.abs(36 - getHeight() / Memory.cellSize / 4) < Math.abs(36 - getHeight() / Memory.cellSize / 8) ? 4 : Math.abs(36 - getHeight() / Memory.cellSize / 8) < Math.abs(36 - getHeight() / Memory.cellSize / 12) ? 8 : 12;

				//	Вычисляем стандартное количество по ширине
				Memory.cellCountWidth = (byte) (getWidth() / Memory.cellSize);

				//	Вычисляем стандартное количество по высоте
				Memory.cellCountHeight = (byte) (getHeight() / Memory.cellSize);

				//	Переходим на страницу PreStart
				Memory.viewMode = ViewMode.PreStart;

				//	Устанавливаем позицию манекену
				Memory.dummy.setPosition((byte) (Memory.cellCountWidth / 2 - 6), (byte) (Memory.cellCountHeight * 3 / 4));

				firstConnection.start();

				break;

			//	Страница очистки данных
			case PreStart:

				//	Генерируем случайную позицию змее
				Memory.snake.random();

				//	Генерируем случайную позицию яблоку
				Memory.apple.random();

				//	Устанваливаем шрифт для текста
				Memory.paint_text.setTypeface(Typeface.createFromAsset(getContext().getResources().getAssets(), "pixel_sans.ttf"));

				//	Вызываем гарбочку и пытаемся очистить мусор
				System.gc();

				//	Переходим на страницу загрузки
				Memory.viewMode = ViewMode.Loading;
				break;

			// Страница загрузки
			case Loading:

				//	Устанавливаем стиль обводки для кисти обводки
				paint_stroke.setStyle(Paint.Style.STROKE);

				//	Задаём цвет кисти обводки
				paint_stroke.setColor(Color.WHITE);

				//	Задаём толщину обводки
				paint_stroke.setStrokeWidth(8);

				//	Проверяем существует ли предыдущая страница, если да, тов возвращаемся на её, иначе в меню
				if (Memory.previousViewMode == null)
					Memory.viewMode = ViewMode.Menu;
				else {
					Memory.viewMode = Memory.previousViewMode;
					Memory.previousViewMode = null;
				}
				break;

			//	Страница меню
			case Menu:

				//	Отрисовываем звёздочку настреок
				Memory.DrawText(canvas, "*", 50, 50, TextScale.Small, Color.YELLOW);

				//	Отрисовываем кнопку одиночной игры
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.single_player_mode), getWidth() / 2, getHeight() / 2, TextScale.Normal, Color.WHITE, Memory.boundOfSinglePlayerText);

				//	Отрисовываем кнопку многопользовательской игры
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.multi_player_mode), getWidth() / 2, getHeight() / 2 + Memory.boundOfSinglePlayerText.height() * 2, TextScale.Small, Color.DKGRAY, Memory.boundOfMultiPlayerText);
				break;

			//	Страница паузы
			case PausePage:

				//	Отрисовываем кнопку выхода с уровня
				Memory.DrawText(canvas, "<-", 50, 50, TextScale.Small, Color.YELLOW, Memory.boundOfSinglePlayerText);

				//	Отрисовываем кнопку продолжения игры
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.continue_game), getWidth() / 2, getHeight() / 2, TextScale.Normal, Color.WHITE, Memory.boundOfSinglePlayerText);
				break;

			//	Комната одиночной игры
			case SingleRoom:

				//	Отрисовка змеи
				Memory.snake.onDraw(canvas);

				//	Отрисовка яблока
				Memory.apple.onDraw(canvas);

				//	Отрисовка длинны змеи
				Memory.DrawText(canvas, String.valueOf(Memory.snake.cells.size()), 50, 50, TextScale.Small, Color.YELLOW, Memory.boundOfSinglePlayerText);
				break;

			case MultiRoom:
				for (int i = 0; i < snakes.size(); i++) {
					snakes.get(i).onDraw(canvas);
					if (snakes.get(i).cells.size() > 0)
						Memory.DrawText(canvas, (i == number ? "You" : "Player") + " [" + snakes.get(i).cells.size() + "]", (int) ((snakes.get(i).cells.get(0).x + 0.5f) * Memory.cellSize), (snakes.get(i).cells.get(0).y - 1) * Memory.cellSize, TextScale.VerySmall, i == number ? Color.WHITE : Color.RED);
				}
				apple.onDraw(canvas);
				break;

			//	Страница проигрыша
			case LosePage:

				//	Отрисовка кнопки назад
				Memory.DrawText(canvas, "<-", 50, 50, TextScale.Small, Color.YELLOW, Memory.boundOfSinglePlayerText);

				//	Отрисовка надписи проигрыша
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.you_lose), getWidth() / 2, getHeight() / 2, TextScale.Normal, Color.WHITE, Memory.boundOfSinglePlayerText);

				//	Отрисовка длинны змеи
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.your_score) + Memory.score, getWidth() / 2, getHeight() / 2 + Memory.boundOfSinglePlayerText.height() * 2, TextScale.Small, Color.WHITE);
				break;

			//	Страница настроек
			case SettingsPage:

				paint.setColor(Color.WHITE);
				//	Отрисовка кнопки назад
				Memory.DrawText(canvas, "<-", 50, 50, TextScale.Small, Color.YELLOW, Memory.boundOfSinglePlayerText);

				//	Рассчёт ширины и высоты прямоугольников выбора цвета и яркости
				int cube_color_width = getWidth() / 30, cube_color_height = getHeight() / 36, gray;

				//	Рассчёт позиции и цветов
				int r, g, b, offset = 0, x = getWidth() - cube_color_width * 8, y = (getHeight() - (cube_color_height * 23 + cube_color_width)) / 2;


				canvas.drawRect(x - cube_color_width / 4f, y - cube_color_width / 4f, x + cube_color_width * 1.25f, y + cube_color_height, paint);
				canvas.drawRect(x - cube_color_width / 4f + cube_color_width * 2, y - cube_color_width / 4f, x + cube_color_width * 1.25f + cube_color_width * 2, y + cube_color_height, paint);
				float left = x - cube_color_width / 4f + cube_color_width * 4;
				canvas.drawRect(left, y - cube_color_width / 4f, x + cube_color_width * 1.25f + cube_color_width * 4, y + cube_color_height, paint);

				canvas.drawRect(x - cube_color_width / 4f, y + cube_color_height * 24, x + cube_color_width * 1.25f, y + cube_color_height * 25 + cube_color_width / 4f, paint);
				canvas.drawRect(x - cube_color_width / 4f + cube_color_width * 2, y + cube_color_height * 24, x + cube_color_width * 1.25f + cube_color_width * 2, y + cube_color_height * 25 + cube_color_width / 4f, paint);
				canvas.drawRect(left, y + cube_color_height * 24, x + cube_color_width * 1.25f + cube_color_width * 4, y + cube_color_height * 25 + cube_color_width / 4f, paint);

				//	Отрисовка RED to GREEN
				for (int i = 0; i < 8; i++) {

					//	Рассчёт цвета ячейки
					r = 255 - i * 32;
					g = i * 32;
					b = brightness() < 0 ? 0 : brightness();
					r = r + brightness() > 255 ? 255 : r + brightness() < 0 ? 0 : r + brightness();
					g = g + brightness() > 255 ? 255 : g + brightness() < 0 ? 0 : g + brightness();

					//	Приминение цвета к кисти
					paint.setColor(Color.rgb(r, g, b));

					//	Если текущая ячейка является выделенным цветом, то отрисовываем её квадратной и применяем цвета к змейям, иначе отрисовываем обычную ячейку
					if (i == Memory.selected_color) {
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_width, paint);
						Memory.snake.paint.setColor(paint.getColor());
						Memory.dummy.paint.setColor(paint.getColor());
						offset += cube_color_width - cube_color_height;
					} else
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_height, paint);

					//	Приминяем смещение толщиной в текущую ячейку
					offset += cube_color_height;
				}

				//	Отрисовка GREEN to BLUE
				for (int i = 8; i < 16; i++) {

					//	Рассчёт цвета ячейки
					r = brightness() < 0 ? 0 : brightness();
					g = 255 - (i - 8) * 32;
					b = (i - 8) * 32;
					b = b + brightness() > 255 ? 255 : b + brightness() < 0 ? 0 : b + brightness();
					g = g + brightness() > 255 ? 255 : g + brightness() < 0 ? 0 : g + brightness();

					//	Приминение цвета к кисти
					paint.setColor(Color.rgb(r, g, b));

					//	Если текущая ячейка является выделенным цветом, то отрисовываем её квадратной и применяем цвета к змейям, иначе отрисовываем обычную ячейку
					if (i == Memory.selected_color) {
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_width, paint);
						Memory.snake.paint.setColor(paint.getColor());
						Memory.dummy.paint.setColor(paint.getColor());
						offset += cube_color_width - cube_color_height;
					} else
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_height, paint);

					//	Приминяем смещение толщиной в текущую ячейку
					offset += cube_color_height;
				}

				//	Отрисовка BLUE to RED
				for (int i = 16; i < 24; i++) {

					//	Рассчёт цвета ячейки
					r = (i - 16) * 32;
					g = brightness() < 0 ? 0 : brightness();
					b = 255 - (i - 16) * 32;
					b = b + brightness() > 255 ? 255 : b + brightness() < 0 ? 0 : b + brightness();
					r = r + brightness() > 255 ? 255 : r + brightness() < 0 ? 0 : r + brightness();

					//	Приминение цвета к кисти
					paint.setColor(Color.rgb(r, g, b));

					//	Если текущая ячейка является выделенным цветом, то отрисовываем её квадратной и применяем цвета к змейям, иначе отрисовываем обычную ячейку
					if (i == Memory.selected_color) {
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_width, paint);
						Memory.snake.paint.setColor(paint.getColor());
						Memory.dummy.paint.setColor(paint.getColor());
						offset += cube_color_width - cube_color_height;
					} else
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_height, paint);

					//	Приминяем смещение толщиной в текущую ячейку
					offset += cube_color_height;
				}

				//	Обнуляем смещение, для того что бы отрисовывать слайдер яркости с начала
				offset = 0;

				//	Отрисовка яркости (DARK to LIGHT)
				for (int i = 0; i < 24; i++) {

					//	Рассчёт яркости
					gray = 18 * i - 177;

					//	Рассчёт цвета ячейки
					if (Memory.selected_color < 8) {
						r = 255 - Memory.selected_color * 32;
						g = Memory.selected_color * 32;
						b = gray < 0 ? 0 : gray;
					} else if (Memory.selected_color < 16) {
						r = gray < 0 ? 0 : gray;
						g = 255 - (Memory.selected_color - 8) * 32;
						b = (Memory.selected_color - 8) * 32;
					} else {
						r = (Memory.selected_color - 16) * 32;
						g = gray < 0 ? 0 : gray;
						b = 255 - (Memory.selected_color - 16) * 32;
					}
					r = r + gray > 255 ? 255 : r + gray < 0 ? 0 : r + gray;
					g = g + gray > 255 ? 255 : g + gray < 0 ? 0 : g + gray;
					b = b + gray > 255 ? 255 : b + gray < 0 ? 0 : b + gray;

					//	Приминение цвета к кисти
					paint.setColor(Color.rgb(r, g, b));

					//	Если текущая ячейка является выделенной, то отрисовываем её квадратной, иначе отрисовываем обычную ячейку
					if (i == Memory.selected_brightness) {
						canvas.drawRect(x + cube_color_width * 2, y + offset, x + cube_color_width + cube_color_width * 2, y + offset + cube_color_width, paint);
						offset += cube_color_width - cube_color_height;
					} else
						canvas.drawRect(x + cube_color_width * 2, y + offset, x + cube_color_width + cube_color_width * 2, y + offset + cube_color_height, paint);

					//	Приминяем смещение толщиной в текущую ячейку
					offset += cube_color_height;
				}

				offset = 0;

				for (int i = 0; i < 24; i++) {

					//	Рассчёт цвета ячейки
					r = (int) (255 - i * 10.66666f);
					g = (int) (i * 10.66666f);
					b = 85;
					r = r + 85 > 255 ? 255 : r + 85 < 0 ? 0 : r + 85;
					g = g + 85 > 255 ? 255 : g + 85 < 0 ? 0 : g + 85;

					//	Приминение цвета к кисти
					paint.setColor(Color.rgb(r, g, b));

					//	Если текущая ячейка является выделенным цветом, то отрисовываем её квадратной и применяем цвета к змейям, иначе отрисовываем обычную ячейку
					if (i == Memory.speed) {
						canvas.drawRect(x + cube_color_width * 4, y + offset, x + cube_color_width * 5, y + offset + cube_color_width, paint);
						offset += cube_color_width - cube_color_height;
					} else
						canvas.drawRect(x + cube_color_width * 4, y + offset, x + cube_color_width * 5, y + offset + cube_color_height, paint);

					//	Приминяем смещение толщиной в текущую ячейку
					offset += cube_color_height;
				}

				//	Отрисовываем обводку для выделенных ячеек
				paint_stroke.setColor(Color.BLACK);
				canvas.drawRect(x + cube_color_width * 2 + 2, y + Memory.selected_brightness * cube_color_height + 2, x + cube_color_width + cube_color_width * 2 - 2, y + Memory.selected_brightness * cube_color_height + cube_color_width - 2, paint_stroke);
				canvas.drawRect(x + 2, y + Memory.selected_color * cube_color_height + 2, x + cube_color_width - 2, y + Memory.selected_color * cube_color_height + cube_color_width - 2, paint_stroke);
				canvas.drawRect(x + cube_color_width * 4 + 2, y + Memory.speed * cube_color_height + 2, x + cube_color_width + cube_color_width * 4 - 2, y + Memory.speed * cube_color_height + cube_color_width - 2, paint_stroke);
				paint_stroke.setColor(Color.WHITE);
				canvas.drawRect(x + cube_color_width * 2, y + Memory.selected_brightness * cube_color_height, x + cube_color_width + cube_color_width * 2, y + Memory.selected_brightness * cube_color_height + cube_color_width, paint_stroke);
				canvas.drawRect(x, y + Memory.selected_color * cube_color_height, x + cube_color_width, y + Memory.selected_color * cube_color_height + cube_color_width, paint_stroke);
				canvas.drawRect(x + cube_color_width * 4, y + Memory.speed * cube_color_height, x + cube_color_width + cube_color_width * 4, y + Memory.speed * cube_color_height + cube_color_width, paint_stroke);

				Memory.DrawText(canvas, getContext().getResources().getString(R.string.color_abbreviated), x + cube_color_width / 2, y - cube_color_height * 2, TextScale.Small, Color.WHITE);
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.brightness_abbreviated), x + cube_color_width / 2 + cube_color_width * 2, y - cube_color_height * 2, TextScale.Small, Color.WHITE);
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.speed_abbreviated), x + cube_color_width / 2 + cube_color_width * 4, y - cube_color_height * 2, TextScale.Small, Color.WHITE);

				//	Отрисовка текста под слайдерами
				switch (sliderClick) {
					case Color:
						Memory.DrawText(canvas, getContext().getResources().getString(R.string.color), x + cube_color_width / 2, y + cube_color_height * 27, TextScale.Small, Color.WHITE);
						break;
					case Brightness:
						Memory.DrawText(canvas, getContext().getResources().getString(R.string.brightness), x + cube_color_width / 2 + cube_color_width * 2, y + cube_color_height * 27, TextScale.Small, Color.WHITE);
						break;

					case Speed:
						Memory.DrawText(canvas, getContext().getResources().getString(R.string.speed), x + cube_color_width / 2 + cube_color_width * 4, y + cube_color_height * 27, TextScale.Small, Color.WHITE);
						break;
				}

				Memory.DrawText(canvas, getContext().getResources().getString(R.string.preview), getWidth() / 3, getHeight() / 2, TextScale.Small, Color.WHITE);
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.apple), getWidth() / 5, getHeight() * 3 / 5, TextScale.Small, Color.WHITE);
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.snake), getWidth() / 2, getHeight() * 3 / 5, TextScale.Small, Color.WHITE);
				Memory.DrawText(canvas, "Screen size: " + Memory.cellCountWidth + "x" + Memory.cellCountHeight, getWidth() / 3, getHeight() / 3, TextScale.Small, Color.WHITE);
				//	Отрисовываем манекен
				Memory.dummy.onDraw(canvas);
				break;
		}

		endTime = System.nanoTime();

		Memory.deltaTime = (endTime - startTime) / 16000000;
	}
}

//	Яблоко
class Apple {

	//	Позиция яблока
	Point position;

	//	Кисть
	private Paint paint = new Paint();

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
	void random() {

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
	void onDraw(Canvas canvas) {
		canvas.drawRect(position.x * Memory.cellSize, position.y * Memory.cellSize, (position.x + 1) * Memory.cellSize, (position.y + 1) * Memory.cellSize, paint);
	}
}

//	Змея
class Snake {

	//	Кисть
	Paint paint = new Paint();

	//	Направление
	Direction direction;

	//	Номер направления
	private byte directionNumber;

	//	Ячейки змеи
	ArrayList<Point> cells = new ArrayList<>();

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
	void random() {
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
	void onDraw(Canvas canvas) {

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

//	Манекен змеи
class SnakeDummy {

	//	Кисть
	Paint paint = new Paint();

	//	Направление движения змеи
	private Direction direction;

	//	Части тела змеи
	private ArrayList<Point> cells = new ArrayList<>();

	//	Позиция клетки
	private Point position;

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
	void setPosition(byte x, byte y) {

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
	void onDraw(Canvas canvas) {

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

class MultiSnake {
	private Paint paint = new Paint();

	byte direction = 0;

	//	Ячейки змеи
	ArrayList<Point> cells = new ArrayList<>();

	boolean isAdded = false;

	//	Констркутор со стартовой позицией и цветом змеи
	MultiSnake(int color) {
		paint.setColor(color);
	}

	void Update(Point point) {

		if (cells.size() > 0 && !isAdded)
			cells.remove(cells.size() - 1);
		isAdded = false;
		//	Проверка направления
		cells.add(0, point);
	}

	void onDraw(Canvas canvas) {
		for (int i = 0; i < cells.size(); i++)
			canvas.drawRect(cells.get(i).x * Memory.cellSize, cells.get(i).y * Memory.cellSize, (cells.get(i).x + 1) * Memory.cellSize, (cells.get(i).y + 1) * Memory.cellSize, paint);
	}
}