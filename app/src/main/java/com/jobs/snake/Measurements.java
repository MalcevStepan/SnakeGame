package com.jobs.snake;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.jobs.snake.components.GameView;
import com.jobs.snake.ext.Point;
import com.jobs.snake.ext.Vector;
/*
class SnakeElement {
	private SnakeElement parent, nextElement;
	Point position;
	private Vector offset = new Vector(0, 0);

	SnakeElement(SnakeElement parent, Point position) {
		this.parent = parent;
		this.position = position;
	}

	SnakeElement insert(SnakeElement head) {
		head.nextElement = this;
		return parent = head;
	}

	SnakeElement displace(SnakeElement head, byte x, byte y) {
		if (nextElement == null) {
			if (parent != null)
				parent.nextElement = null;
			parent = null;
			if (head != this)
				nextElement = head;
			position.setPosition(x, y);
			offset.setPosition(0, 0);
			return head.parent = this;
		}
		return nextElement.displace(head, x, y);
	}

	private void add(Point position) {
		if (nextElement == null)
			nextElement = new SnakeElement(this, position);
		else
			nextElement.add(position);
	}

	void onDraw(Canvas canvas, Paint paint, SnakeElement head, int number, float off) {
		if (parent != null) {
			if(Math.max(Math.abs((position.x - parent.position.x) * Memory.cellSize), Math.abs((position.y - parent.position.y) * Memory.cellSize)) < canvas.getHeight() * 2f / 3f)
				offset = Vector.lerp(position, parent.position, off);
			else
				offset.setPosition(position.x, position.y);
			canvas.drawRect(offset.x * Memory.cellSize, offset.y * Memory.cellSize, (offset.x + 1) * Memory.cellSize, (offset.y + 1) * Memory.cellSize, paint);
		}
		if (position.equals(GameView.apple.position) && number == GameView.number) {
			GameView.apple.setPosition((byte) -1, (byte) -1);
		}
		if ((position.equals(head.position) && this != head && number == GameView.number) || (number != GameView.number && position.equals(GameView.snakes.get(GameView.number).head.position))) {
			Memory.viewMode = ViewMode.LosePage;
			return;
		}
		if (nextElement != null)
			nextElement.onDraw(canvas, paint, head, number, off);
	}
}*/