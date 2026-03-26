package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.dto.ItemDtoWithDates;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {

	/// Получить вещь по id
	ItemDtoWithComments getItem(long itemId);

	/// Получить все вещи
	List<ItemDtoWithDates> getAllItemsOfUser(long userId);

	/// Поиск вещей по подстроке
	List<ItemDto> search(long userId, String search);

	/// Добавить новую вещь
	@Transactional
	ItemDto addNewItem(Long userId, ItemDto itemDto);

	/// Обновить данные вещи по id
	@Transactional(propagation = Propagation.REQUIRED)
	ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

	/// Удалить вещь по id
	@Transactional
	void deleteItem(long userId, long itemId);

	/// Добавить комментарий
	@Transactional
	CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
