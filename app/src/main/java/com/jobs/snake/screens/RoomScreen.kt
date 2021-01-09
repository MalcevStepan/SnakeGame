package com.jobs.snake.screens

import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import com.jobs.snake.Memory
import com.jobs.snake.components.GameView
import com.jobs.snake.elements.ButtonElement
import com.jobs.snake.elements.GestureElement
import com.jobs.snake.ext.Direction
import com.jobs.snake.ext.Screen
import com.jobs.snake.ext.TextScale
import kotlin.math.abs

class RoomScreen(gameView: GameView) : Screen(gameView) {
	private val gestureElement = GestureElement()
	private val pauseButton = object : ButtonElement("*", Runnable { finish() }) {
		override fun isEntry(x: Float, y: Float): Boolean {
			return x < 100 && y < 100
		}
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		canvas.drawColor(Color.BLACK)
		Memory.snake.onDraw(canvas)

		//	Отрисовка яблока
		Memory.apple.onDraw(canvas)

		//	Отрисовка длинны змеи
		pauseButton.text = Memory.snake.cells.size.toString()
		pauseButton.onDraw(canvas)
		gestureElement.onDraw(canvas)
	}

	private var x1 = 0f
	private var y1 = 0f

	override fun onTouchEvent(motionEvent: MotionEvent): Boolean {

		when (motionEvent.actionMasked) {

			MotionEvent.ACTION_DOWN -> {
				x1 = motionEvent.x
				y1 = motionEvent.y
			}

			MotionEvent.ACTION_UP -> {
				val v1 = motionEvent.x - x1
				val v2 = motionEvent.y - y1

				//	Проверка по какой из осей растояние пройдено больше, в ту сторону и изменяем направление
				if (abs(v1) > abs(v2)) {
					if (v1 != 0f && (Memory.snake.direction == Direction.Up || Memory.snake.direction == Direction.Down)) {
						if (v1 > 0f) {
							Memory.snake.direction = Direction.Right
						} else {
							Memory.snake.direction = Direction.Left
						}
					}
				} else if (v2 != 0f && (Memory.snake.direction == Direction.Left || Memory.snake.direction == Direction.Right))
					if (v2 > 0f) {
						Memory.snake.direction = Direction.Down
					} else {
						Memory.snake.direction = Direction.Up
					}
			}
		}
		pauseButton.onTouchEvent(motionEvent)
		gestureElement.onTouchEvent(motionEvent)
		return false
	}

	override fun finish() = gameView.pushScreen(PauseScreen(gameView))

	init {
		Memory.snake.random()
		Memory.apple.random()
		pauseButton.setPosition(50, 50)
		pauseButton.textScale = TextScale.Small
		pauseButton.color = Color.YELLOW
	}
}