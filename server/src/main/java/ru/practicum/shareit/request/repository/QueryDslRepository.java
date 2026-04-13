package ru.practicum.shareit.request.repository;

import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.QItemRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QueryDslRepository {

	final JPQLQueryFactory queryFactory;

	final QItem qItem = QItem.item;
	final QItemRequest qItemRequest = QItemRequest.itemRequest;

	public Map<Long, List<Item>> getMapOfItemsByRequestId(List<Long> requestIds) {
		return queryFactory
				.selectFrom(qItem)
				.where(qItem.request.id.in(requestIds))
				.fetch()
				.stream()
				.collect(Collectors.groupingBy(item -> item.getRequest().getId()));
	}

	public List<ItemRequestDto> getAllRequestsWitoutByUser(Long userId) {
		return queryFactory.selectFrom(qItemRequest)
				.where(qItemRequest.requestor.id.notIn(userId))
				.orderBy(qItemRequest.createdDate.desc())
				.fetch()
				.stream()
				.map(ItemRequestMapper::toDto)
				.toList();
	}

	public List<Item> getAllItemsByRequest(Long requestId) {
		return queryFactory.selectFrom(qItem).where(qItem.request.id.eq(requestId)).fetch();
	}
}
