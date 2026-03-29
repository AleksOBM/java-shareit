package ru.practicum.shareit.util.exception;

import lombok.Getter;

@Getter
public class ParameterNotValidException extends IllegalArgumentException {
	String parameter;
	String reason;

	public ParameterNotValidException(String parameter, String reason) {
		super(reason);
		this.parameter = parameter;
		this.reason = reason;
	}
}

