package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

	/// Получить вещь по id
	Optional<Item> getOne(long itemId);

	/// Получить все вещи
	List<Item> findAll();

	/// Сохранить новую вещь
	Item save(Item item);

	/// Обновить вещь по id
	Item update(long itemId, Item newItem);

	/// Проверить что вещи с таким id нет в базе
	boolean checkItemIsNotPresent(long itemId);

	/// Удалить вещь по id
	void remove(long userId, long itemId);
}
