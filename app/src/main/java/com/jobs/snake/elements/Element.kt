package com.jobs.snake.elements

import android.graphics.Rect
import com.jobs.snake.ext.Area
import com.jobs.snake.ext.Point

abstract class Element : Area {
	val position = Point()
	val bound = Rect()

	fun setPosition(x: Int, y: Int) = position.setPosition(x, y)

	override fun isEntry(x: Float, y: Float) = x >= position.x && y >= position.y && x <= position.x + bound.width() && y <= position.y + bound.height()
}