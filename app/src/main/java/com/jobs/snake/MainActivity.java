package com.jobs.snake;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;

import com.jobs.snake.components.GameView;
import com.jobs.snake.screens.MenuScreen;

import java.util.Random;

//  Стандартное Activity
public class MainActivity extends Activity {

	private GameView gameView;

	//  Создание Activity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Memory.init(this);

		//  Скрытие SystemUI
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		//  Проверяем первичный запуск, если запуск не в первый раз, то пропускаем страницу рассчётов
		if (Memory.viewMode != ViewMode.FirstStart && Memory.viewMode != ViewMode.PreStart)
			Memory.viewMode = ViewMode.Loading;

		(gameView = new GameView(this)).pushScreen(new MenuScreen(gameView));

		//  Помещаем холст на Activity
		setContentView(gameView);
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
		gameView.getActiveScreen().finish();
	}
}

