package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncomingBookingDto {
	long itemId;
	LocalDateTime start;
	LocalDateTime end;
}
