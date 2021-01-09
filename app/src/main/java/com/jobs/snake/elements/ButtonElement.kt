package com.jobs.snake.elements

import android.view.MotionEvent
import com.jobs.snake.ext.Touchable

open class ButtonElement(text: String, var action: Runnable) : TextElement(text), Touchable {
	override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
		if (isEntry(motionEvent.x, motionEvent.y) && (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL)) {
			action.run()
		}
		return false
	}
}