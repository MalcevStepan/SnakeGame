package com.jobs.snake.ext;

import android.graphics.Point;

public class Vector {
	public float x, y;

	public Vector(final float x, final float y) {
		this.x = x;
		this.y = y;
	}

	public void setPosition(final float x, final float y) {
		this.x = x;
		this.y = y;
	}

	public void lerp(final float x, final float y) {
		this.x = (this.x + x) / 2f;
		this.y = (this.y + y) / 2f;
	}

	public void lerp(final float x, final float y, final float c) {
		this.x += (this.x - x < 0 ? 1 : -1) * Math.abs(this.x - x) / c;
		this.y += (this.y - y < 0 ? 1 : -1) * Math.abs(this.y - y) / c;
	}

	static Vector lerp(final Point a, final Point b, final float c) {
		return new Vector(a.x + (a.x - b.x < 0 ? 1 : -1) * Math.abs(a.x - b.x) * c, a.y + (a.y - b.y < 0 ? 1 : -1) * Math.abs(a.y - b.y) * c);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Vector && (((Vector) obj).x == x && ((Vector) obj).y == y);
	}
}