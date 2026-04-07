package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class IncomingBookingDto {
	long itemId;
	LocalDateTime start;
	LocalDateTime end;
}
