package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UtilService;
import ru.practicum.shareit.util.exception.BookingStatusException;
import ru.practicum.shareit.util.exception.ForbiddenException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ParameterNotValidException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class BookingServiceImpl implements BookingService {

	BookingRepository bookingRepository;
	UtilService utilService;

	@Override
	public BookingDto addNewBooking(Long userId, @NonNull IncomingBookingDto bookingDto) {
		LocalDateTime start = bookingDto.getStart();
		LocalDateTime end = bookingDto.getEnd();
		if (start.isAfter(end) || start.isEqual(end)) {
			throw new ValidationException("Дата начала бронирования должна быть раньше даты окончания");
		}

		User booker = utilService.getUser(userId);
		long itemId = bookingDto.getItemId();
		Item item = utilService.getItem(itemId);
		if (item.getOwner().getId().equals(userId)) {
			throw new ValidationException("Нельзя забронировать свою-же вещь");
		}
		if (!item.isAvailable()) {
			throw new ValidationException("Вещь с id=" + item.getId() + " не доступна для аренды");
		}

		return BookingMapper.toBookingDto(
				bookingRepository.save(BookingMapper.fromIncomingDto(bookingDto, item, booker))
		);
	}

	@Override
	public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
		utilService.checkUser(userId);
		Booking booking = getBooking(bookingId);
		if (userId != booking.getItem().getOwner().getId()) {
			throw new ForbiddenException("Только владелец может менять статус вещи");
		}
		BookingStatus status = booking.getStatus();
		switch (status) {
			case WAITING -> {
				if (approved) {
					booking.setStatus(BookingStatus.APPROVED);
				} else {
					booking.setStatus(BookingStatus.REJECTED);
				}
			}
			case REJECTED -> {
				if (approved) {
					booking.setStatus(BookingStatus.APPROVED);
				}
			}
			case CANCELED -> throw new BookingStatusException("Бронирование уже было отменено создателем");
		}

		return BookingMapper.toBookingDto(booking);
	}

	@Override
	public BookingDto getBooking(Long userId, long bookingId) {
		utilService.checkUser(userId);
		Booking booking = getBooking(bookingId);
		if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
			throw new ForbiddenException("Может быть выполнено либо автором бронирования, либо владельцем вещи");
		}
		return BookingMapper.toBookingDto(booking);
	}

	@Override
	public List<BookingDto> getAllBookingsByBooker(long userId, String state) {
		utilService.checkUser(userId);
		StateOfBooking stateOfBooking = StateOfBooking.of(state);
		switch (stateOfBooking) {
			case ALL -> {
				return bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case CURRENT -> {
				return bookingRepository.findCurrentByBooker(userId).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case PAST -> {
				return bookingRepository.findPastByBooker(userId).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case FUTURE -> {
				return bookingRepository.findFutureByBooker(userId).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case WAITING -> {
				return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(
								userId,
								BookingStatus.WAITING
						).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case REJECTED -> {
				return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(
								userId,
								BookingStatus.REJECTED
						).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case null, default -> {
				assert stateOfBooking != null;
				throw new ParameterNotValidException(
						"state = " + stateOfBooking.getInvalidValue(),
						"допустимые значения: " + StateOfBooking.getValidValues()
				);
			}
		}
	}

	@Override
	public List<BookingDto> getAllBookingsByOwner(long userId, String state) {
		utilService.checkUser(userId);
		StateOfBooking stateOfBooking = StateOfBooking.of(state);
		switch (stateOfBooking) {
			case ALL -> {
				return bookingRepository.findAllByOwner(userId).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case CURRENT -> {
				return bookingRepository.findCurrentByOwner(userId).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case PAST -> {
				return bookingRepository.findPastByOwner(userId).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case FUTURE -> {
				return bookingRepository.findFutureByOwner(userId).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case WAITING -> {
				return bookingRepository.findAllByOwnerAndStatusOrderByStartDesc(
								userId,
								BookingStatus.WAITING
						).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case REJECTED -> {
				return bookingRepository.findAllByOwnerAndStatusOrderByStartDesc(
								userId,
								BookingStatus.REJECTED
						).stream()
						.map(BookingMapper::toBookingDto)
						.toList();
			}
			case null, default -> {
				assert stateOfBooking != null;
				throw new ParameterNotValidException(
						"state = " + stateOfBooking.getInvalidValue(),
						"допустимые значения: " + StateOfBooking.getValidValues()
				);
			}
		}
	}

	@NonNull
	private Booking getBooking(Long bookingId) {
		return bookingRepository.findById(bookingId).orElseThrow(
				() -> new NotFoundException("Запрос аренды с id=" + bookingId + " не существует")
		);
	}
}
