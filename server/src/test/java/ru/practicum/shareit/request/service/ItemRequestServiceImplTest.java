package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.item.dto.ItemLowDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestBigDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.repository.QueryDslRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UtilService;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

	@Mock
	UtilService utilService;

	@Mock
	ItemRequestRepository itemRequestRepository;

	@Mock
	QueryDslRepository queryDslRepository;

	@InjectMocks
	ItemRequestServiceImpl requestService;

	final TestUtils testUtils = new TestUtils();

	@Nested
	class AddNewRequest {

		@Test
		void whenCreatedDateIsNotNull() {
			// region setup
			ItemRequest request = testUtils.makeNewItemRequest(1, testUtils.makeNewUser(10));
			User requestor = request.getRequestor();
			ItemRequestDto dtoIn = ItemRequestDto.builder()
					.description(request.getDescription())
					.createdDate(request.getCreatedDate())
					.build();
			ItemRequestDto dtoOut = ItemRequestMapper.toDto(request);
			// endregion setup

			when(utilService.getUser(requestor.getId())).thenReturn(requestor);
			when(itemRequestRepository.save(testUtils.makeCopyOfRequest(request).setId(null))).thenReturn(request);

			ItemRequestDto resultDto = requestService.addNewRequest(requestor.getId(), dtoIn);

			assertThat(resultDto, equalTo(dtoOut));
		}

		@Test
		void whenCreatedDateIsNull() {
			// region setup
			ItemRequest request = testUtils.makeNewItemRequest(1, testUtils.makeNewUser(10));
			User requestor = request.getRequestor();
			ItemRequestDto dtoIn = ItemRequestDto.builder()
					.description(request.getDescription())
					.createdDate(null)
					.build();
			ItemRequestDto dtoOut = ItemRequestMapper.toDto(request);
			// endregion setup

			when(utilService.getUser(requestor.getId())).thenReturn(requestor);
			when(itemRequestRepository.save(testUtils.makeCopyOfRequest(request).setId(null))).thenReturn(request);

			ItemRequestDto resultDto = requestService.addNewRequest(requestor.getId(), dtoIn);

			assertThat(resultDto, equalTo(dtoOut));
		}
	}

	@Nested
	class GetRequestListByUser {

		@Test
		void getRequestListByUser() {
			// region setup
			User requestor = testUtils.makeNewUser(5);
			List<Item> items = List.of(
					testUtils.makeNewFastItem(40),
					testUtils.makeNewFastItem(41),
					testUtils.makeNewFastItem(42)
			);

			List<ItemRequest> itemRequests = List.of(
					testUtils.makeNewItemRequest(70, requestor),
					testUtils.makeNewItemRequest(71, requestor)
			);
			List<Long> requestIds = itemRequests.stream().map(ItemRequest::getId).toList();
			List<ItemRequestBigDto> bigDtos = List.of(
					ItemRequestMapper
							.toBigDto(itemRequests.getLast(), List.of(items.get(0), items.get(2))),
					ItemRequestMapper
							.toBigDto(itemRequests.getFirst(), List.of(items.get(0), items.get(1)))
			);
			Map<Long, List<Item>> bigMap = new HashMap<>();
			for (ItemRequestBigDto bigDto : bigDtos) {
				List<Long> itemIds = bigDto.getItemDtos().stream().map(ItemLowDto::id).toList();
				bigMap.put(
						bigDto.getId(),
						items.stream().filter(item -> itemIds.contains(item.getId()))
								.toList()
				);
			}
			// endregion setup

			doNothing().when(utilService).checkUser(requestor.getId());
			when(itemRequestRepository.findAllByRequestor_Id(requestor.getId())).thenReturn(itemRequests);
			when(queryDslRepository.getMapOfItemsByRequestId(requestIds)).thenReturn(bigMap);

			List<ItemRequestBigDto> resultDtos = requestService.getRequestListByUser(requestor.getId());

			assertThat(resultDtos, equalTo(bigDtos));
		}

		@Test
		void getRequestListByUser_thenReturnEmptyList() {
			User requestor = testUtils.makeNewUser(5);

			doNothing().when(utilService).checkUser(requestor.getId());
			when(itemRequestRepository.findAllByRequestor_Id(requestor.getId())).thenReturn(Collections.emptyList());

			List<ItemRequestBigDto> resultDtos = requestService.getRequestListByUser(requestor.getId());

			assertThat(resultDtos, equalTo(Collections.emptyList()));
		}
	}

	@Nested
	class GetAllRequests {

		@Test
		void getAllRequests() {
			// region setup
			Long userId = 1L;
			List<ItemRequest> requests = List.of(
					testUtils.makeNewItemRequest(10, testUtils.makeNewUser()),
					testUtils.makeNewItemRequest(11, testUtils.makeNewUser()),
					testUtils.makeNewItemRequest(12, testUtils.makeNewUser())
			);
			List<ItemRequestDto> dtos = requests.stream().map(ItemRequestMapper::toDto).toList();
			// endregion setup

			doNothing().when(utilService).checkUser(userId);
			when(queryDslRepository.getAllRequestsWitoutByUser(userId)).thenReturn(requests);

			List<ItemRequestDto> resultDtos = requestService.getAllRequests(userId);

			assertThat(resultDtos, equalTo(dtos));
		}
	}

	@Nested
	class GetRequestById {

		@Test
		void getRequestById() {
			// region setup
			ItemRequest request = testUtils.makeNewItemRequest(1, testUtils.makeNewUser(10));
			List<Item> items = List.of(
					testUtils.makeNewFastItem(40),
					testUtils.makeNewFastItem(41),
					testUtils.makeNewFastItem(42)
			);
			ItemRequestBigDto bigDto = ItemRequestMapper.toBigDto(request, items);
			// endregion setup

			when(queryDslRepository.getAllItemsByRequest(request.getId())).thenReturn(items);
			when(utilService.getItemRequest(request.getId())).thenReturn(request);

			ItemRequestBigDto resultDto = requestService.getRequestById(request.getId());

			assertThat(resultDto, equalTo(bigDto));
		}
	}
}