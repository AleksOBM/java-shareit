package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.UtilService;
import ru.practicum.shareit.util.exception.ValidationException;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@Mock
	UserService userService;

	@Mock
	UtilService utilService;

	@Mock
	ItemRepository itemRepository;

	@Mock
	BookingRepository bookingRepository;

	@InjectMocks
	BookingServiceImpl bookingService;

	final TestUtils testUtils = new TestUtils();

	@Nested
	class AddNewBooking {

		@Test
		void whenAllIsOk_thenReturnActualBookingDto() {
			// region setup
			Booking booking = testUtils.makeNewAnyFullFastBooking(
					1,
					testUtils.futureDate,
					BookingStatus.WAITING,
					null
			);
			BookingDto bookingDto = BookingMapper.toBookingDto(booking);
			IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
					booking.getItem().getId(), booking.getStart(), booking.getEnd());
			Booking savedBooking = testUtils.makeCopyOfBooking(booking).setId(null);
			// endregion setup

			when(utilService.getUser(booking.getBooker().getId())).thenReturn(booking.getBooker());
			when(utilService.getItem(booking.getItem().getId())).thenReturn(booking.getItem());
			when(bookingRepository.save(savedBooking)).thenReturn(booking);

			BookingDto resultDto = bookingService.addNewBooking(booking.getBooker().getId(), incomingBookingDto);

			assertThat(resultDto, equalTo(bookingDto));
			verify(bookingRepository).save(savedBooking);
		}

		@Test
		void whenStartDateIsAfterEndDate_thenReturnValidationException() {
			// region setup
			Booking booking = testUtils.makeNewAnyFullFastBooking(
					1,
					testUtils.futureDate,
					BookingStatus.WAITING,
					null
			);

			// Breaking it here
			booking.setStart(booking.getEnd().plusYears(1));

			IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
					booking.getItem().getId(), booking.getStart(), booking.getEnd());
			// endregion setup

			assertThrows(ValidationException.class, () ->
					bookingService.addNewBooking(booking.getBooker().getId(), incomingBookingDto)
			);

			verify(utilService, never()).getItem(anyLong());
			verify(utilService, never()).getUser(anyLong());
			verify(bookingRepository, never()).save(any(Booking.class));
		}

		@Test
		void whenStartDateIsEqualEndDate_thenReturnValidationException() {
			// region setup
			Booking booking = testUtils.makeNewAnyFullFastBooking(
					1,
					testUtils.futureDate,
					BookingStatus.WAITING,
					null
			);

			// Breaking it here
			booking.setStart(booking.getEnd());

			IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
					booking.getItem().getId(), booking.getStart(), booking.getEnd());
			// endregion setup

			assertThrows(ValidationException.class, () ->
					bookingService.addNewBooking(booking.getBooker().getId(), incomingBookingDto)
			);

			verify(utilService, never()).getItem(anyLong());
			verify(utilService, never()).getUser(anyLong());
			verify(bookingRepository, never()).save(any(Booking.class));
		}

		@Test
		void whenBookerIsEqualOwner_thenReturnValidationException() {
			// region setup
			Booking booking = testUtils.makeNewAnyFullFastBooking(
					1,
					testUtils.futureDate,
					BookingStatus.WAITING,
					null
			);

			// Breaking it here
			booking.setBooker(booking.getItem().getOwner());

			IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
					booking.getItem().getId(), booking.getStart(), booking.getEnd());
			// endregion setup

			when(utilService.getUser(booking.getBooker().getId())).thenReturn(booking.getBooker());
			when(utilService.getItem(booking.getItem().getId())).thenReturn(booking.getItem());

			assertThrows(ValidationException.class, () ->
					bookingService.addNewBooking(booking.getBooker().getId(), incomingBookingDto)
			);

			verify(bookingRepository, never()).save(any(Booking.class));
		}

		@Test
		void whenItemIsNotAvaialble_thenReturnValidationException() {
			// region setup
			Booking booking = testUtils.makeNewAnyFullFastBooking(
					1,
					testUtils.futureDate,
					BookingStatus.WAITING,
					null
			);

			// Breaking it here
			booking.setItem(booking.getItem().setAvailable(false));

			IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
					booking.getItem().getId(), booking.getStart(), booking.getEnd());
			// endregion setup

			when(utilService.getUser(booking.getBooker().getId())).thenReturn(booking.getBooker());
			when(utilService.getItem(booking.getItem().getId())).thenReturn(booking.getItem());

			assertThrows(ValidationException.class, () ->
					bookingService.addNewBooking(booking.getBooker().getId(), incomingBookingDto)
			);

			verify(bookingRepository, never()).save(any(Booking.class));
		}
	}

	@Nested
	class ApproveBooking {

		@Test
		void whenAllIsOk_thenReturnActualBookingDto() {
			// region setup
			long bookingId = 1L;
			Booking approovedBooking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.APPROVED,
					null
			);
			Long ownerId = approovedBooking.getBooker().getId();
			approovedBooking.setItem(approovedBooking.getItem().setOwner(approovedBooking.getBooker()));
			Booking inBaseBooking = testUtils.makeCopyOfBooking(approovedBooking).setStatus(BookingStatus.WAITING);
			BookingDto bookingDto = BookingMapper.toBookingDto(approovedBooking);
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(inBaseBooking));
			when(bookingRepository.save(inBaseBooking)).thenReturn(approovedBooking);

			BookingDto resultDto = bookingService
					.approveBooking(ownerId, bookingId, true);

			assertThat(resultDto, equalTo(bookingDto));
		}
	}

	@Nested
	class GetBooking {

		@Test
		void testGetBooking() {
		}
	}

	@Nested
	class GetAllBookingsByBooker {

		@Test
		void testGetAllBookingsByBooker() {
		}
	}

	@Nested
	class GetAllBookingsByOwner {

		@Test
		void testGetAllBookingsByOwner() {
		}
	}
}