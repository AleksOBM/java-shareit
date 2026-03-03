package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

	/// Получить вещь по id
	ItemDto grtItem(long itemId);

	/// Получить все вещи
	List<ItemDto> getAllItemsOfUser(long userId);

	/// Поиск вещей по подстроке
	List<ItemDto> search(long userId, String search);

	/// Добавить новую вещь
	ItemDto addNewItem(Long userId, ItemDto itemDto);

	/// Удалить вещь по id
	void deleteItem(long userId, long itemId);

	/// Обновить данные вещи по id
	ItemDto updateItem(long userId, long itemId, ItemDto itemDto);
}
