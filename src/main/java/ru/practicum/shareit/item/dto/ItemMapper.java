package ru.practicum.shareit.item.dto;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {

	@NonNull
	public static ItemDto toItemDto(@NonNull Item item) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.isAvailable())
				.requestId(item.getRequest() != null ? item.getRequest().getId() : null)
				.build();
	}

	public static ItemDtoFullVersion toFullItemDto(
			@NonNull Item item,
			LocalDateTime lastBookingDate,
			LocalDateTime nextBookingDate,
			List<CommentDto> comments
	) {
		return ItemDtoFullVersion.builder()
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

	@NonNull
	public static Item toItem(@NonNull ItemDto itemDto, User owner) {
		return new Item()
				.setId(itemDto.getId())
				.setName(itemDto.getName())
				.setDescription(itemDto.getDescription())
				.setAvailable(itemDto.getAvailable())
				.setOwner(owner)
				.setRequest(null);
	}
}
