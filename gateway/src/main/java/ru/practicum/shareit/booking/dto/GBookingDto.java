package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.util.Marker;

@Data
@AllArgsConstructor
public class GBookingDto {

	@NotNull(groups = Marker.OnCreate.class)
	long itemId;

	@FutureOrPresent
	@NotNull(groups = Marker.OnCreate.class)
	LocalDateTime start;

	@Future
	@NotNull(groups = Marker.OnCreate.class)
	LocalDateTime end;
}
