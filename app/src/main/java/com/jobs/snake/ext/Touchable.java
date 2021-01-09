package com.jobs.snake.ext;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

public interface Touchable {
	boolean onTouchEvent(final @NonNull MotionEvent motionEvent);
}
