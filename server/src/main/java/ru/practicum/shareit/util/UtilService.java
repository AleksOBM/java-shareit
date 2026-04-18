package ru.practicum.shareit.util;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.ForbiddenException;
import ru.practicum.shareit.util.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class UtilService {
	final UserRepository userRepository;
	final ItemRepository itemRepository;
	final ItemRequestRepository itemRequestRepository;

	public void checkUser(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new ForbiddenException("Пользователь с id=" + userId + " не найден.");
		}
	}

	@NonNull
	public User getUser(long userId) {
		return userRepository.findById(userId).orElseThrow(() ->
				new NotFoundException("Пользователь с id=" + userId + " не найден.")
		);
	}

	@NonNull
	public Item getItem(long itemId) {
		return itemRepository.findById(itemId).orElseThrow(() ->
				new NotFoundException("Вещь с id=" + itemId + " не найдена.")
		);
	}

	@NonNull
	public ItemRequest getItemRequest(long requestId) {
		return itemRequestRepository.findById(requestId).orElseThrow(
				()  -> new NotFoundException("Запрос с id=" + requestId + " на добавление вещи не найден.")
		);
	}
}
