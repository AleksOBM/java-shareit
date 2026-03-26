package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneId;

public class BookingMapper {
	public static BookingDto toBookingDto(Booking booking) {
		BookingDto bookingDto = new BookingDto(
				booking.getId(),
				booking.getStart().atZone(ZoneId.systemDefault()).toLocalDateTime(),
				booking.getEnd().atZone(ZoneId.systemDefault()).toLocalDateTime(),
				null,
				null,
				booking.getStatus()
		);
		bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
		bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
		return bookingDto;
	}

	public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
		return new Booking(
				bookingDto.getId(),
				bookingDto.getStart().atZone(ZoneId.systemDefault()).toLocalDateTime(),
				bookingDto.getEnd().atZone(ZoneId.systemDefault()).toLocalDateTime(),
				item,
				booker,
				bookingDto.getStatus()
		);
	}
}
