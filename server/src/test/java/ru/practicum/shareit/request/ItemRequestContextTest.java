package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.repository.QueryDslRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.UtilService;
import ru.practicum.shareit.util.error.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = ShareItServer.class)
class ItemRequestContextTest {

	final TestUtils testUtils = new TestUtils();

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	@Autowired
	ItemRequestController itemRequestController;

	@Autowired
	ItemRequestService itemRequestService;

	@Autowired
	UtilService utilService;

	// region repositories

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

	// endregion repositories

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(itemRequestController)
				.setControllerAdvice(new ErrorHandler())
				.setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
				.build();
	}

	@Nested
	class AddNewRequest {

		@Test
		@SneakyThrows
		void addNewRequest() {
			User requestor = testUtils.makeNewUser(1);
			ItemRequest itemRequest = testUtils.makeNewItemRequest(1, requestor);
			ItemRequestDto requestDto = ItemRequestDto.builder()
					.description(itemRequest.getDescription())
					.createdDate(itemRequest.getCreatedDate())
					.build();

			when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
			when(itemRequestRepository.save(ItemRequestMapper.toEntity(requestDto, requestor))).thenReturn(itemRequest);

			// region mvc test
			String result = mvc.perform(post("/requests")
							.header("X-Sharer-User-Id", requestor.getId())
							.content(mapper.writeValueAsString(requestDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andReturn()
					.getResponse()
					.getContentAsString();
			// endregion mvc test

			assertEquals(result, mapper.writeValueAsString(ItemRequestMapper.toDto(itemRequest)));
		}
	}

	@Nested
	class GetRequestListByUser {

		@Test
		void getRequestListByUser() {
			User requestor = testUtils.makeNewUser(1);

			List<ItemRequest> itemRequests = List.of(
			testUtils.makeNewItemRequest(1, requestor),
			testUtils.makeNewItemRequest(2, testUtils.makeNewUser(2)),
			testUtils.makeNewItemRequest(3, requestor)
			);

			when(userRepository.existsById(requestor.getId())).thenReturn(true);
			when(itemRequestRepository.findAllByRequestor_Id(requestor.getId())).thenReturn(itemRequests);

		}
	}

	@Nested
	class GetAllRequests {

		@Test
		void getAllRequests() {
		}
	}

	@Nested
	class GetRequestById {

		@Test
		void getRequestById() {
		}
	}
}