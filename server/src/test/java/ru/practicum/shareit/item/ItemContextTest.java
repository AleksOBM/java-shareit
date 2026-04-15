package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.repository.QueryDslRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.error.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
public class ItemContextTest {

	final TestUtils testUtils = new TestUtils();

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	@Autowired
	ItemController itemController;

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
			when(itemRepository.search(item1.getName())).thenReturn(List.of(item1, item2, item3));

			// region mvc test
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

			// region mvc test
			mvc.perform(get("/items/search?text={text}", "")
							.header("X-Sharer-User-Id", 1))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andExpect(jsonPath("$", hasSize(0)));
			// endregion mvc test
		}

		@Test
		@SneakyThrows
		void whenUserIsNotFound_returnsResponseStatusForbidden() {
			when(userRepository.existsById(1L)).thenReturn(false);

			// region mvc test
			mvc.perform(get("/items/search?text={text}", "")
							.header("X-Sharer-User-Id", 1))
					.andExpect(status().isForbidden())
					.andDo(MockMvcResultHandlers.print());
			// endregion mvc test
		}
	}

	@Nested
	class AddNewItem {

		@Test
		@SneakyThrows
		void whenRequestIsNotNull_returnItemWithRequestId() {
			Item item = testUtils.makeNewFastItem(10);

			when(userRepository.findById(item.getOwner().getId())).thenReturn(Optional.of(item.getOwner()));
			when(itemRequestRepository.findById(item.getRequest().getId()))
					.thenReturn(Optional.ofNullable(item.getRequest()));
			when(itemRepository.save(item)).thenReturn(item);

			// region mvc test
			mvc.perform(post("/items")
							.header("X-Sharer-User-Id", item.getOwner().getId())
							.content(mapper.writeValueAsString(ItemMapper.toDto(item)))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andExpect(jsonPath("$.id", is(item.getId()), Long.class))
					.andExpect(jsonPath("$.name", is(item.getName()), String.class))
					.andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
					.andExpect(jsonPath("$.available", is(item.isAvailable()), Boolean.class))
					.andExpect(jsonPath("$.requestId", is(item.getRequest().getId()), Long.class));
			// endregion mvc test
		}

		@Test
		@SneakyThrows
		void whenRequestIsNull_returnItemWithNullableRequestId() {
			Item item = testUtils.makeNewFastItem(10);
			item.setRequest(null);

			when(userRepository.findById(item.getOwner().getId())).thenReturn(Optional.of(item.getOwner()));
			when(itemRepository.save(item)).thenReturn(item);

			// region mvc test
			mvc.perform(post("/items")
							.header("X-Sharer-User-Id", item.getOwner().getId())
							.content(mapper.writeValueAsString(ItemMapper.toDto(item)))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andExpect(jsonPath("$.id", is(item.getId()), Long.class))
					.andExpect(jsonPath("$.name", is(item.getName()), String.class))
					.andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
					.andExpect(jsonPath("$.available", is(item.isAvailable()), Boolean.class))
					.andExpect(jsonPath("$.requestId", nullValue()));
			// endregion mvc test

			verify(itemRequestRepository, never()).findById(anyLong());
		}
	}

	@Nested
	class UpdateItem {

		@Test
		@SneakyThrows
		void updateAllfields_whenUserIsOwnerOfItem_thenReturnsUpdatableItem() {
			// region setup
			Item item = testUtils.makeNewFastItem(10);
			ItemDto itemDto = ItemDto.builder()
					.name("this is new name")
					.description("this is new description")
					.available(false)
					.requestId(100L)
					.build();
			ItemRequest newRequest = testUtils.makeNewItemRequest(100, testUtils.makeNewUser(200));
			Item newItem = testUtils.makeCopyOfItem(item)
					.setName(itemDto.getName())
					.setDescription(itemDto.getDescription())
					.setAvailable(itemDto.getAvailable())
					.setRequest(newRequest);
			// endregion setup

			when(userRepository.existsById(item.getOwner().getId())).thenReturn(true);
			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
			when(userRepository.findById(item.getOwner().getId())).thenReturn(Optional.of(item.getOwner()));
			when(itemRequestRepository.findById(item.getRequest().getId()))
					.thenReturn(Optional.ofNullable(item.getRequest()));
			when(itemRequestRepository.findById(newItem.getRequest().getId())).thenReturn(Optional.of(newRequest));
			when(itemRepository.save(newItem)).thenReturn(newItem);

			// region mvc test
			mvc.perform(patch("/items/{itemId}", item.getId())
							.header("X-Sharer-User-Id", item.getOwner().getId())
							.content(mapper.writeValueAsString(itemDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andExpect(jsonPath("$.id", is(newItem.getId()), Long.class))
					.andExpect(jsonPath("$.name", is(newItem.getName()), String.class))
					.andExpect(jsonPath("$.description", is(newItem.getDescription()), String.class))
					.andExpect(jsonPath("$.available", is(newItem.isAvailable()), Boolean.class))
					.andExpect(jsonPath("$.requestId", is(newItem.getRequest().getId()), Long.class));
			// endregion mvc test
		}

		@Test
		@SneakyThrows
		void updateNullfields_whenUserIsOwnerOfItem_thenReturnsUpdatableItem() {
			// region setup
			Item item = testUtils.makeNewFastItem(10);
			ItemDto itemDto = ItemDto.builder().build();
			Item newItem = testUtils.makeCopyOfItem(item);
			// endregion setup

			when(userRepository.existsById(item.getOwner().getId())).thenReturn(true);
			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
			when(userRepository.findById(item.getOwner().getId())).thenReturn(Optional.of(item.getOwner()));
			when(itemRequestRepository.findById(item.getRequest().getId()))
					.thenReturn(Optional.ofNullable(item.getRequest()));
			when(itemRepository.save(newItem)).thenReturn(newItem);

			// region mvc test
			mvc.perform(patch("/items/{itemId}", item.getId())
							.header("X-Sharer-User-Id", item.getOwner().getId())
							.content(mapper.writeValueAsString(itemDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andExpect(jsonPath("$.id", is(newItem.getId()), Long.class))
					.andExpect(jsonPath("$.name", is(newItem.getName()), String.class))
					.andExpect(jsonPath("$.description", is(newItem.getDescription()), String.class))
					.andExpect(jsonPath("$.available", is(newItem.isAvailable()), Boolean.class))
					.andExpect(jsonPath("$.requestId", is(newItem.getRequest().getId()), Long.class));
			// endregion mvc test
		}

		@Test
		@SneakyThrows
		void whenUserIsNotOwnerOfItem_thenReturnsResponseWithStatuNotFound() {
			Item item = testUtils.makeNewFastItem(10);
			ItemDto itemDto = ItemDto.builder().name("this is new name").build();
			Item newItem = testUtils.makeCopyOfItem(item).setName(itemDto.getName());

			when(userRepository.existsById(item.getOwner().getId() + 1)).thenReturn(true);
			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

			// region mvc test
			mvc.perform(patch("/items/{itemId}", item.getId())
							.header("X-Sharer-User-Id", item.getOwner().getId() + 1)
							.content(mapper.writeValueAsString(itemDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andDo(MockMvcResultHandlers.print());
			// endregion mvc test

			verify(userRepository, never()).findById(item.getOwner().getId() + 1);
			verify(itemRequestRepository, never()).findById(item.getRequest().getId());
			verify(itemRepository, never()).save(newItem);
		}
	}

	@Nested
	class DeleteItem {

		@Test
		@SneakyThrows
		void wnenUserIsOwnerOfItem_thenReturnsResponseWithStatusOk() {
			Item item = testUtils.makeNewFastItem(10);
			when(userRepository.existsById(item.getOwner().getId())).thenReturn(true);
			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
			doNothing().when(itemRepository).deleteById(item.getId());

			// region mvc test
			mvc.perform(delete("/items/{itemId}", item.getId())
							.header("X-Sharer-User-Id", item.getOwner().getId())
					)
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print());
			// endregion mvc test
		}

		@Test
		@SneakyThrows
		void wnenUserIsNotOwnerOfItem_thenReturnsResponseWithStatuNotFound() {
			Item item = testUtils.makeNewFastItem(10);
			when(userRepository.existsById(item.getOwner().getId() + 1)).thenReturn(true);
			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

			// region mvc test
			mvc.perform(delete("/items/{itemId}", item.getId())
							.header("X-Sharer-User-Id", item.getOwner().getId() + 1)
					)
					.andExpect(status().isNotFound())
					.andDo(MockMvcResultHandlers.print());
			// endregion mvc test

			verify(itemRepository, never()).deleteById(item.getId());
		}
	}

	@Nested
	class AddComment {

		@Test
		@SneakyThrows
		void whenUserIsNotOwnerOfItem_thenReturnsResponseWithStatusOkAndBodyOfActualComment() {
			Booking booking1 = testUtils.makeNewAnyFullFastBooking(10, testUtils.pastDate, BookingStatus.APPROVED);
			Booking booking2 = testUtils.makeNewAnyFullFastBooking(11, testUtils.pastDate, BookingStatus.APPROVED);
			booking2.setItem(booking1.getItem());
			Item item = booking1.getItem();
			User booker = booking1.getBooker();
			String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
			Comment comment = testUtils.makeNewComment(100, item, booker, LocalDateTime.parse(date));
			CommentDto commentDto = CommentMapper.toDto(comment);

			when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
			when(bookingRepository.findAllBookingByItem_Id(item.getId()))
					.thenReturn(List.of(booking1, booking2));
			when(commentRepository.save(comment)).thenReturn(comment);

			// region mvc test
			mvc.perform(post("/items/{itemId}/comment", item.getId())
							.header("X-Sharer-User-Id", booker.getId())
							.content(mapper.writeValueAsString(commentDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
					.andExpect(jsonPath("$.itemId", is(item.getId()), Long.class))
					.andExpect(jsonPath("$.authorName", is(booker.getName())))
					.andExpect(jsonPath("$.created", is(comment.getCreatedDate().toString())))
					.andExpect(jsonPath("$.text", is(comment.getText())));
			// endregion mvc test

			verify(commentRepository).save(any());
		}

		@Test
		@SneakyThrows
		void whenUserIsOwnerOfItem_thenReturnsResponseWithStatusForbidden() {
			User owner = testUtils.makeNewUser(10);
			Item item = testUtils.makeNewItem(50, owner, null);
			CommentDto commentDto = CommentDto.builder().text("this is comment").build();

			when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

			// region mvc test
			mvc.perform(post("/items/{itemId}/comment", item.getId())
							.header("X-Sharer-User-Id", owner.getId())
							.content(mapper.writeValueAsString(commentDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isForbidden());
			// endregion mvc test

			verify(bookingRepository, never()).findAllBookingByItem_Id(item.getId());
			verify(commentRepository, never()).save(any());
		}

		@Test
		@SneakyThrows
		void whenBookingIsNotCompleted_thenReturnsResponseWithStatusBadRequest() {
			Booking booking = testUtils.makeNewAnyFullFastBooking(10, testUtils.pastDate, BookingStatus.APPROVED);
			booking.setEnd(testUtils.futureDate);
			Item item = booking.getItem();
			User booker = booking.getBooker();
			CommentDto commentDto = CommentDto.builder().text("this is comment").build();

			when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
			when(bookingRepository.findAllBookingByItem_Id(item.getId()))
					.thenReturn(Collections.singletonList(booking));

			// region mvc test
			mvc.perform(post("/items/{itemId}/comment", item.getId())
							.header("X-Sharer-User-Id", booker.getId())
							.content(mapper.writeValueAsString(commentDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
			// endregion mvc test

			verify(commentRepository, never()).save(any());
		}

		@Test
		@SneakyThrows
		void whenBookingIsLost_thenReturnsResponseWithStatusBadRequest() {
			User booker = testUtils.makeNewUser(10);
			User owner = testUtils.makeNewUser(11);
			Item item = testUtils.makeNewItem(50, owner, null);
			CommentDto commentDto = CommentDto.builder().text("this is comment").build();

			when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
			when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
			when(bookingRepository.findAllBookingByItem_Id(item.getId()))
					.thenReturn(Collections.emptyList());

			// region mvc test
			mvc.perform(post("/items/{itemId}/comment", item.getId())
							.header("X-Sharer-User-Id", booker.getId())
							.content(mapper.writeValueAsString(commentDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
			// endregion mvc test

			verify(commentRepository, never()).save(any());
		}
	}
}
