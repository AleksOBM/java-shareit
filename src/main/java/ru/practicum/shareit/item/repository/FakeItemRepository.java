package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.IdentifyService;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.stub.MethodNotImplemented;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FakeItemRepository implements ItemRepository {

	private final Map<Long, Item> items =  new HashMap<>();
	private final IdentifyService identifyService;
	private final UserRepository userRepository;

	@Override
	public Item getOne(long itemId) {
		return items.get(itemId);
	}

	@Override
	public List<Item> findAll() {
		return items.values().stream().toList();
	}

	@Override
	public Item save(long userId, ItemDto itemDto) {
		User user = userRepository.findOne(userId);

		if (user == null) {
			throw new NotFoundException("Пользователь с id=" + userId + "не найден.");
		}

		Item item = new Item(
				identifyService.getNextId(items),
				itemDto.getName(),
				itemDto.getDescription(),
				itemDto.getAvailable(),
				user,
				null
		);

		items.put(item.getId(), item);
		return item;
	}

	@Override
	public Item update(long userId, long itemId, ItemDto itemDto) {
		User user = userRepository.findOne(userId);

		if (user == null) {
			throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
		}

		if (!items.containsKey(itemId)) {
			throw new NotFoundException("Вещь с id=" + itemId + " не найдена.");
		}

		Item oldItem = items.get(itemId);

		Item item = new Item(
				itemId,
				itemDto.getName() == null ?  oldItem.getName() : itemDto.getName(),
				itemDto.getDescription() == null  ?  oldItem.getDescription() : itemDto.getDescription(),
				itemDto.getAvailable() == null ? oldItem.isAvailable() : itemDto.getAvailable(),
				user,
				null
		);

		items.put(item.getId(), item);
		return item;
	}

	@Override
	@MethodNotImplemented
	public void remove(long userId, long itemId) {
		items.remove(itemId);
	}
}
