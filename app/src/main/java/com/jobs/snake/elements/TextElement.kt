package com.jobs.snake.elements

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.jobs.snake.Memory
import com.jobs.snake.ext.Drawer
import com.jobs.snake.ext.TextScale

open class TextElement : Element, Drawer {
	var text: String
		set(value) {
			if (value != field) {
				field = value
				paint.getTextBounds(value, 0, value.length, bound)
			}
		}

	private val paint = Paint()

	private var heightScreen = 0
		set(value) {
			if (field != value) {
				field = value
				paint.textSize = value.toFloat() / textScale.value
				paint.getTextBounds(text, 0, text.length, bound)
			}
		}

	var color
		get() = paint.color
		set(value) {
			paint.color = value
		}

	var textScale = TextScale.Normal
		set(value) {
			if (field != value) {
				field = value
				paint.textSize = heightScreen.toFloat() / value.value
				paint.getTextBounds(text, 0, text.length, bound)
			}
		}

	constructor(text: String) {
		this.text = text
	}

	init {
		paint.color = Color.WHITE
		paint.typeface = Memory.font
	}

	override fun onDraw(canvas: Canvas) {
		heightScreen = canvas.height
		canvas.drawText(text, position.x - bound.width() / 2f, position.y + bound.height() / 2f, paint)
	}

	override fun isEntry(x: Float, y: Float) = x >= position.x - bound.width() / 2f && y >= position.y - bound.height() / 2f && x <= position.x + bound.width() / 2f && y <= position.y + bound.height() / 2f
}