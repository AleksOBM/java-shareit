package ru.practicum.shareit.request.dto;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

	public static ItemRequestDto toDto(@NonNull ItemRequest itemRequest) {
		return ItemRequestDto.builder()
				.id(itemRequest.getId())
				.description(itemRequest.getDescription())
				.requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
				.createdDate(itemRequest.getCreatedDate())
				.build();
	}

	public static ItemRequestBigDto toBigDto(@NonNull ItemRequest itemRequest, @NonNull List<Item> items) {
		return ItemRequestBigDto.builder()
				.id(itemRequest.getId())
				.description(itemRequest.getDescription())
				.requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
				.itemDtos(items.stream().map(ItemMapper::toLowDto).toList())
				.createdDate(itemRequest.getCreatedDate())
				.build();
	}

	public static ItemRequest toEntity(@NonNull ItemRequestDto requestDto, User requestor) {
		return new ItemRequest()
				.setId(requestDto.getId())
				.setDescription(requestDto.getDescription())
				.setRequestor(requestor)
				.setCreatedDate(requestDto.getCreatedDate() == null ?
						LocalDateTime.now() : requestDto.getCreatedDate());
	}
}
