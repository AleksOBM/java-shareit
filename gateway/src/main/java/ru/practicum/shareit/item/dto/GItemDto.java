package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.practicum.shareit.util.Marker;

public record GItemDto(

		@NotNull(groups = Marker.OnCreate.class)
		@Size(min = 1, max = 255)
		String name,

		@NotNull(groups = Marker.OnCreate.class)
		@Size(max = 500)
		String description,

		@NotNull(groups = Marker.OnCreate.class)
		Boolean available,

		Long requestId
) {}
