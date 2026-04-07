package ru.practicum.shareit.request.service;

import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.request.dto.ItemRequestBigDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.QItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UtilService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

	final ItemRequestRepository itemRequestRepository;
	final JPQLQueryFactory queryFactory;
	final UtilService utilService;

	@Override
	public ItemRequestDto addNewRequest(long userId, ItemRequestDto requestDto) {
		User user = utilService.getUser(userId);
		ItemRequest itemRequest = ItemRequestMapper.toEntity(requestDto, user);
		itemRequest = itemRequestRepository.save(itemRequest);
		return ItemRequestMapper.toDto(itemRequest);
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

		QItem qItem = QItem.item;
		Map<Long, List<Item>> itemsByRequestId = queryFactory
				.selectFrom(qItem)
				.where(qItem.request.id.in(requestIds))
				.fetch()
				.stream()
				.collect(Collectors.groupingBy(item -> item.getRequest().getId()));

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
		QItemRequest qItemRequest = QItemRequest.itemRequest;
		return queryFactory.selectFrom(qItemRequest)
				.where(qItemRequest.requestor.id.notIn(userId))
				.orderBy(qItemRequest.createdDate.desc())
				.fetch()
				.stream()
				.map(ItemRequestMapper::toDto)
				.toList();
	}

	@Override
	public ItemRequestBigDto getRequestById(long requestId) {
		QItem qItem = QItem.item;
		List<Item> items = queryFactory.selectFrom(qItem).where(qItem.request.id.eq(requestId)).fetch();
		ItemRequest request = utilService.getItemRequest(requestId);
		return ItemRequestMapper.toBigDto(request, items);
	}
}
