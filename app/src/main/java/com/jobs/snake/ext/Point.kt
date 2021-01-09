package com.jobs.snake.ext

class Point(var x: Int, var y: Int) {
	constructor() : this(0, 0)

	fun setPosition(x: Int, y: Int) {
		this.x = x
		this.y = y
	}

	override fun equals(other: Any?): Boolean {
		return other is Point && other.x == x && other.y == y
	}

	override fun hashCode(): Int {
		var result = x
		result = 31 * result + y
		return result
	}
}