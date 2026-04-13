package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.UtilService;
import ru.practicum.shareit.util.error.ErrorHandler;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = ShareItServer.class)
public class ItemIntegationTest {

	final TestUtils testUtils = new TestUtils();

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	@Autowired
	ItemController itemController;

	@Autowired
	ItemService itemService;

	@Autowired
	UtilService utilService;

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

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(itemController)
				.setControllerAdvice(new ErrorHandler())
				.setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
				.build();
	}

	@Nested
	class GetItem {

		@Test
		@SneakyThrows
		void whenUserIdIsEqualsOwnerId_htenReturnsItemWithBookingDates() {
			User owner = testUtils.makeNewUser(1);
			Item item = testUtils.makeNewItem(100, owner, null);

			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
			when(bookingRepository.findLastBookingDate(item.getId())).thenReturn(testUtils.pastDate);
			when(bookingRepository.findNextBookingDate(item.getId())).thenReturn(testUtils.futureDate);

			mvc.perform(get("/items/{itemid}", item.getId())
							.header("X-Sharer-User-Id", owner.getId()))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					// region .andExpect item
					.andExpect(jsonPath("$.id", is(item.getId()), Long.class))
					.andExpect(jsonPath("$.name", is(item.getName()), String.class))
					.andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
					.andExpect(jsonPath("$.available", is(item.isAvailable()), Boolean.class))
					.andExpect(jsonPath("$.request", nullValue()))
					.andExpect(jsonPath("$.lastBooking", is(testUtils.pastDate.toString()), String.class))
					.andExpect(jsonPath("$.nextBooking", is(testUtils.futureDate.toString()), String.class))
					.andExpect(jsonPath("$.comments", empty()));
			// endregion .andExpect item
		}

		@Test
		@SneakyThrows
		void whenUserIdIsNotEqualsOwnerId_htenReturnsItemWithNullableBookingDates() {
			User owner = testUtils.makeNewUser(1);
			Item item = testUtils.makeNewItem(100, owner, null);

			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

			mvc.perform(get("/items/{itemid}", item.getId())
							.header("X-Sharer-User-Id", item.getId()))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					// region .andExpect item
					.andExpect(jsonPath("$.id", is(item.getId()), Long.class))
					.andExpect(jsonPath("$.name", is(item.getName()), String.class))
					.andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
					.andExpect(jsonPath("$.available", is(item.isAvailable()), Boolean.class))
					.andExpect(jsonPath("$.request", nullValue()))
					.andExpect(jsonPath("$.lastBooking", nullValue()))
					.andExpect(jsonPath("$.nextBooking", nullValue()))
					.andExpect(jsonPath("$.comments", empty()));
			// endregion .andExpect item

			verify(bookingRepository, never()).findLastBookingDate(item.getId());
			verify(bookingRepository, never()).findNextBookingDate(item.getId());
		}
	}


	@Nested
	class GetAllItemsOfUser {

		@Test
		@SneakyThrows
		void returnResponseWithStatusOkAndActualItems() {
			Booking booking1 = testUtils.makeNewAnyFullFastBooking(
					10,
					testUtils.pastDate,
					BookingStatus.APPROVED
			);

			User booker = booking1.getBooker();
			User owner = booking1.getItem().getOwner();

			Booking booking2 = testUtils.makeNewAnyFullBooking(
					11,
					booker,
					owner,
					testUtils.futureDate,
					BookingStatus.WAITING
			);

			Item item1 = booking1.getItem();
			Item item2 = booking2.getItem();

			ItemRequest request1 = item1.getRequest();
			ItemRequest request2 = item2.getRequest();

			Comment comment = testUtils.makeNewComment(
					90,
					booking1.getItem(),
					booker,
					testUtils.pastDate.plusHours(11)
			);

			when(userRepository.existsById(owner.getId())).thenReturn(true);
			when(itemRepository.findAllByOwner_Id(owner.getId())).thenReturn(List.of(item1, item2));
			when(bookingRepository.findAllBookingByItemIdIn(Set.of(item1.getId(), item2.getId())))
					.thenReturn(List.of(booking1, booking2));
			when(commentRepository.findAllByItemIdIn(Set.of(item1.getId(), item2.getId())))
					.thenReturn(List.of(comment));

			mvc.perform(get("/items").header("X-Sharer-User-Id", owner.getId()))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andExpect(jsonPath("$", hasSize(2)))
					// region .andExpect item 1
					.andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
					.andExpect(jsonPath("$[0].name", is(item1.getName())))
					.andExpect(jsonPath("$[0].description", is(item1.getDescription())))
					.andExpect(jsonPath("$[0].available", is(item1.isAvailable())))
					.andExpect(jsonPath("$[0].request", is(request1.getId()), Long.class))
					.andExpect(jsonPath("$[0].lastBooking", is(booking1.getStart().toString())))
					.andExpect(jsonPath("$[0].nextBooking", nullValue()))
					.andExpect(jsonPath("$[0].comments[0].id", is(comment.getId()), Long.class))
					.andExpect(jsonPath("$[0].comments[0].text", is(comment.getText())))
					.andExpect(jsonPath("$[0].comments[0].itemId", is(comment.getItem().getId()), Long.class))
					.andExpect(jsonPath("$[0].comments[0].created", is(comment.getCreatedDate().toString())))
					.andExpect(jsonPath("$[0].comments[0].authorName", is(comment.getAuthor().getName())))
					// endregion .andExpect item 1
					// region .andExpect item 2
					.andExpect(jsonPath("$[1].id", is(item2.getId()), Long.class))
					.andExpect(jsonPath("$[1].name", is(item2.getName())))
					.andExpect(jsonPath("$[1].description", is(item2.getDescription())))
					.andExpect(jsonPath("$[1].available", is(item2.isAvailable())))
					.andExpect(jsonPath("$[1].request", is(request2.getId()), Long.class))
					.andExpect(jsonPath("$[1].lastBooking", nullValue()))
					.andExpect(jsonPath("$[1].nextBooking", is(booking2.getStart().toString())))
					.andExpect(jsonPath("$[1].comments", empty()));
			// endregion .andExpect item 2
		}
	}

	@Nested
	class Search {

		@Test
		@SneakyThrows
		void whenTextIsValid_returnsListWithActualItems() {
			Item item1 = testUtils.makeNewFastItem(10);
			Item item2 = testUtils.makeNewFastItem(11);
			Item item3 = testUtils.makeNewFastItem(12);
			String text = item1.getName();
			item2.setDescription(text + item2.getName());
			item3.setName("hghgh" + text);
			Long seacherId = item3.getOwner().getId();

			when(userRepository.existsById(seacherId)).thenReturn(true);
			when(itemRepository.search(item1.getName())).thenReturn(List.of(item1, item2));

			mvc.perform(get("/items/search?text={text}", item1.getName())
					.header("X-Sharer-User-Id", seacherId))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andExpect(jsonPath("$", hasSize(2)));
		}

		@Test
		@SneakyThrows
		void whenTextIsEmpty_returnsEmptyList() {
			when(userRepository.existsById(1L)).thenReturn(true);

			mvc.perform(get("/items/search?text={text}", "")
							.header("X-Sharer-User-Id", 1))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andExpect(jsonPath("$", hasSize(0)));
		}

		@Test
		@SneakyThrows
		void whenUserIsNotFound_returnsResponseStatusForbidden() {
			when(userRepository.existsById(1L)).thenReturn(false);

			mvc.perform(get("/items/search?text={text}", "")
							.header("X-Sharer-User-Id", 1))
					.andExpect(status().isForbidden())
					.andDo(MockMvcResultHandlers.print());
		}
	}

	@Nested
	class AddNewItem {

		@Test
		@SneakyThrows
		void addNewItem() {
		}
	}

	@Nested
	class UpdateItem {

		@Test
		@SneakyThrows
		void updateItem() {
		}
	}

	@Nested
	class DeleteItem {

		@Test
		@SneakyThrows
		void deleteItem() {
		}
	}

	@Nested
	class AddComment {

		@Test
		@SneakyThrows
		void addComment() {
		}
	}
}
