package ru.practicum.shareit.util.error;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.util.exception.DuplicatedDataException;
import ru.practicum.shareit.util.exception.MethodNotImplementedException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	@ExceptionHandler
	public ErrorResponse handleMethodNotImplemented(final MethodNotImplementedException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler
	public ErrorResponse handleNotFound(final NotFoundException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler
	public ErrorResponse handleDuplicatedData(final DuplicatedDataException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ErrorResponse handleMethodValidationException(final MethodValidationException e) {
		return new ErrorResponse(e.getAllErrors().getFirst().getDefaultMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
		return new ErrorResponse("Не верно указан параметр запроса: " + e.getPropertyName() + "=" + e.getValue());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ErrorResponse handleValidationException(final ValidationException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingRequestHeaderException.class)
	public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
		return new ErrorResponse("Отсутствует заголовок в запросе: " + e.getHeaderName());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ErrorResponse handleEmptyBody() {
		return new ErrorResponse("Тело запроса отсутствует или некорректно");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleMethodArgumentException(final MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();

		e.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON)
				.body(errors);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public ErrorResponse handleAnyThrowable(final Throwable e) {
		UUID uuid = UUID.randomUUID();
		log.error("UUID={} Message=\"{}.\"", uuid, e.getMessage());
		return new ErrorResponse("Произошла непредвиденная ошибка. UUID=" + uuid);
	}
}
