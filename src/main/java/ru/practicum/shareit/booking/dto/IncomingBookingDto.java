package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.practicum.shareit.util.Marker;

import java.time.LocalDateTime;

@Data
@NotNull
public class IncomingBookingDto {

	@Positive(message = "id вещи - обязательный параметр, должен быть положительным числом")
	long itemId;

	@FutureOrPresent(message = "Дата начала не может быть в прошлом")
	@NotNull(groups = Marker.OnCreate.class, message = "Дата начала бронирования - обязательна")
	LocalDateTime start;

	@Future(message = "Дата окончания должна быть в будущем")
	@NotNull(groups = Marker.OnCreate.class, message = "Дата окончания бронирования - обязательна")
	LocalDateTime end;
}
