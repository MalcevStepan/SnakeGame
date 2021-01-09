package com.jobs.snake.elements

import android.graphics.*
import android.view.MotionEvent
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.jobs.snake.Memory
import com.jobs.snake.ext.Drawer
import com.jobs.snake.ext.Touchable
import com.jobs.snake.ext.Vector


open class GestureElement : Element(), Drawer, Touchable {
	private var x = 0f
	private var y = 0f

	private var xl = 0f
	private var yl = 0f

	private var isTouch = false
	private var segment: GestureSegment? = null

	private var saver: Bitmap? = null
	private var saverInvert: Bitmap? = null
	private var canvasSaver: Canvas? = null
	private var canvasSaverInvert: Canvas? = null

	private val paint = Paint()
	private val paintDst = Paint()
	private val color: Int

	init {
		val c = Memory.snake.paint.color
		color = Color.argb(15, c.red, c.green, c.blue)
		paint.color = Memory.snake.paint.color
		paint.strokeCap = Paint.Cap.ROUND
		paint.style = Paint.Style.STROKE
		paint.strokeWidth = 32f

		paintDst.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
	}

	override fun onDraw(canvas: Canvas) {
		if (saver == null) {
			saver = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
			canvasSaver = Canvas(saver!!)
		}

		if (saverInvert == null) {
			saverInvert = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
			canvasSaverInvert = Canvas(saverInvert!!)
		}

		if (isTouch && (xl != x || yl != y)) {
			if (segment != null) {
				val seg = GestureSegment(Vector(x, y), Vector(xl, yl), 32f)
				seg.oldSegment = segment
				segment = seg
			} else {
				segment = GestureSegment(Vector(x, y), Vector(xl, yl), 32f)
			}
			canvasSaver?.drawLine(x, y, xl, yl, paint)
		}
		canvasSaver?.drawColor(color, PorterDuff.Mode.DST_OUT)

		canvasSaverInvert?.drawBitmap(saver!!, 0f, 0f, null)
		//canvasSaverInvert?.drawColor(Memory.snake.paint.color)
		segment?.onDraw(canvasSaverInvert!!)
		if (segment?.discardStrokeSize() == false) {
			segment = null
		}
		canvasSaver?.drawBitmap(saverInvert!!, 0f, 0f, paintDst)
		canvas.drawBitmap(saver!!, 0f, 0f, null)
	}

	override fun isEntry(x: Float, y: Float) = true

	override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
		xl = x
		yl = y
		x = motionEvent.x
		y = motionEvent.y
		if (motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE != isTouch) {
			isTouch = motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE
			if (isTouch) {
				xl = x
				yl = y
			}
		}
		return true
	}

	class GestureSegment(private val start: Vector, private val end: Vector, private var strokeWidth: Float) : Drawer {

		var oldSegment: GestureSegment? = null
		private val paint = Paint()

		init {
			paint.color = Color.WHITE
			paint.strokeCap = Paint.Cap.ROUND
			paint.style = Paint.Style.STROKE
			paint.strokeWidth = strokeWidth
			paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
		}

		fun discardStrokeSize(): Boolean {
			if (oldSegment?.discardStrokeSize() == false) {
				oldSegment = null
			}
			strokeWidth -= 1f
			paint.strokeWidth = strokeWidth
			return strokeWidth > 2f
		}

		override fun onDraw(canvas: Canvas) {
			oldSegment?.onDraw(canvas)
			canvas.drawLine(start.x, start.y, end.x, end.y, paint)
		}
	}
}