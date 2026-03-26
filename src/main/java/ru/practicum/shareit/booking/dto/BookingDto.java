package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Marker;
import ru.practicum.shareit.util.datetime.FutureOrPresentWithTolerance;
import ru.practicum.shareit.util.datetime.TimeRoundingStrategy;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@NotNull
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {

	Long id;

	@NotNull(groups = Marker.OnCreate.class, message = "Дата начала бронирования - обязательна")
	@FutureOrPresentWithTolerance(
			rounding = TimeRoundingStrategy.TRUNCATE_TO_SECONDS,
			message = "Дата начала не может быть в прошлом"
	)
	LocalDateTime start;

	@NotNull(groups = Marker.OnCreate.class, message = "Дата окончания бронирования - обязательна")
	@Future(message = "Дата окончания должна быть в будущем")
	LocalDateTime end;

	ItemDto item;

	UserDto booker;

	BookingStatus status;

	public BookingDto(
			Long id,
			LocalDateTime start,
			LocalDateTime end,
			Long itemId,
			Long bookerId,
			BookingStatus status
	) {
		this.id = id != null && id <= 0 ? null : id;
		this.start = start;
		this.end = end;
		this.item = itemId != null && itemId <= 0 ? null :
				new ItemDto(itemId, null, null, null, null);
		this.booker = bookerId != null && bookerId <= 0 ? null :
				new UserDto(bookerId, null, null);
		this.status = status;
	}
}
