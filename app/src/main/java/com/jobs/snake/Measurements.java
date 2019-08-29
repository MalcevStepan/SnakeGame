package com.jobs.snake;

import android.graphics.Canvas;
import android.graphics.Paint;

class Point {
	byte x, y;

	Point(byte x, byte y) {
		this.x = x;
		this.y = y;
	}

	void setPosition(byte x, byte y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Point && (((Point) obj).x == x && ((Point) obj).y == y);
	}
}

class Vector {
	float x, y;

	Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	void lerp(float x, float y) {
		this.x = (this.x + x) / 2f;
		this.y = (this.y + y) / 2f;
	}

	void lerp(float x, float y, float c) {
		this.x += (this.x - x < 0 ? 1 : -1) * Math.abs(this.x - x) / c;
		this.y += (this.y - y < 0 ? 1 : -1) * Math.abs(this.y - y) / c;
	}

	static Vector lerp(Point a, Point b, float c) {
		return new Vector(a.x + (a.x - b.x < 0 ? 1 : -1) * Math.abs(a.x - b.x) * c, a.y + (a.y - b.y < 0 ? 1 : -1) * Math.abs(a.y - b.y) * c);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Vector && (((Vector) obj).x == x && ((Vector) obj).y == y);
	}
}

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
			Net.sendMessage((byte) 6);
			GameView.apple.setPosition((byte) -1, (byte) -1);
		}
		if ((position.equals(head.position) && this != head && number == GameView.number) || (number != GameView.number && position.equals(GameView.snakes.get(GameView.number).head.position))) {
			Net.sendMessage((byte) 7);
			Memory.viewMode = ViewMode.LosePage;
			return;
		}
		if (nextElement != null)
			nextElement.onDraw(canvas, paint, head, number, off);
	}
}