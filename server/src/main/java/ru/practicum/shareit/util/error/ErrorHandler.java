package ru.practicum.shareit.util.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.util.exception.*;

import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler
	public ErrorResponse handleNotFound(final NotFoundException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler
	public ErrorResponse handleSharingForbidden(final ForbiddenException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ErrorResponse handleParameterNotValid(final ParameterNotValidException e) {
		return new ErrorResponse("Некорректное значение параметра " +
				e.getParameter() + ": " + e.getReason()
		);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ErrorResponse handleBookingStatusException(final BookingStatusException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler
	public ErrorResponse handleDuplicatedData(final DuplicatedDataException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ErrorResponse handleValidationException(final ValidationException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public ErrorResponse handleAnyThrowable(final Throwable e) {
		UUID uuid = UUID.randomUUID();
		log.error("UUID={} Message=\"{}.\"", uuid, e.getMessage());
		return new ErrorResponse("Произошла непредвиденная ошибка. UUID=" + uuid);
	}
}
