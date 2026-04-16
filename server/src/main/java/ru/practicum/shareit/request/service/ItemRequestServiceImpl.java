package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestBigDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.QueryDslRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UtilService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

	final ItemRequestRepository itemRequestRepository;
	final QueryDslRepository queryDslRepository;
	final UtilService utilService;

	@Override
	public ItemRequestDto addNewRequest(long userId, ItemRequestDto requestDto) {
		User user = utilService.getUser(userId);
		ItemRequest itemRequest = ItemRequestMapper.toEntity(requestDto, user);
		ItemRequest resultRequest = itemRequestRepository.save(itemRequest);
		return ItemRequestMapper.toDto(resultRequest);
	}

	@Override
	public List<ItemRequestBigDto> getRequestListByUser(long userId) {
		utilService.checkUser(userId);

		List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(userId);
		if (itemRequests.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> requestIds = itemRequests.stream()
				.map(ItemRequest::getId)
				.toList();

		Map<Long, List<Item>> itemsByRequestId = queryDslRepository.getMapOfItemsByRequestId(requestIds);

		return itemRequests.stream()
				.map(itemRequest -> ItemRequestMapper.toBigDto(
						itemRequest,
						itemsByRequestId.getOrDefault(itemRequest.getId(), Collections.emptyList())
				))
				.sorted(Comparator.comparing(ItemRequestBigDto::getCreatedDate).reversed())
				.toList();
	}

	@Override
	public List<ItemRequestDto> getAllRequests(long userId) {
		utilService.checkUser(userId);
		return queryDslRepository.getAllRequestsWithoutThisUserRequests(userId).stream()
				.map(ItemRequestMapper::toDto)
				.toList();
	}

	@Override
	public ItemRequestBigDto getRequestById(long requestId) {
		List<Item> items = queryDslRepository.getAllItemsByRequest(requestId);
		ItemRequest request = utilService.getItemRequest(requestId);
		return ItemRequestMapper.toBigDto(request, items);
	}
}
