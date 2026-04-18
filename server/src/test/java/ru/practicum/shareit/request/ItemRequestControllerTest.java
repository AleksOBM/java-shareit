package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestBigDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UtilService;
import ru.practicum.shareit.util.error.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	MockMvc mvc;

	@Autowired
	ItemRequestController controller;

	@MockitoBean
	ItemRequestService requestService;

	@MockitoBean
	UtilService utilService;

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
	void addNewRequest() {
		User requestor = testUtils.makeNewUser(1);
		ItemRequest itemRequest = testUtils.makeNewItemRequest(50, requestor);
		ItemRequestDto requestDto = ItemRequestDto.builder()
				.description(itemRequest.getDescription())
				.createdDate(itemRequest.getCreatedDate())
				.build();
		ItemRequestDto resultRequestDto = ItemRequestMapper.toDto(itemRequest);

		when(requestService.addNewRequest(requestor.getId(), requestDto)).thenReturn(resultRequestDto);

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

		assertEquals(result, mapper.writeValueAsString(ItemRequestMapper.toDto(itemRequest)));
	}

	@Test
	@SneakyThrows
	void getRequestListByUser() {
		User requestor = testUtils.makeNewUser(5);
		Item item1 = testUtils.makeNewFastItem(40);
		Item item2 = testUtils.makeNewFastItem(41);
		Item item3 = testUtils.makeNewFastItem(42);
		List<ItemRequestBigDto> bigDtos = List.of(
				ItemRequestMapper
						.toBigDto(testUtils.makeNewItemRequest(70, requestor), List.of(item1, item3)),
				ItemRequestMapper
						.toBigDto(testUtils.makeNewItemRequest(71, requestor), List.of(item1, item2))
		);

		when(requestService.getRequestListByUser(requestor.getId())).thenReturn(bigDtos);

		String result = mvc.perform(get("/requests")
						.header("X-Sharer-User-Id", requestor.getId()))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(result, mapper.writeValueAsString(bigDtos));
	}

	@Test
	@SneakyThrows
	void getAllRequests() {
		User user = testUtils.makeNewUser(1);
		List<ItemRequest> requests = List.of(
				testUtils.makeNewItemRequest(10, testUtils.makeNewUser(2)),
				testUtils.makeNewItemRequest(11, testUtils.makeNewUser(3))
		);
		List<ItemRequestDto> requestDtos = requests.stream()
				.map(ItemRequestMapper::toDto)
				.toList();

		when(requestService.getAllRequests(user.getId())).thenReturn(requestDtos);

		String result = mvc.perform(get("/requests/all")
						.header("X-Sharer-User-Id", user.getId()))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(result, mapper.writeValueAsString(requestDtos));
	}

	@Test
	@SneakyThrows
	void getRequestById() {
		ItemRequest itemRequest = testUtils.makeNewItemRequest(50, testUtils.makeNewUser(1));
		Item item = testUtils.makeNewFastItem(30);
		ItemRequestBigDto bigDto = ItemRequestMapper.toBigDto(itemRequest, List.of(item));

		when(requestService.getRequestById(itemRequest.getId())).thenReturn(bigDto);

		String result = mvc.perform(get("/requests/{requestId}", itemRequest.getId())
						.header("X-Sharer-User-Id", anyLong()))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(result, mapper.writeValueAsString(bigDto));
	}
}