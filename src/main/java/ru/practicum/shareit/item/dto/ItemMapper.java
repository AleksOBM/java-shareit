package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {
	public static ItemDto toItemDto(Item item) {
		return new ItemDto(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.isAvailable(),
				item.getRequest() != null ? item.getRequest().getId() : null
		);
	}

	public static ItemDtoWithDates toItemDtoWithDates(
			Item item,
			LocalDateTime lastBookingDate,
			LocalDateTime nextBookingDate
	) {
		return ItemDtoWithDates.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.isAvailable())
				.requestId(item.getRequest() != null ? item.getRequest().getId() : null)
				.lastBookingDate(lastBookingDate)
				.nextBookingDate(nextBookingDate)
				.build();
	}

	public static ItemDtoWithComments toItemDtoWithComments(
			Item item,
			LocalDateTime lastBookingDate,
			LocalDateTime nextBookingDate,
			List<CommentDto> comments
	) {
		return ItemDtoWithComments.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.isAvailable())
				.requestId(item.getRequest() == null ? null : item.getRequest().getId())
				.lastBookingDate(lastBookingDate)
				.nextBookingDate(nextBookingDate)
				.comments(comments)
				.build();
	}

	public static Item toItem(ItemDto itemDto, User user) {
		return new Item(
				itemDto.getId(),
				itemDto.getName(),
				itemDto.getDescription(),
				itemDto.getAvailable(),
				user,
				null
		);
	}
}
