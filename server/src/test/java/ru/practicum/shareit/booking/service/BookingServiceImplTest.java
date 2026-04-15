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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UtilService;
import ru.practicum.shareit.util.exception.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@Mock
	UtilService utilService;

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
		void whenApproveIsTrue_andUserIsOwner_thenReturnApprovedBookingDto() {
			// region setup
			long bookingId = 1L;
			Booking approovedBooking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.APPROVED,
					null
			);
			Long ownerId = approovedBooking.getItem().getOwner().getId();
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

		@Test
		void whenApproveIsTrue_andUserIsOwner_andBookingAlredyApprouved_thenReturnApprovedBookingDto() {
			// region setup
			long bookingId = 1L;
			Booking approovedBooking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.APPROVED,
					null
			);
			Long ownerId = approovedBooking.getItem().getOwner().getId();
			Booking inBaseBooking = testUtils.makeCopyOfBooking(approovedBooking).setStatus(BookingStatus.APPROVED);
			BookingDto bookingDto = BookingMapper.toBookingDto(approovedBooking);
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(inBaseBooking));
			when(bookingRepository.save(inBaseBooking)).thenReturn(approovedBooking);

			BookingDto resultDto = bookingService
					.approveBooking(ownerId, bookingId, true);

			assertThat(resultDto, equalTo(bookingDto));
		}

		@Test
		void whenApproveIsFalse_andUserIsOwner_thenReturnRejectedBookingDto() {
			// region setup
			long bookingId = 1L;
			Booking approovedBooking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.REJECTED,
					null
			);
			Long ownerId = approovedBooking.getItem().getOwner().getId();
			Booking inBaseBooking = testUtils.makeCopyOfBooking(approovedBooking).setStatus(BookingStatus.WAITING);
			BookingDto bookingDto = BookingMapper.toBookingDto(approovedBooking);
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(inBaseBooking));
			when(bookingRepository.save(inBaseBooking)).thenReturn(approovedBooking);

			BookingDto resultDto = bookingService
					.approveBooking(ownerId, bookingId, false);

			assertThat(resultDto, equalTo(bookingDto));
		}

		@Test
		void whenApproveIsTrue_butBookingWasRejected_thenStatusWillChanged_andReturnApprovedBookingDto() {
			// region setup
			long bookingId = 1L;
			Booking approovedBooking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.APPROVED,
					null
			);
			Long ownerId = approovedBooking.getItem().getOwner().getId();
			Booking inBaseBooking = testUtils.makeCopyOfBooking(approovedBooking).setStatus(BookingStatus.REJECTED);
			BookingDto bookingDto = BookingMapper.toBookingDto(approovedBooking);
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(inBaseBooking));
			when(bookingRepository.save(inBaseBooking)).thenReturn(approovedBooking);

			BookingDto resultDto = bookingService
					.approveBooking(ownerId, bookingId, true);

			assertThat(resultDto, equalTo(bookingDto));
		}

		@Test
		void whenApproveIsFalse_andBookingWasRejected_thenStatusWillChanged_andReturnRejectedBookingDto() {
			// region setup
			long bookingId = 1L;
			Booking approovedBooking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.REJECTED,
					null
			);
			Long ownerId = approovedBooking.getItem().getOwner().getId();
			Booking inBaseBooking = testUtils.makeCopyOfBooking(approovedBooking).setStatus(BookingStatus.REJECTED);
			BookingDto bookingDto = BookingMapper.toBookingDto(approovedBooking);
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(inBaseBooking));
			when(bookingRepository.save(inBaseBooking)).thenReturn(approovedBooking);

			BookingDto resultDto = bookingService
					.approveBooking(ownerId, bookingId, false);

			assertThat(resultDto, equalTo(bookingDto));
		}

		@Test
		void whenApproveIsTrue_butBookingIsCanceled_thenReturnBookingStatusException() {
			// region setup
			long bookingId = 1L;
			Booking approovedBooking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.APPROVED,
					null
			);
			Long ownerId = approovedBooking.getItem().getOwner().getId();
			Booking inBaseBooking = testUtils.makeCopyOfBooking(approovedBooking).setStatus(BookingStatus.CANCELED);
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(inBaseBooking));

			assertThrows(BookingStatusException.class, () -> bookingService
					.approveBooking(ownerId, bookingId, true));

			verify(bookingRepository, never()).save(any(Booking.class));
		}

		@Test
		void whenUserIsNotAnOwnerAndNotABooker_thenReturnForbiddenException() {
			// region setup
			long bookingId = 1L;
			Booking approovedBooking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.APPROVED,
					null
			);
			Long ownerId = approovedBooking.getItem().getOwner().getId();
			approovedBooking.setItem(approovedBooking.getItem().setOwner(testUtils.makeNewUser(500)));
			approovedBooking.setBooker(testUtils.makeNewUser(501));
			Booking inBaseBooking = testUtils.makeCopyOfBooking(approovedBooking).setStatus(BookingStatus.WAITING);
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(inBaseBooking));

			assertThrows(ForbiddenException.class, () -> bookingService
					.approveBooking(ownerId, bookingId, true));

			verify(bookingRepository, never()).save(any(Booking.class));
		}

		@Test
		void whenBookingIsLost_thenReturnNotFoundException() {
			// region setup
			long bookingId = 1L;
			Booking approovedBooking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.APPROVED,
					null
			);
			Long ownerId = approovedBooking.getItem().getOwner().getId();
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

			assertThrows(NotFoundException.class, () -> bookingService
					.approveBooking(ownerId, bookingId, true));

			verify(bookingRepository, never()).save(any(Booking.class));
		}

	}

	@Nested
	class GetBooking {

		@Test
		void whenUserIsOwner_thenReturnActualBookingDto() {
			// region setup
			long bookingId = 1L;
			Booking booking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.APPROVED,
					null
			);
			Long ownerId = booking.getItem().getOwner().getId();
			BookingDto bookingDto = BookingMapper.toBookingDto(booking);
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

			BookingDto resultDto = bookingService.getBooking(ownerId, bookingId);

			assertThat(resultDto, equalTo(bookingDto));
		}

		@Test
		void whenUserIsBooker_thenReturnActualBookingDto() {
			// region setup
			long bookingId = 1L;
			Booking booking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.APPROVED,
					null
			);
			Long bookerId = booking.getBooker().getId();
			BookingDto bookingDto = BookingMapper.toBookingDto(booking);
			// endregion setup

			doNothing().when(utilService).checkUser(bookerId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

			BookingDto resultDto = bookingService.getBooking(bookerId, bookingId);

			assertThat(resultDto, equalTo(bookingDto));
		}

		@Test
		void whenUserIsNotBooker_andUserIsNotOwner_thenReturnForbiddenException() {
			// region setup
			long bookingId = 1L;
			Booking booking = testUtils.makeNewAnyFullFastBooking(
					bookingId,
					testUtils.futureDate,
					BookingStatus.APPROVED,
					null
			);
			Long userId = 500L;
			// endregion setup

			doNothing().when(utilService).checkUser(userId);
			when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

			assertThrows(ForbiddenException.class, () -> bookingService.getBooking(userId, bookingId));
		}
	}

	@Nested
	class GetAllBookingsByBooker {

		@Test
		void whenUserIsBooker_andStateIsAll_thenReturnListOfAllBookingDtosOfBooker() {
			// region setup
			User booker = testUtils.makeNewUser(1);
			long bookerId = booker.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.setBooker(booker));
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(bookerId);
			when(bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId)).thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByBooker(bookerId, "all");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStateIsNull_thenReturnListOfAllBookingDtosOfBooker() {
			// region setup
			User booker = testUtils.makeNewUser(1);
			long bookerId = booker.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.setBooker(booker));
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(bookerId);
			when(bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId)).thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByBooker(bookerId, null);

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStateIsCurrent_thenReturnListOfCurrentBookingDtosOfBooker() {
			// region setup
			User booker = testUtils.makeNewUser(1);
			long bookerId = booker.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.setBooker(booker));
			bookings = bookings.stream()
					.filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
					.toList();
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(bookerId);
			when(bookingRepository.findCurrentByBooker(bookerId)).thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByBooker(bookerId, "CURRENT");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStateIsPast_thenReturnListOfPastBookingDtosOfBooker() {
			// region setup
			User booker = testUtils.makeNewUser(1);
			long bookerId = booker.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.setBooker(booker));
			bookings = bookings.stream()
					.filter(booking -> booking.getEnd().isBefore(now))
					.toList();
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(bookerId);
			when(bookingRepository.findPastByBooker(bookerId)).thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByBooker(bookerId, "past");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStateIsFuture_thenReturnListOfFutureBookingDtosOfBooker() {
			// region setup
			User booker = testUtils.makeNewUser(1);
			long bookerId = booker.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.setBooker(booker));
			bookings = bookings.stream()
					.filter(booking -> booking.getStart().isAfter(now))
					.toList();
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(bookerId);
			when(bookingRepository.findFutureByBooker(bookerId)).thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByBooker(bookerId, "Future");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStatusIsWaiting_thenReturnListOfWaitingBookingDtosOfBooker() {
			// region setup
			User booker = testUtils.makeNewUser(1);
			long bookerId = booker.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.setBooker(booker));
			bookings = bookings.stream()
					.filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
					.toList();
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(bookerId);
			when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING))
					.thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByBooker(bookerId, "waItIng");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStatusIsRejected_thenReturnListOfRejectedBookingDtosOfBooker() {
			// region setup
			User booker = testUtils.makeNewUser(1);
			long bookerId = booker.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.setBooker(booker));
			bookings = bookings.stream()
					.filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
					.toList();
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(bookerId);
			when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED))
					.thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByBooker(bookerId, "rejected");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStatusIsUndefined_thenReturnParameterNotValidException() {
			long bookerId = 1L;

			doNothing().when(utilService).checkUser(bookerId);

			assertThrows(ParameterNotValidException.class, () ->
					bookingService.getAllBookingsByBooker(bookerId, "XXX"));
		}
	}

	@Nested
	class GetAllBookingsByOwner {

		@Test
		void whenUserIsBooker_andStateIsAll_thenReturnListOfAllBookingDtosOfBooker() {
			// region setup
			User owner = testUtils.makeNewUser(1);
			long ownerId = owner.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.getItem().setOwner(owner));
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findAllByOwner(ownerId)).thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByOwner(ownerId, "all");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStateIsNull_thenReturnListOfAllBookingDtosOfBooker() {
			// region setup
			User owner = testUtils.makeNewUser(1);
			long ownerId = owner.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.getItem().setOwner(owner));
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findAllByOwner(ownerId)).thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByOwner(ownerId, null);

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStateIsCurrent_thenReturnListOfCurrentBookingDtosOfBooker() {
			// region setup
			User owner = testUtils.makeNewUser(1);
			long ownerId = owner.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.getItem().setOwner(owner));
			bookings = bookings.stream()
					.filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
					.toList();
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findCurrentByOwner(ownerId)).thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByOwner(ownerId, "CURRENT");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStateIsPast_thenReturnListOfPastBookingDtosOfBooker() {
			// region setup
			User owner = testUtils.makeNewUser(1);
			long ownerId = owner.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.getItem().setOwner(owner));
			bookings = bookings.stream()
					.filter(booking -> booking.getEnd().isBefore(now))
					.toList();
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findPastByOwner(ownerId)).thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByOwner(ownerId, "past");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStateIsFuture_thenReturnListOfFutureBookingDtosOfBooker() {
			// region setup
			User owner = testUtils.makeNewUser(1);
			long ownerId = owner.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.getItem().setOwner(owner));
			bookings = bookings.stream()
					.filter(booking -> booking.getStart().isAfter(now))
					.toList();
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findFutureByOwner(ownerId)).thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByOwner(ownerId, "Future");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStatusIsWaiting_thenReturnListOfWaitingBookingDtosOfBooker() {
			// region setup
			User owner = testUtils.makeNewUser(1);
			long ownerId = owner.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.getItem().setOwner(owner));
			bookings = bookings.stream()
					.filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
					.toList();
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findAllByOwnerAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING))
					.thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByOwner(ownerId, "waItIng");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStatusIsRejected_thenReturnListOfRejectedBookingDtosOfBooker() {
			// region setup
			User owner = testUtils.makeNewUser(1);
			long ownerId = owner.getId();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> bookings = List.of(
					// future
					testUtils.makeNewAnyFullFastBooking(
							53, testUtils.futureDate, BookingStatus.WAITING, null),
					// current
					testUtils.makeNewAnyFullFastBooking(
							52, now.minusHours(5), BookingStatus.APPROVED, null),
					// rejected
					testUtils.makeNewAnyFullFastBooking(
							51, testUtils.pastDate.plusDays(30), BookingStatus.REJECTED, null),
					// past
					testUtils.makeNewAnyFullFastBooking(
							50, testUtils.pastDate, BookingStatus.APPROVED)
			);
			bookings.forEach(booking -> booking.getItem().setOwner(owner));
			bookings = bookings.stream()
					.filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
					.toList();
			List<BookingDto> bookingDtos = bookings.stream()
					.map(BookingMapper::toBookingDto)
					.toList();
			// endregion setup

			doNothing().when(utilService).checkUser(ownerId);
			when(bookingRepository.findAllByOwnerAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED))
					.thenReturn(bookings);

			List<BookingDto> resultDtoList = bookingService.getAllBookingsByOwner(ownerId, "rejected");

			assertThat(resultDtoList, equalTo(bookingDtos));
		}

		@Test
		void whenUserIsBooker_andStatusIsUndefined_thenReturnParameterNotValidException() {
			long ownerId = 1L;

			doNothing().when(utilService).checkUser(ownerId);

			assertThrows(ParameterNotValidException.class, () ->
					bookingService.getAllBookingsByOwner(ownerId, "XXX"));
		}
	}
}