package com.jobs.snake.screens

import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import com.jobs.snake.R
import com.jobs.snake.components.GameView
import com.jobs.snake.elements.ButtonElement
import com.jobs.snake.ext.Screen
import com.jobs.snake.ext.TextScale

class PauseScreen(gameView: GameView) : Screen(gameView) {
	private val continueButton: ButtonElement
	private val exitButton: ButtonElement = object : ButtonElement("<-", Runnable { gameView.finish(); gameView.finish() }) {
		override fun isEntry(x: Float, y: Float): Boolean {
			return x < 100 && y < 100
		}
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		canvas.drawColor(Color.BLACK)
		continueButton.setPosition(width / 2, height / 2)
		continueButton.onDraw(canvas)

		exitButton.onDraw(canvas)
	}

	override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
		continueButton.onTouchEvent(motionEvent)
		exitButton.onTouchEvent(motionEvent)
		return false
	}

	init {
		exitButton.setPosition(50, 50)
		exitButton.textScale = TextScale.Small
		exitButton.color = Color.YELLOW
		continueButton = ButtonElement(gameView.context.resources.getString(R.string.continue_game)) { finish() }
	}
}