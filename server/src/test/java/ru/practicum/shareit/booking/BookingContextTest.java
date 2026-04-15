package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.repository.QueryDslRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.error.ErrorHandler;
import ru.practicum.shareit.util.error.ErrorResponse;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
public class BookingContextTest {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	@Autowired
	BookingController controller;

	// region mocks
	@MockitoBean
	UserRepository userRepository;

	@MockitoBean
	ItemRepository itemRepository;

	@MockitoBean
	ItemRequestRepository itemRequestRepository;

	@MockitoBean
	BookingRepository bookingRepository;

	@MockitoBean
	CommentRepository commentRepository;

	@MockitoBean
	QueryDslRepository queryDslRepository;

	@MockitoBean
	EntityManagerFactory entityManagerFactory;
	// endregion mocks

	final TestUtils testUtils = new TestUtils();

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(controller)
				.setControllerAdvice(new ErrorHandler())
				.setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
				.build();
	}

	@Test
	@SneakyThrows
	void approveBooking_whenBookingWasCanseled_thenReturnErrorResponse() {
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

		when(userRepository.existsById(ownerId)).thenReturn(true);
		when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(inBaseBooking));

		String result = mvc.perform(
						patch("/bookings/{bookingId}?approved={approved}", bookingId, true)
								.header("X-Sharer-User-Id", ownerId))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(result, is(
				mapper.writeValueAsString(new ErrorResponse("Бронирование уже было отменено создателем"))
		));

		verify(bookingRepository, never()).save(any(Booking.class));
	}

	@Test
	@SneakyThrows
	void addNewBooking_whenItenIsNotApprouved_thenReturnErrorResponse() {
		Booking booking = testUtils.makeNewAnyFullFastBooking(
				1,
				testUtils.futureDate,
				BookingStatus.WAITING,
				null
		);
		booking.getItem().setAvailable(false);
		IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
				booking.getItem().getId(), booking.getStart(), booking.getEnd());

		when(userRepository.findById(booking.getBooker().getId())).thenReturn(Optional.of(booking.getBooker()));
		when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.of(booking.getItem()));

		String result = mvc.perform(
						post("/bookings")
								.header("X-Sharer-User-Id", booking.getBooker().getId())
								.content(mapper.writeValueAsString(incomingBookingDto))
								.characterEncoding(StandardCharsets.UTF_8)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(result, is(
						mapper.writeValueAsString(
								new ErrorResponse("Вещь с id=" + booking.getItem().getId() + " не доступна для аренды"))
				));

		verify(bookingRepository, never()).save(any(Booking.class));
	}
}
