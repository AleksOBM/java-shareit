package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.stub.MethodNotImplemented;

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

	@MethodNotImplemented
	public static Item toItem(ItemDto itemDto) {
		return null;
	}
}
