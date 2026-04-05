package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.validate.Marker;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
	final BookingService bookingService;

	@GetMapping("/{bookingId}")
	public BookingDto getBooking(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@PathVariable long bookingId
	) {
		return bookingService.getBooking(userId, bookingId);
	}

	@GetMapping
	public List<BookingDto> getAllBookingsByBooker(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(required = false) String state
	) {
		return bookingService.getAllBookingsByBooker(userId, state);
	}

	@GetMapping("/owner")
	public List<BookingDto> getAllBookingsByOwner(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(required = false) String state
	) {
		return bookingService.getAllBookingsByOwner(userId, state);
	}

	@PostMapping
	@Validated(Marker.OnCreate.class)
	public BookingDto addNewBooking(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@Valid @RequestBody IncomingBookingDto bookingDto
	) {
		return bookingService.addNewBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public BookingDto approveBooking(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable long bookingId,
			@RequestParam boolean approved
	) {
		return bookingService.approveBooking(userId, bookingId, approved);
	}
}
