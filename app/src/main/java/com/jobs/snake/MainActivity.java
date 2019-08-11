package com.jobs.snake;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
		Memory.gameView = new GameView(this);
		setContentView(Memory.gameView);
	}

	@Override
	public void onBackPressed() {
		switch (Memory.viewMode) {
			case SingleRoom:
				Memory.viewMode = ViewMode.PausePage;
				break;
			case PausePage:
				Memory.viewMode = ViewMode.PreStart;
				break;
			case Menu:
				finish();
				break;
			case SettignsPage:
				Memory.viewMode = ViewMode.Menu;
				break;
			case MultiRoom:
				Memory.currentState = State.Exited;
				Multiplayer.sendState();
				Memory.viewMode = ViewMode.Menu;
				break;
			case MultiGamePage:
				Memory.currentState = State.Exited;
				Multiplayer.sendState();

				Memory.viewMode = ViewMode.MultiRoom;
				break;
			case Connecting:
				Memory.currentState = State.Exited;
				Multiplayer.sendState();
				Memory.viewMode = ViewMode.MultiRoom;
				break;
		}
	}
}

class GameView extends View {
	public GameView(final Context context) {
		super(context);
		new Thread(() -> {
			try {
				while (true) {
					Thread.sleep(50);
					post(this::invalidate);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		switch (Memory.viewMode) {
			case Menu:
				if (m.getActionMasked() == MotionEvent.ACTION_UP && m.getY() >= getHeight() / 2 - Memory.boundOfSinglePlayerText.height() / 2 && m.getY() <= getHeight() / 2 + Memory.boundOfSinglePlayerText.height() / 2 && m.getX() >= getWidth() / 2 - Memory.boundOfSinglePlayerText.width() / 2 && m.getX() <= getWidth() / 2 + Memory.boundOfSinglePlayerText.width() / 2)
					Memory.viewMode = ViewMode.SingleRoom;
				if (m.getActionMasked() == MotionEvent.ACTION_UP && m.getY() >= getHeight() / 2 + Memory.boundOfSinglePlayerText.height() * 2 - Memory.boundOfMultiPlayerText.height() / 2 && m.getY() <= getHeight() / 2 + Memory.boundOfSinglePlayerText.height() * 2 + Memory.boundOfMultiPlayerText.height() / 2 && m.getX() <= getWidth() / 2 + Memory.boundOfMultiPlayerText.width() / 2 && m.getX() >= getWidth() / 2 - Memory.boundOfMultiPlayerText.width() / 2) {
					Memory.viewMode = ViewMode.MultiRoom;
				}
				if (m.getY() <= 100 && m.getX() <= 100)
					Memory.viewMode = ViewMode.SettignsPage;
				break;
			case MultiRoom:
				if (m.getActionMasked() == MotionEvent.ACTION_UP && m.getY() >= getHeight() / 2 - Memory.boundOfFastGame.height() / 2 && m.getY() <= getHeight() / 2 + Memory.boundOfFastGame.height() / 2 && m.getX() >= getWidth() / 2 - Memory.boundOfFastGame.width() / 2 && m.getX() <= getWidth() / 2 + Memory.boundOfFastGame.width() / 2) {
					new Thread(() -> {
						Memory.viewMode = ViewMode.Connecting;
						Memory.currentState = State.Ready;
						Multiplayer.sendState();
						boolean connection = Multiplayer.getConfirm();
						while (true) {
							if (connection) {
								Memory.viewMode = ViewMode.MultiGamePage;
								break;
							}
						}
						Multiplayer.getData();
					}).start();
				}
				break;
			case PausePage:
				if (m.getActionMasked() == MotionEvent.ACTION_UP && m.getY() >= getHeight() / 2 - Memory.boundOfSinglePlayerText.height() / 2 && m.getY() <= getHeight() / 2 + Memory.boundOfSinglePlayerText.height() / 2 && m.getX() >= getWidth() / 2 - Memory.boundOfSinglePlayerText.width() / 2 && m.getX() <= getWidth() / 2 + Memory.boundOfSinglePlayerText.width() / 2)
					Memory.viewMode = ViewMode.SingleRoom;
				break;
			case SingleRoom:
				switch (m.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
						x1 = m.getX();
						y1 = m.getY();
						break;
					case MotionEvent.ACTION_UP:
						if (y1 <= 50 + Memory.boundOfSinglePlayerText.height() && x1 <= 50 + Memory.boundOfSinglePlayerText.width())
							Memory.viewMode = ViewMode.PausePage;
						float v1 = m.getX() - x1, v2 = m.getY() - y1;
						if (Math.abs(v1) > Math.abs(v2)) {
							if (v1 != 0 && (Memory.snake.direction == Direction.Up || Memory.snake.direction == Direction.Down))
								Memory.snake.direction = v1 > 0 ? Direction.Right : Direction.Left;
						} else if (v2 != 0 && (Memory.snake.direction == Direction.Left || Memory.snake.direction == Direction.Right))
							Memory.snake.direction = v2 > 0 ? Direction.Down : Direction.Up;
						break;
				}
				break;
			case MultiGamePage:
				switch (m.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
						x1 = m.getX();
						y1 = m.getY();
						break;
					case MotionEvent.ACTION_UP:
						if (y1 <= 50 + Memory.boundOfSinglePlayerText.height() && x1 <= 50 + Memory.boundOfSinglePlayerText.width())
							Memory.viewMode = ViewMode.PausePage;
						float v1 = m.getX() - x1, v2 = m.getY() - y1;
						if (Math.abs(v1) > Math.abs(v2)) {
							if (v1 != 0 && (Memory.snake.direction == Direction.Up || Memory.snake.direction == Direction.Down)) {
								Memory.snake.direction = v1 > 0 ? Direction.Right : Direction.Left;
								Memory.snake.directionNumber = v1 > 0 ? (byte) 1 : (byte) 3;
							}
						} else if (v2 != 0 && (Memory.snake.direction == Direction.Left || Memory.snake.direction == Direction.Right)) {
							Memory.snake.direction = v2 > 0 ? Direction.Down : Direction.Up;
							Memory.snake.directionNumber = v2 > 0 ? (byte) 2 : (byte) 0;
						}
						new Thread(Multiplayer::sendDirection).start();
						break;
				}
				break;
			case LosePage:
				if (m.getActionMasked() == MotionEvent.ACTION_UP)
					Memory.viewMode = ViewMode.PreStart;
				break;
			case SettignsPage:
				int cube_color_width = getWidth() / 30, cube_color_height = getHeight() / 36;
				int x = getWidth() - cube_color_width * 8, y = (getHeight() - (cube_color_height * 23 + cube_color_width)) / 2;
				if (m.getActionMasked() == MotionEvent.ACTION_MOVE) {
					if (m.getX() > x && m.getY() > y && m.getX() < x + cube_color_width * 2 && m.getY() < y + cube_color_height * 23 + cube_color_width) {
						selected_color = (int) ((m.getY() - y) / (cube_color_height * 23 + cube_color_width) * 24);
					}
					if (m.getX() > x + cube_color_width * 2 && m.getY() > y && m.getY() < y + cube_color_height * 23 + cube_color_width) {
						selected_brightness = (int) ((m.getY() - y) / (cube_color_height * 23 + cube_color_width) * 24);
					}
				}
				break;
		}
		return true;
	}

	Paint paint = new Paint(), paint_stroke = new Paint();

	int selected_color = 10, selected_brightness = 16;

	int brightness() {
		return (int) (21.25f * selected_brightness - 255);
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.BLACK);
		switch (Memory.viewMode) {
			case PreStart:
				Memory.cellSize = Memory.nod(getWidth(), getHeight()) / 4;
				Memory.cellCountWidth = (byte) (getWidth() / Memory.cellSize);
				Memory.cellCountHeight = (byte) (getHeight() / Memory.cellSize);
				Memory.apple.random();
				Memory.snake.random();
				Memory.paint_text.setTypeface(Typeface.createFromAsset(getContext().getResources().getAssets(), "pixel_sans.ttf"));
				paint_stroke.setStyle(Paint.Style.STROKE);
				paint_stroke.setStrokeCap(Paint.Cap.ROUND);
				paint_stroke.setColor(Color.WHITE);
				paint_stroke.setStrokeWidth(8);
				Memory.viewMode = ViewMode.Menu;
				break;
			case Menu:
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.single_player_mode), getWidth() / 2, getHeight() / 2, TextScale.Normal, Color.WHITE, Memory.boundOfSinglePlayerText);
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.multi_player_mode), getWidth() / 2, getHeight() / 2 + Memory.boundOfSinglePlayerText.height() * 2, TextScale.Small, Color.WHITE, Memory.boundOfMultiPlayerText);
				break;
			case PausePage:
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.continue_game), getWidth() / 2, getHeight() / 2, TextScale.Normal, Color.WHITE, Memory.boundOfSinglePlayerText);
				break;
			case SingleRoom:
				Memory.snake.onDraw(canvas);
				Memory.apple.onDraw(canvas);
				Memory.DrawText(canvas, String.valueOf(Memory.snake.cells.size()), 50, 50, TextScale.Small, Color.YELLOW, Memory.boundOfSinglePlayerText);
				break;
			case MultiRoom:
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.fast_game), getWidth() / 2, getHeight() / 2, TextScale.Normal, Color.WHITE, Memory.boundOfFastGame);
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.list_of_rooms), getWidth() / 2, getHeight() / 2 + Memory.boundOfFastGame.height() * 2, TextScale.Small, Color.WHITE, Memory.boundOfListRoom);
				break;
			case Connecting:
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.wait_connection), getWidth() / 2, getHeight() / 2, TextScale.Normal, Color.WHITE);
				break;
			case MultiGamePage:
				Memory.snake.onDraw(canvas);
				Memory.snakeEnemy.onDraw(canvas);
				Memory.apple.onDraw(canvas);
				Memory.DrawText(canvas, String.valueOf(Memory.snake.cells.size()), 50, 50, TextScale.Small, Color.YELLOW, Memory.boundOfSinglePlayerText);
				break;
			case LosePage:
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.you_lose), getWidth() / 2, getHeight() / 2, TextScale.Normal, Color.WHITE, Memory.boundOfSinglePlayerText);
				Memory.DrawText(canvas, getContext().getResources().getString(R.string.your_score) + Memory.snake.cells.size(), getWidth() / 2, getHeight() / 2 + Memory.boundOfSinglePlayerText.height() * 2, TextScale.Small, Color.WHITE);
				break;
			case SettignsPage:
				int cube_color_width = getWidth() / 30, cube_color_height = getHeight() / 36, gray;
				int r, g, b, offset = 0, x = getWidth() - cube_color_width * 8, y = (getHeight() - (cube_color_height * 23 + cube_color_width)) / 2;
				for (int i = 0; i < 8; i++) {
					r = 255 - i * 32;
					g = i * 32;
					b = brightness() < 0 ? 0 : brightness();
					r = r + brightness() > 255 ? 255 : r + brightness() < 0 ? 0 : r + brightness();
					g = g + brightness() > 255 ? 255 : g + brightness() < 0 ? 0 : g + brightness();
					paint.setColor(Color.rgb(r, g, b));
					if (i == selected_color) {
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_width, paint);
						Memory.snake.paint.setColor(paint.getColor());
						Memory.dummy.paint.setColor(paint.getColor());
						offset += cube_color_width - cube_color_height;
					} else
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_height, paint);
					offset += cube_color_height;
				}
				for (int i = 8; i < 16; i++) {
					r = brightness() < 0 ? 0 : brightness();
					g = 255 - (i - 8) * 32;
					b = (i - 8) * 32;
					b = b + brightness() > 255 ? 255 : b + brightness() < 0 ? 0 : b + brightness();
					g = g + brightness() > 255 ? 255 : g + brightness() < 0 ? 0 : g + brightness();
					paint.setColor(Color.rgb(r, g, b));
					if (i == selected_color) {
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_width, paint);
						Memory.snake.paint.setColor(paint.getColor());
						Memory.dummy.paint.setColor(paint.getColor());
						offset += cube_color_width - cube_color_height;
					} else
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_height, paint);
					offset += cube_color_height;
				}
				for (int i = 16; i < 24; i++) {
					r = (i - 16) * 32;
					g = brightness() < 0 ? 0 : brightness();
					b = 255 - (i - 16) * 32;
					b = b + brightness() > 255 ? 255 : b + brightness() < 0 ? 0 : b + brightness();
					r = r + brightness() > 255 ? 255 : r + brightness() < 0 ? 0 : r + brightness();
					paint.setColor(Color.rgb(r, g, b));
					if (i == selected_color) {
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_width, paint);
						Memory.snake.paint.setColor(paint.getColor());
						Memory.dummy.paint.setColor(paint.getColor());
						offset += cube_color_width - cube_color_height;
					} else
						canvas.drawRect(x, y + offset, x + cube_color_width, y + offset + cube_color_height, paint);
					offset += cube_color_height;
				}
				offset = 0;
				for (int i = 0; i < 24; i++) {
					gray = (int) (21.25f * i) - 255;
					if (selected_color < 8) {
						r = 255 - selected_color * 32;
						g = selected_color * 32;
						b = gray < 0 ? 0 : gray;
					} else if (selected_color < 16) {
						r = gray < 0 ? 0 : gray;
						g = 255 - (selected_color - 8) * 32;
						b = (selected_color - 8) * 32;
					} else {
						r = (selected_color - 16) * 32;
						g = gray < 0 ? 0 : gray;
						b = 255 - (selected_color - 16) * 32;
					}
					r = r + gray > 255 ? 255 : r + gray < 0 ? 0 : r + gray;
					g = g + gray > 255 ? 255 : g + gray < 0 ? 0 : g + gray;
					b = b + gray > 255 ? 255 : b + gray < 0 ? 0 : b + gray;
					paint.setColor(Color.rgb(r, g, b));
					if (i == selected_brightness) {
						canvas.drawRect(x + cube_color_width * 2, y + offset, x + cube_color_width + cube_color_width * 2, y + offset + cube_color_width, paint);
						offset += cube_color_width - cube_color_height;
					} else {
						canvas.drawRect(x + cube_color_width * 2, y + offset, x + cube_color_width + cube_color_width * 2, y + offset + cube_color_height, paint);
					}
					offset += cube_color_height;
				}
				paint_stroke.setColor(Color.BLACK);
				canvas.drawRect(x + cube_color_width * 2 + 2, y + selected_brightness * cube_color_height + 2, x + cube_color_width + cube_color_width * 2 - 2, y + selected_brightness * cube_color_height + cube_color_width - 2, paint_stroke);
				canvas.drawRect(x + 2, y + selected_color * cube_color_height + 2, x + cube_color_width - 2, y + selected_color * cube_color_height + cube_color_width - 2, paint_stroke);
				paint_stroke.setColor(Color.WHITE);
				canvas.drawRect(x + cube_color_width * 2, y + selected_brightness * cube_color_height, x + cube_color_width + cube_color_width * 2, y + selected_brightness * cube_color_height + cube_color_width, paint_stroke);
				canvas.drawRect(x, y + selected_color * cube_color_height, x + cube_color_width, y + selected_color * cube_color_height + cube_color_width, paint_stroke);
				Memory.dummy.onDraw(canvas);
				break;
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
		new Thread(Multiplayer::sendApplePosition).start();
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
	Paint paint = new Paint();
	Direction direction;
	byte directionNumber;
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
		new Thread(Multiplayer::sendDirection).start();
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
				cells.add(0, up());
				break;
			case Right:
				cells.add(0, right());
				break;
			case Down:
				cells.add(0, down());
				break;
			case Left:
				cells.add(0, left());
				break;
		}
		if (cells.get(0).x != Memory.apple.position.x || cells.get(0).y != Memory.apple.position.y) {
			cells.remove(cells.size() - 1);
		} else {
			Memory.apple.random();
		}
		for (int i = 0; i < cells.size(); i++) {
			if (i != 0 && cells.get(0).equals(cells.get(i)))
				Memory.viewMode = ViewMode.LosePage;
			canvas.drawRect(cells.get(i).x * Memory.cellSize, cells.get(i).y * Memory.cellSize, (cells.get(i).x + 1) * Memory.cellSize, (cells.get(i).y + 1) * Memory.cellSize, paint);
		}
	}
}

class SnakeDummy {
	Paint paint = new Paint();
	private Paint borderPaint = new Paint();
	private Direction direction;
	private ArrayList<Point> cells = new ArrayList<>();

	private Point position;

	private int index = 0;

	SnakeDummy(int x, int y) {
		position = new Point((byte) x, (byte) y);
		for (int i = 0; i < 8; i++)
			cells.add(new Point((byte) (x + 12), (byte) (y + 3)));
		direction = Direction.Left;
		borderPaint.setColor(Color.WHITE);
	}

	private Point left() {
		return new Point((byte) (cells.get(0).x - 1 >= position.x ? cells.get(0).x - 1 : cells.get(0).x + 12), cells.get(0).y);
	}

	private Point down() {
		return new Point(cells.get(0).x, (byte) (cells.get(0).y + 1));
	}

	private Point up() {
		return new Point(cells.get(0).x, (byte) (cells.get(0).y - 1));
	}

	void onDraw(Canvas canvas) {
		switch (direction) {
			case Up:
				cells.add(0, up());
				break;
			case Down:
				cells.add(0, down());
				break;
			case Left:
				cells.add(0, left());
				break;
		}
		cells.remove(cells.size() - 1);
		for (int i = 0; i < cells.size(); i++)
			canvas.drawRect(cells.get(i).x * Memory.cellSize, cells.get(i).y * Memory.cellSize, (cells.get(i).x + 1) * Memory.cellSize, (cells.get(i).y + 1) * Memory.cellSize, paint);
		switch (++index) {
			case 2:
				direction = Direction.Up;
				break;
			case 5:
				direction = Direction.Left;
				break;
			case 8:
				direction = Direction.Down;
				break;
			case 11:
				direction = Direction.Left;
				break;
			case 14:
				direction = Direction.Up;
				break;
			case 17:
				direction = Direction.Left;
				break;
			case 20:
				direction = Direction.Down;
				break;
			case 23:
				direction = Direction.Left;
				break;
			case 24:
				index = 0;
				break;
		}
		canvas.drawLine(position.x * Memory.cellSize, position.y * Memory.cellSize - 5, position.x * Memory.cellSize, (position.y + 4) * Memory.cellSize + 5, borderPaint);
		canvas.drawLine((position.x + 13) * Memory.cellSize, position.y * Memory.cellSize - 5, (position.x + 13) * Memory.cellSize, (position.y + 4) * Memory.cellSize + 5, borderPaint);
	}
}