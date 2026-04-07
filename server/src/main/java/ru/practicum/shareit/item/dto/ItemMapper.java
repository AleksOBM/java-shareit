package ru.practicum.shareit.item.dto;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {

	@NonNull
	public static ItemDto toDto(@NonNull Item item) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.isAvailable())
				.requestId(item.getRequest() != null ? item.getRequest().getId() : null)
				.build();
	}

	public static ItemBigDto toBigDto(
			@NonNull Item item,
			LocalDateTime lastBookingDate,
			LocalDateTime nextBookingDate,
			List<CommentDto> comments
	) {
		return ItemBigDto.builder()
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

	public static ItemLowDto toLowDto(Item item) {
		return new ItemLowDto(
				item.getId(),
				item.getName(),
				item.getOwner().getId()
		);
	}

	@NonNull
	public static Item toEntity(@NonNull ItemDto itemDto, User owner, ItemRequest request) {
		return new Item()
				.setId(itemDto.getId())
				.setName(itemDto.getName())
				.setDescription(itemDto.getDescription())
				.setAvailable(itemDto.getAvailable())
				.setOwner(owner)
				.setRequest(request);
	}
}
