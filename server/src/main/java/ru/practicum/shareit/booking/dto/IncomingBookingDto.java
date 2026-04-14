package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class IncomingBookingDto {
	long itemId;
	LocalDateTime start;
	LocalDateTime end;
}
