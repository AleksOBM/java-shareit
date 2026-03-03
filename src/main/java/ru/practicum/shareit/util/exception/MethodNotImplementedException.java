package ru.practicum.shareit.util.exception;

public class MethodNotImplementedException extends RuntimeException {
	public MethodNotImplementedException(String className, String methodName) {
		super("Method not implemented " + className + "." + methodName);
	}
}
