package ru.practicum.shareit.util.exception;

public class DuplicatedDataException extends RuntimeException {
	public <T> DuplicatedDataException(Class<T> clazz) {
		super("Обнаружено дублирование данных в " + clazz.getSimpleName());
	}
}
