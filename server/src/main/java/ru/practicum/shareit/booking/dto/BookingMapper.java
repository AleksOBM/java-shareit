package ru.practicum.shareit.booking.dto;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {

	@NonNull
	public static BookingDto toBookingDto(@NonNull Booking booking) {
		return new BookingDto(
				booking.getId(),
				booking.getStart(),
				booking.getEnd(),
				ItemMapper.toDto(booking.getItem()),
				UserMapper.toUserDto(booking.getBooker()),
				booking.getStatus()
		);
	}

	public static Booking fromIncomingDto(@NonNull IncomingBookingDto bookingDto, Item item, User booker) {
		return new Booking()
				.setID(null)
				.setStart(bookingDto.getStart())
				.setEnd(bookingDto.getEnd())
				.setBooker(booker)
				.setItem(item)
				.setStatus(BookingStatus.WAITING);
	}
}
