package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.util.Marker;

public record GCommentDto(

	@NotNull(groups = Marker.OnCreate.class)
	Long itemId,

	@NotNull
	String text
) {}