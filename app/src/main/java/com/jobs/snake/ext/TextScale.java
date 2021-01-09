package com.jobs.snake.ext;

public enum TextScale {

	// Огромный
	Huge(3),

	//	Большой
	Big(5),

	//	Средний
	Normal(7),

	//	Мелкий
	Small(10),

	//	Очень мелкий
	VerySmall(30);

	//	Значение размера текста, рассчёт по формуле (getHeight() / value)
	private final int value;

	//	Конструктор для задания значения размера текста
	TextScale(int value) {
		this.value = value;
	}

	//	Получить значение размера текста
	public int getValue() {
		return value;
	}
}