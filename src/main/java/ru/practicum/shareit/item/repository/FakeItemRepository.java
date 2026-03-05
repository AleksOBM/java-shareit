package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.IdentifyService;
import ru.practicum.shareit.util.stub.MethodNotImplemented;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FakeItemRepository implements ItemRepository {

	Map<Long, Item> items =  new HashMap<>();
	IdentifyService identifyService;

	@Override
	public Optional<Item> getOne(long itemId) {
		Item item = items.get(itemId);
		return item == null ? Optional.empty() : Optional.of(item);
	}

	@Override
	public List<Item> findAll() {
		return items.values().stream().toList();
	}

	@Override
	public Item save(Item item) {
		item.setId(identifyService.getNextId(items));
		items.put(item.getId(), item);
		return item;
	}

	@Override
	public Item update(long itemId, Item newItem) {
		items.put(itemId, newItem);
		return newItem;
	}

	@Override
	public boolean checkItemIsNotPresent(long itemId) {
		return !items.containsKey(itemId);
	}

	@Override
	@MethodNotImplemented
	public void remove(long userId, long itemId) {
		items.remove(itemId);
	}
}
