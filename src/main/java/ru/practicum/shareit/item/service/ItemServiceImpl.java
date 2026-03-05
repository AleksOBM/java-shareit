package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Collections;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	ItemRepository itemRepository;
	UserRepository userRepository;

	@Override
	public ItemDto getItem(long itemId) {
		return ItemMapper.toItemDto(getItemWithCheckPresent(itemId));
	}

	@Override
	public List<ItemDto> getAllItemsOfUser(long userId) {
		checkUser(userId);

		return itemRepository.findAll().stream()
				.filter(item -> item.getOwner().getId() == userId)
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	public List<ItemDto> search(long userId, String text) {
		if (text == null || text.isEmpty()) {
			return Collections.emptyList();
		}
		String searchText = text.toLowerCase();
		return itemRepository.findAll().stream()
				.filter(Item::isAvailable)
				.filter(item -> item.getName().toLowerCase().contains(searchText) ||
						item.getDescription().toLowerCase().contains(searchText)
				)
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	public ItemDto addNewItem(Long userId, ItemDto itemDto) {
		User user = getUserWithCheckPresent(userId);
		return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, user)));
	}

	@Override
	public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
		checkUser(userId);
		Item oldItem = getItemWithCheckPresentAndOwner(itemId, userId);

		Item item = new Item(
				itemId,
				itemDto.getName() == null ? oldItem.getName() : itemDto.getName(),
				itemDto.getDescription() == null ? oldItem.getDescription() : itemDto.getDescription(),
				itemDto.getAvailable() == null ? oldItem.isAvailable() : itemDto.getAvailable(),
				getUserWithCheckPresent(userId),
				null
		);
		return ItemMapper.toItemDto(itemRepository.update(itemId, item));
	}

	@Override
	public void deleteItem(long userId, long itemId) {
		checkUser(userId);
		getItemWithCheckPresentAndOwner(itemId, userId);
		itemRepository.remove(userId, itemId);
	}

	private void checkUser(long userId) {
		if (userRepository.checkUserIsNotPresent(userId)) {
			throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
		}
	}

	private User getUserWithCheckPresent(long userId) {
		return userRepository.findOne(userId).orElseThrow(() ->
				new NotFoundException("Пользователь с id=" + userId + " не найден.")
		);
	}

	private Item getItemWithCheckPresent(long itemId) {
		return itemRepository.getOne(itemId).orElseThrow(() ->
				new NotFoundException("Вещь с id=" + itemId + " не найдена.")
		);
	}

	private Item getItemWithCheckPresentAndOwner(long itemId, long userId) {
		Item item = getItemWithCheckPresent(itemId);
		if (item.getOwner().getId() == userId) {
			return item;
		}
		throw new NotFoundException("Пользователь с id=" + userId + " не является владельцем вещи с id=" + itemId);
	}
}
