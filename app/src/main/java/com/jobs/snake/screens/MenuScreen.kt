package com.jobs.snake.screens

import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import com.jobs.snake.R
import com.jobs.snake.components.GameView
import com.jobs.snake.elements.ButtonElement
import com.jobs.snake.ext.Screen
import com.jobs.snake.ext.TextScale

class MenuScreen(gameView: GameView) : Screen(gameView) {
	private val singlePlayerButton: ButtonElement
	private val settingsButton: ButtonElement = object : ButtonElement("*", Runnable { gameView.pushScreen(SettingsScreen(gameView)) }) {
		override fun isEntry(x: Float, y: Float): Boolean {
			return x < 100 && y < 100
		}
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		canvas.drawColor(Color.BLACK)
		singlePlayerButton.setPosition(width / 2, height / 2)
		singlePlayerButton.onDraw(canvas)

		settingsButton.onDraw(canvas)
	}

	override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
		singlePlayerButton.onTouchEvent(motionEvent)
		settingsButton.onTouchEvent(motionEvent)
		return false
	}

	init {
		settingsButton.setPosition(50, 50)
		settingsButton.textScale = TextScale.Small
		settingsButton.color = Color.YELLOW
		singlePlayerButton = ButtonElement(gameView.context.resources.getString(R.string.single_player_mode)) { gameView.pushScreen(RoomScreen(gameView)) }
	}
}