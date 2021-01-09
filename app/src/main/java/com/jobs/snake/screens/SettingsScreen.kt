package com.jobs.snake.screens

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import com.jobs.snake.Memory
import com.jobs.snake.R
import com.jobs.snake.components.GameView
import com.jobs.snake.elements.ButtonElement
import com.jobs.snake.ext.Screen
import com.jobs.snake.ext.TextScale

class SettingsScreen(gameView: GameView) : Screen(gameView) {
	internal enum class SliderClick {
		None,
		Color,
		Brightness,
		Speed
	}

	private var sliderClick = SliderClick.None

	//	Кисти для настроек
	private var paint = Paint()
	private var paint_stroke = Paint()
	private val backButton: ButtonElement = object : ButtonElement("<-", Runnable { finish() }) {
		override fun isEntry(x: Float, y: Float): Boolean {
			return x < 100 && y < 100
		}
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		canvas.drawColor(Color.BLACK)
		paint.color = Color.WHITE
		//	Отрисовка кнопки назад
		backButton.onDraw(canvas)

		//	Рассчёт ширины и высоты прямоугольников выбора цвета и яркости
		val cube_color_width = width / 30
		val cube_color_height = height / 36
		var gray: Int

		//	Рассчёт позиции и цветов
		var r: Int
		var g: Int
		var b: Int
		var offset = 0
		val x = width - cube_color_width * 8
		val y = (height - (cube_color_height * 23 + cube_color_width)) / 2
		canvas.drawRect(x - cube_color_width / 4f, y - cube_color_width / 4f, x + cube_color_width * 1.25f, (y + cube_color_height).toFloat(), paint)
		canvas.drawRect(x - cube_color_width / 4f + cube_color_width * 2, y - cube_color_width / 4f, x + cube_color_width * 1.25f + cube_color_width * 2, (y + cube_color_height).toFloat(), paint)
		val left = x - cube_color_width / 4f + cube_color_width * 4
		canvas.drawRect(left, y - cube_color_width / 4f, x + cube_color_width * 1.25f + cube_color_width * 4, (y + cube_color_height).toFloat(), paint)
		canvas.drawRect(x - cube_color_width / 4f, (y + cube_color_height * 24).toFloat(), x + cube_color_width * 1.25f, y + cube_color_height * 25 + cube_color_width / 4f, paint)
		canvas.drawRect(x - cube_color_width / 4f + cube_color_width * 2, (y + cube_color_height * 24).toFloat(), x + cube_color_width * 1.25f + cube_color_width * 2, y + cube_color_height * 25 + cube_color_width / 4f, paint)
		canvas.drawRect(left, (y + cube_color_height * 24).toFloat(), x + cube_color_width * 1.25f + cube_color_width * 4, y + cube_color_height * 25 + cube_color_width / 4f, paint)

		//	Отрисовка RED to GREEN
		for (i in 0..7) {

			//	Рассчёт цвета ячейки
			r = 255 - i * 32
			g = i * 32
			b = Math.max(brightness(), 0)
			r = if (r + brightness() > 255) 255 else Math.max(r + brightness(), 0)
			g = if (g + brightness() > 255) 255 else Math.max(g + brightness(), 0)

			//	Приминение цвета к кисти
			paint.color = Color.rgb(r, g, b)

			//	Если текущая ячейка является выделенным цветом, то отрисовываем её квадратной и применяем цвета к змейям, иначе отрисовываем обычную ячейку
			if (i == Memory.selected_color) {
				canvas.drawRect(x.toFloat(), (y + offset).toFloat(), (x + cube_color_width).toFloat(), (y + offset + cube_color_width).toFloat(), paint)
				Memory.snake.paint.color = paint.color
				Memory.dummy.paint.color = paint.color
				offset += cube_color_width - cube_color_height
			} else canvas.drawRect(x.toFloat(), (y + offset).toFloat(), (x + cube_color_width).toFloat(), (y + offset + cube_color_height).toFloat(), paint)

			//	Приминяем смещение толщиной в текущую ячейку
			offset += cube_color_height
		}

		//	Отрисовка GREEN to BLUE
		for (i in 8..15) {

			//	Рассчёт цвета ячейки
			r = Math.max(brightness(), 0)
			g = 255 - (i - 8) * 32
			b = (i - 8) * 32
			b = if (b + brightness() > 255) 255 else Math.max(b + brightness(), 0)
			g = if (g + brightness() > 255) 255 else Math.max(g + brightness(), 0)

			//	Приминение цвета к кисти
			paint.color = Color.rgb(r, g, b)

			//	Если текущая ячейка является выделенным цветом, то отрисовываем её квадратной и применяем цвета к змейям, иначе отрисовываем обычную ячейку
			if (i == Memory.selected_color) {
				canvas.drawRect(x.toFloat(), (y + offset).toFloat(), (x + cube_color_width).toFloat(), (y + offset + cube_color_width).toFloat(), paint)
				Memory.snake.paint.color = paint.color
				Memory.dummy.paint.color = paint.color
				offset += cube_color_width - cube_color_height
			} else canvas.drawRect(x.toFloat(), (y + offset).toFloat(), (x + cube_color_width).toFloat(), (y + offset + cube_color_height).toFloat(), paint)

			//	Приминяем смещение толщиной в текущую ячейку
			offset += cube_color_height
		}

		//	Отрисовка BLUE to RED
		for (i in 16..23) {

			//	Рассчёт цвета ячейки
			r = (i - 16) * 32
			g = Math.max(brightness(), 0)
			b = 255 - (i - 16) * 32
			b = if (b + brightness() > 255) 255 else Math.max(b + brightness(), 0)
			r = if (r + brightness() > 255) 255 else Math.max(r + brightness(), 0)

			//	Приминение цвета к кисти
			paint.color = Color.rgb(r, g, b)

			//	Если текущая ячейка является выделенным цветом, то отрисовываем её квадратной и применяем цвета к змейям, иначе отрисовываем обычную ячейку
			if (i == Memory.selected_color) {
				canvas.drawRect(x.toFloat(), (y + offset).toFloat(), (x + cube_color_width).toFloat(), (y + offset + cube_color_width).toFloat(), paint)
				Memory.snake.paint.color = paint.color
				Memory.dummy.paint.color = paint.color
				offset += cube_color_width - cube_color_height
			} else canvas.drawRect(x.toFloat(), (y + offset).toFloat(), (x + cube_color_width).toFloat(), (y + offset + cube_color_height).toFloat(), paint)

			//	Приминяем смещение толщиной в текущую ячейку
			offset += cube_color_height
		}

		//	Обнуляем смещение, для того что бы отрисовывать слайдер яркости с начала
		offset = 0

		//	Отрисовка яркости (DARK to LIGHT)
		for (i in 0..23) {

			//	Рассчёт яркости
			gray = 10 * i - 57

			//	Рассчёт цвета ячейки
			if (Memory.selected_color < 8) {
				r = 255 - Memory.selected_color * 32
				g = Memory.selected_color * 32
				b = Math.max(gray, 0)
			} else if (Memory.selected_color < 16) {
				r = Math.max(gray, 0)
				g = 255 - (Memory.selected_color - 8) * 32
				b = (Memory.selected_color - 8) * 32
			} else {
				r = (Memory.selected_color - 16) * 32
				g = Math.max(gray, 0)
				b = 255 - (Memory.selected_color - 16) * 32
			}
			r = if (r + gray > 255) 255 else Math.max(r + gray, 0)
			g = if (g + gray > 255) 255 else Math.max(g + gray, 0)
			b = if (b + gray > 255) 255 else Math.max(b + gray, 0)

			//	Приминение цвета к кисти
			paint.color = Color.rgb(r, g, b)

			//	Если текущая ячейка является выделенной, то отрисовываем её квадратной, иначе отрисовываем обычную ячейку
			if (i == Memory.selected_brightness) {
				canvas.drawRect((x + cube_color_width * 2).toFloat(), (y + offset).toFloat(), (x + cube_color_width + cube_color_width * 2).toFloat(), (y + offset + cube_color_width).toFloat(), paint)
				offset += cube_color_width - cube_color_height
			} else canvas.drawRect((x + cube_color_width * 2).toFloat(), (y + offset).toFloat(), (x + cube_color_width + cube_color_width * 2).toFloat(), (y + offset + cube_color_height).toFloat(), paint)

			//	Приминяем смещение толщиной в текущую ячейку
			offset += cube_color_height
		}
		offset = 0
		for (i in 0..23) {

			//	Рассчёт цвета ячейки
			r = (255 - i * 10.66666f).toInt()
			g = (i * 10.66666f).toInt()
			b = 85
			r = if (r + 85 > 255) 255 else Math.max(r + 85, 0)
			g = if (g + 85 > 255) 255 else Math.max(g + 85, 0)

			//	Приминение цвета к кисти
			paint.color = Color.rgb(r, g, b)

			//	Если текущая ячейка является выделенным цветом, то отрисовываем её квадратной и применяем цвета к змейям, иначе отрисовываем обычную ячейку
			if (i == Memory.speed) {
				canvas.drawRect((x + cube_color_width * 4).toFloat(), (y + offset).toFloat(), (x + cube_color_width * 5).toFloat(), (y + offset + cube_color_width).toFloat(), paint)
				offset += cube_color_width - cube_color_height
			} else canvas.drawRect((x + cube_color_width * 4).toFloat(), (y + offset).toFloat(), (x + cube_color_width * 5).toFloat(), (y + offset + cube_color_height).toFloat(), paint)

			//	Приминяем смещение толщиной в текущую ячейку
			offset += cube_color_height
		}

		//	Отрисовываем обводку для выделенных ячеек
		paint_stroke.color = Color.BLACK
		canvas.drawRect((x + cube_color_width * 2 + 2).toFloat(), (y + Memory.selected_brightness * cube_color_height + 2).toFloat(), (x + cube_color_width + cube_color_width * 2 - 2).toFloat(), (y + Memory.selected_brightness * cube_color_height + cube_color_width - 2).toFloat(), paint_stroke)
		canvas.drawRect((x + 2).toFloat(), (y + Memory.selected_color * cube_color_height + 2).toFloat(), (x + cube_color_width - 2).toFloat(), (y + Memory.selected_color * cube_color_height + cube_color_width - 2).toFloat(), paint_stroke)
		canvas.drawRect((x + cube_color_width * 4 + 2).toFloat(), (y + Memory.speed * cube_color_height + 2).toFloat(), (x + cube_color_width + cube_color_width * 4 - 2).toFloat(), (y + Memory.speed * cube_color_height + cube_color_width - 2).toFloat(), paint_stroke)
		paint_stroke.color = Color.WHITE
		canvas.drawRect((x + cube_color_width * 2).toFloat(), (y + Memory.selected_brightness * cube_color_height).toFloat(), (x + cube_color_width + cube_color_width * 2).toFloat(), (y + Memory.selected_brightness * cube_color_height + cube_color_width).toFloat(), paint_stroke)
		canvas.drawRect(x.toFloat(), (y + Memory.selected_color * cube_color_height).toFloat(), (x + cube_color_width).toFloat(), (y + Memory.selected_color * cube_color_height + cube_color_width).toFloat(), paint_stroke)
		canvas.drawRect((x + cube_color_width * 4).toFloat(), (y + Memory.speed * cube_color_height).toFloat(), (x + cube_color_width + cube_color_width * 4).toFloat(), (y + Memory.speed * cube_color_height + cube_color_width).toFloat(), paint_stroke)
		Memory.DrawText(canvas, gameView.context.resources.getString(R.string.color_abbreviated), x + cube_color_width / 2, y - cube_color_height * 2, TextScale.Small, Color.WHITE)
		Memory.DrawText(canvas, gameView.context.resources.getString(R.string.brightness_abbreviated), x + cube_color_width / 2 + cube_color_width * 2, y - cube_color_height * 2, TextScale.Small, Color.WHITE)
		Memory.DrawText(canvas, gameView.context.resources.getString(R.string.speed_abbreviated), x + cube_color_width / 2 + cube_color_width * 4, y - cube_color_height * 2, TextScale.Small, Color.WHITE)
		when (sliderClick) {
			SliderClick.Color -> Memory.DrawText(canvas, gameView.context.resources.getString(R.string.color), x + cube_color_width / 2, y + cube_color_height * 27, TextScale.Small, Color.WHITE)
			SliderClick.Brightness -> Memory.DrawText(canvas, gameView.context.resources.getString(R.string.brightness), x + cube_color_width / 2 + cube_color_width * 2, y + cube_color_height * 27, TextScale.Small, Color.WHITE)
			SliderClick.Speed -> Memory.DrawText(canvas, gameView.context.resources.getString(R.string.speed), x + cube_color_width / 2 + cube_color_width * 4, y + cube_color_height * 27, TextScale.Small, Color.WHITE)
		}
		Memory.DrawText(canvas, gameView.context.resources.getString(R.string.preview), width / 3, height / 2, TextScale.Small, Color.WHITE)
		Memory.DrawText(canvas, gameView.context.resources.getString(R.string.apple), width / 5, height * 3 / 5, TextScale.Small, Color.WHITE)
		Memory.DrawText(canvas, gameView.context.resources.getString(R.string.snake), width / 2, height * 3 / 5, TextScale.Small, Color.WHITE)
		Memory.DrawText(canvas, "Screen size: " + Memory.cellCountWidth + "x" + Memory.cellCountHeight, width / 3, height / 3, TextScale.Small, Color.WHITE)
		//	Отрисовываем манекен
		Memory.dummy.onDraw(canvas)
	}

	override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
		backButton.onTouchEvent(motionEvent);
		//	Рассчитываем ширину и высоту прямоугольника
		val cube_color_width = width / 30
		val cube_color_height = height / 36

		//	Рассчитываем позицию слайдеров выбора цвета на экране
		val x = width - cube_color_width * 8
		val y = (height - (cube_color_height * 23 + cube_color_width)) / 2
		when (motionEvent.actionMasked) {
			MotionEvent.ACTION_UP -> sliderClick = SliderClick.None
			MotionEvent.ACTION_DOWN -> if (motionEvent.x > x && motionEvent.y > y && motionEvent.x < x + cube_color_width && motionEvent.y < y + cube_color_height * 23 + cube_color_width) sliderClick = SliderClick.Color else if (motionEvent.x > x + cube_color_width * 2 && motionEvent.x < x + cube_color_width * 3 && motionEvent.y > y && motionEvent.y < y + cube_color_height * 23 + cube_color_width) sliderClick = SliderClick.Brightness else if (motionEvent.x > x + cube_color_width * 4 && motionEvent.x < x + cube_color_width * 5 && motionEvent.y > y && motionEvent.y < y + cube_color_height * 23 + cube_color_width) sliderClick = SliderClick.Speed
			MotionEvent.ACTION_MOVE -> if (motionEvent.y > y && motionEvent.y < y + cube_color_height * 23 + cube_color_width) when (sliderClick) {
				SliderClick.Color -> Memory.selected_color = ((motionEvent.y - y) / (cube_color_height * 23 + cube_color_width) * 24).toInt()
				SliderClick.Brightness -> Memory.selected_brightness = ((motionEvent.y - y) / (cube_color_height * 23 + cube_color_width) * 24).toInt()
				SliderClick.Speed -> Memory.speed = ((motionEvent.y - y) / (cube_color_height * 23 + cube_color_width) * 24).toInt()
			}
		}
		return false
	}

	private fun brightness() = 10 * Memory.selected_brightness - 57

	init {
		paint_stroke.style = Paint.Style.STROKE
		paint_stroke.color = Color.WHITE
		paint_stroke.strokeWidth = 8f
		Memory.dummy.setPosition((Memory.cellCountWidth / 2 - 6).toByte(), (Memory.cellCountHeight * 3 / 4).toByte())
		backButton.setPosition(50, 50)
		backButton.textScale = TextScale.Small
		backButton.color = Color.YELLOW
	}
}