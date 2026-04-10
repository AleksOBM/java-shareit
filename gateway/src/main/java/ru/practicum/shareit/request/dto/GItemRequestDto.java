package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;

public record GItemRequestDto(

		@NotNull
		String description
) {}
