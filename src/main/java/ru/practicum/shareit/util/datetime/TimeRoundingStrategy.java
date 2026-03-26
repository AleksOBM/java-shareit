package ru.practicum.shareit.util.datetime;

public enum TimeRoundingStrategy {

	NONE,                // без округления
	TRUNCATE_TO_SECONDS, // обрезать до секунд
	TRUNCATE_TO_MINUTES, // обрезать до минут
	ROUND_TO_SECONDS     // математическое округление
}
