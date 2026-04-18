package ru.practicum.shareit.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestBigDto;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemRequestService {

	/// Добавить новый запрос вещи
	@Transactional
	ItemRequestDto addNewRequest(long userId, ItemRequestDto requestDto);

	/// Получить список своих запросов вместе с данными об ответах на них
	List<ItemRequestBigDto> getRequestListByUser(long userId);

	/// Получить список запросов, созданных другими пользователями
	List<ItemRequestDto> getAllRequests(long userId);

	/// Получить данные об одном конкретном запросе вместе с данными об ответах на него
	ItemRequestBigDto getRequestById(long requestId);
}
