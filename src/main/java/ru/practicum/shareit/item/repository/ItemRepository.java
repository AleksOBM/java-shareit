package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

	/// Получить вещь по id
	Item getOne(long itemId);

	/// Получить все вещи
	List<Item> findAll();

	/// Сохранить новую вещь
	Item save(long userId, ItemDto itemDto);

	/// Удалить вещь по id
	void remove(long userId, long itemId);

	/// Обновить вещь по id
	Item update(long userId, long itemId, ItemDto itemDto);
}
