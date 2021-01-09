package com.jobs.snake.ext

import android.graphics.Canvas
import com.jobs.snake.components.GameView

abstract class Screen(val gameView: GameView) : Drawer, Touchable {
	private var canvas: Canvas? = null
	override fun onDraw(canvas: Canvas) {
		this.canvas = canvas
	}

	open fun finish() = gameView.finish(this)

	val width
		get() = if (canvas != null) canvas!!.width else 0
	val height
		get() = if (canvas != null) canvas!!.height else 0
}