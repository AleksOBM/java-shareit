package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {

	/// Получение данных о конкретном бронировании
	BookingDto getBooking(Long userId, long bookingId);

	/// Получение списка всех бронирований текущего пользователя
	List<BookingDto> getAllBookingsByBooker(long userId, String state);

	/// Получение списка бронирований для всех вещей текущего пользователя
	List<BookingDto> getAllBookingsByOwner(long userId, String state);

	/// Добавление нового бронирования
	@Transactional
	BookingDto addNewBooking(Long userId, IncomingBookingDto bookingDto);

	/// Согласование бронирования
	@Transactional(propagation = Propagation.REQUIRED)
	BookingDto approveBooking(long userId, long bookingId, boolean approved);
}
