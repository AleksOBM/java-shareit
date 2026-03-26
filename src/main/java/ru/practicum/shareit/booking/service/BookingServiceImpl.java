package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.ForbiddenException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ParameterNotValidException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

	BookingRepository bookingRepository;
	ItemRepository itemRepository;
	UserRepository userRepository;

	@Override
	public BookingDto addNewBooking(Long userId, BookingDto bookingDto) {
		LocalDateTime start = bookingDto.getStart();
		LocalDateTime end = bookingDto.getEnd();
		if (start.isAfter(end) || start.isEqual(end)) {
			throw new ValidationException("Дата начала бронирования должна быть раньше даты окончания");
		}
		if (bookingDto.getItem() == null) {
			throw new ValidationException("item.id не может быть null");
		}
		User booker = userRepository.findById(userId).orElseThrow(
				() -> new ForbiddenException("Пользователь с id=" + userId + " не найден")
		);
		Long itemId = bookingDto.getItem().getId();
		Item item = itemRepository.findById(itemId).orElseThrow(
				() -> new NotFoundException("Вещь с id=" + itemId + " не найдена")
		);
		if (item.getOwner().getId().equals(userId)) {
			throw new ValidationException("Нельзя забронировать свою-же вещь");
		}
		if (!item.isAvailable()) {
			throw new ValidationException("Вещь с id=" + item.getId() + " не доступна для аренды");
		}
		bookingDto.setStatus(BookingStatus.WAITING);
		return BookingMapper.toBookingDto(
				bookingRepository.save(BookingMapper.toBooking(bookingDto, item, booker))
		);
	}

	@Override
	public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
		checkUser(userId);
		Booking booking = getBooking(bookingId);
		if (approved) {
			booking.setStatus(BookingStatus.APPROVED);
		} else {
			booking.setStatus(BookingStatus.REJECTED);
		}
		return BookingMapper.toBookingDto(booking);
	}

	@Override
	public BookingDto getBooking(Long userId, long bookingId) {
		checkUser(userId);
		Booking booking = getBooking(bookingId);
		if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
			throw new ForbiddenException("Может быть выполнено либо автором бронирования, либо владельцем вещи");
		}
		return BookingMapper.toBookingDto(booking);
	}

	@Override
	public List<BookingDto> getAllBookingsByBooker(long userId, String state) {
		checkUser(userId);
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
		checkUser(userId);
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

	private void checkUser(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new ForbiddenException("Пользователь с id=" + userId + " не найден.");
		}
	}

	private Booking getBooking(Long bookingId) {
		return bookingRepository.findById(bookingId).orElseThrow(
				() -> new NotFoundException("Запрос аренды с id=" + bookingId + " не существует")
		);
	}
}
