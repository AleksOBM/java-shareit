package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateOfBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UtilService;
import ru.practicum.shareit.util.error.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	MockMvc mvc;

	@Autowired
	BookingController bookingController;

	@MockitoBean
	BookingService bookingService;

	@MockitoBean
	UtilService utilService;

	final TestUtils testUtils = new TestUtils();

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(bookingController)
				.setControllerAdvice(new ErrorHandler())
				.setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
				.build();
	}

	@Test
	@SneakyThrows
	void addNewBooking_whenBookingIsValid() {
		Booking booking = testUtils.makeNewAnyFullFastBooking(1, testUtils.pastDate, BookingStatus.APPROVED);
		BookingDto bookingDto = BookingMapper.toBookingDto(booking);
		IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
				booking.getItem().getId(), booking.getStart(), booking.getEnd());

		when(bookingService.addNewBooking(booking.getBooker().getId(), incomingBookingDto)).thenReturn(bookingDto);

		String result = mvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", booking.getBooker().getId())
						.content(mapper.writeValueAsString(incomingBookingDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(result, is(mapper.writeValueAsString(bookingDto)));
	}

	@Test
	@SneakyThrows
	void getBooking_whenBookingIsValid() {
		Booking booking = testUtils.makeNewAnyFullFastBooking(1, testUtils.pastDate, BookingStatus.APPROVED);
		BookingDto bookingDto = BookingMapper.toBookingDto(booking);

		when(bookingService.getBooking(booking.getBooker().getId(), booking.getId())).thenReturn(bookingDto);

		String result = mvc.perform(get("/bookings/{bookingId}", booking.getId())
						.header("X-Sharer-User-Id", booking.getBooker().getId()))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(result, is(mapper.writeValueAsString(bookingDto)));
	}

	@Test
	@SneakyThrows
	void getAllBookingsByBooker_whenBookingIsValid() {
		Booking booking1 = testUtils.makeNewAnyFullFastBooking(1, testUtils.pastDate, BookingStatus.APPROVED);
		Booking booking2 = testUtils.makeNewAnyFullFastBooking(2, testUtils.pastDate, BookingStatus.APPROVED);
		booking2.setBooker(booking1.getBooker());
		List<BookingDto> bookingDtos = List.of(
				BookingMapper.toBookingDto(booking1),
				BookingMapper.toBookingDto(booking2)
		);
		StateOfBooking state = StateOfBooking.ALL;

		when(bookingService.getAllBookingsByBooker(booking1.getBooker().getId(), String.valueOf(state)))
				.thenReturn(bookingDtos);

		String result = mvc.perform(get("/bookings?state={state}", state)
						.header("X-Sharer-User-Id", booking1.getBooker().getId()))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(result, is(mapper.writeValueAsString(bookingDtos)));
	}

	@Test
	@SneakyThrows
	void getAllBookingsByOwner_whenBookingIsValid() {
		User owner = testUtils.makeNewUser(1);
		Booking booking1 = testUtils.makeNewAnyFullBooking(
				10,
				testUtils.makeNewUser(2),
				owner,
				testUtils.pastDate,
				BookingStatus.APPROVED
		);
		Booking booking2 = testUtils.makeNewAnyFullBooking(
				11,
				testUtils.makeNewUser(3),
				owner,
				testUtils.futureDate,
				BookingStatus.APPROVED
		);
		List<BookingDto> bookingDtos = List.of(
				BookingMapper.toBookingDto(booking1),
				BookingMapper.toBookingDto(booking2)
		);
		StateOfBooking state = StateOfBooking.ALL;

		when(bookingService.getAllBookingsByOwner(owner.getId(), String.valueOf(state)))
				.thenReturn(bookingDtos);

		String result = mvc.perform(get("/bookings/owner?state={state}", state)
						.header("X-Sharer-User-Id", owner.getId()))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(result, is(mapper.writeValueAsString(bookingDtos)));
	}

	@Test
	@SneakyThrows
	void approveBooking_whenBookingIsValid() {
		Booking booking = testUtils.makeNewAnyFullFastBooking(1, testUtils.pastDate, BookingStatus.APPROVED);
		BookingDto bookingDto = BookingMapper.toBookingDto(booking);


		when(bookingService.approveBooking(booking.getItem().getOwner().getId(), booking.getId(), true))
				.thenReturn(bookingDto);

		String result = mvc.perform(
						patch("/bookings/{bookingId}?approved={approved}", booking.getId(), true)
								.header("X-Sharer-User-Id", booking.getItem().getOwner().getId()))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(result, is(mapper.writeValueAsString(bookingDto)));
	}

}
