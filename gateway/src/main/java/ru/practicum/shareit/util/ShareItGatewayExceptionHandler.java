package ru.practicum.shareit.util;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ShareItGatewayExceptionHandler {

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleConstraintViolation(final ConstraintViolationException ex) {
		String errorMessage = ex.getConstraintViolations().stream()
				.map(violation -> {
					String field = violation.getPropertyPath().toString();

					if (field.contains(".")) {
						field = field.substring(field.lastIndexOf('.') + 1);
					}

					return "Поле " + field + ": " + violation.getMessage();
				})
				.collect(Collectors.joining("; "));

		ErrorResponse response = new ErrorResponse(errorMessage);
		log.error(errorMessage);

		return response;
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {
		ErrorResponse response = new ErrorResponse(
				"Поле " + Objects.requireNonNull(ex.getFieldError()).getField() + " " +
				ex.getFieldError().getDefaultMessage()
		);
		log.error(response.error());
		return response;
	}
}