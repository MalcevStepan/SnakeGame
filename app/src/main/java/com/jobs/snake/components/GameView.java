package com.jobs.snake.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.jobs.snake.Memory;
import com.jobs.snake.ext.Screen;

import java.util.Stack;

public class GameView extends View {

	private boolean firstOpen = true;

	@NonNull
	private final Stack<Screen> screens = new Stack<>();

	@NonNull
	private final Screen firstScreen = new Screen(this) {
		@Override
		public boolean onTouchEvent(@NonNull MotionEvent motionEvent) {
			return false;
		}

		@Override
		public void onDraw(final @NonNull Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawColor(Color.BLACK);
		}

		@Override
		public void finish() {
			((Activity) getContext()).finish();
		}
	};

	public Screen getActiveScreen() {
		return screens.size() > 0 ? screens.lastElement() : firstScreen;
	}

	public GameView(final @NonNull Context context) {
		super(context);
	}

	public void pushScreen(final @NonNull Screen screen) {
		screens.push(screen);
	}

	public void finish(final @NonNull Screen item) {
		screens.remove(item);
	}

	public void finish() {
		screens.pop();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(final @NonNull MotionEvent motionEvent) {
		super.onTouchEvent(motionEvent);
		getActiveScreen().onTouchEvent(motionEvent);
		return true;
	}

	float endTime = System.nanoTime();

	//	Отрисовка
	public void onDraw(final @NonNull Canvas canvas) {
		float startTime = endTime;
		endTime = System.nanoTime();

		Memory.deltaTime = (endTime - startTime) / 16000000;

		super.onDraw(canvas);
		if (firstOpen) {
			Memory.cellSize = Memory.nod(getWidth(), getHeight());
			Memory.cellSize /= Math.abs(36 - getHeight() / Memory.cellSize / 4) < Math.abs(36 - getHeight() / Memory.cellSize / 8) ? 4 : Math.abs(36 - getHeight() / Memory.cellSize / 8) < Math.abs(36 - getHeight() / Memory.cellSize / 12) ? 8 : 12;
			Memory.cellCountWidth = (byte) (getWidth() / Memory.cellSize);
			Memory.cellCountHeight = (byte) (getHeight() / Memory.cellSize);
			firstOpen = false;
		}
		getActiveScreen().onDraw(canvas);
		invalidate();
	}
}