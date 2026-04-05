package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

	public static ItemRequestDto toDto(ItemRequest itemRequest) {
		return ItemRequestDto.builder()
				.id(itemRequest.getId())
				.description(itemRequest.getDescription())
				.requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
				.createdDate(itemRequest.getCreatedDate())
				.build();
	}

	public static ItemRequestBigDto toBigDto(ItemRequest itemRequest, List<Item> items) {
		return ItemRequestBigDto.builder()
				.id(itemRequest.getId())
				.description(itemRequest.getDescription())
				.requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
				.itemDtos(items.stream().map(ItemMapper::toLowDto).toList())
				.createdDate(itemRequest.getCreatedDate())
				.build();
	}

	public static ItemRequest toEntity(ItemRequestDto requestDto, User requestor) {
		return new ItemRequest()
				.setDescription(requestDto.getDescription())
				.setRequestor(requestor)
				.setCreatedDate(requestDto.getCreatedDate() == null ?
						LocalDateTime.now() : requestDto.getCreatedDate());
	}
}
