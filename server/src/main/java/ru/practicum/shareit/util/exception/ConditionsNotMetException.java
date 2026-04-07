package ru.practicum.shareit.util.exception;

public class ConditionsNotMetException extends RuntimeException {
	public ConditionsNotMetException(String message) {
		super(message);
	}
}
