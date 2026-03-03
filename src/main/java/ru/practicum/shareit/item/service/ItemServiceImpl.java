package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;

	@Override
	public ItemDto grtItem(long itemId) {
		return ItemMapper.toItemDto(itemRepository.getOne(itemId));
	}

	@Override
	public List<ItemDto> getAllItemsOfUser(long userId) {
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
				.filter(item -> item.getOwner().getId() == userId)
				.filter(item -> item.getName().toLowerCase().contains(searchText) ||
						item.getDescription().toLowerCase().contains(searchText)
				)
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	public ItemDto addNewItem(Long userId, ItemDto itemDto) {
		return ItemMapper.toItemDto(itemRepository.save(userId, itemDto));
	}

	@Override
	public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
		return ItemMapper.toItemDto(itemRepository.update(userId, itemId, itemDto));
	}

	@Override
	public void deleteItem(long userId, long itemId) {
		itemRepository.remove(userId, itemId);
	}
}
