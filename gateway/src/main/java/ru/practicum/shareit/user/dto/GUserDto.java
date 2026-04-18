package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.practicum.shareit.util.Marker;

public record GUserDto(

		@NotNull(groups = Marker.OnCreate.class)
		@Size(min = 1, max = 255)
		String name,

		@Email @NotNull(groups = Marker.OnCreate.class)
		@Size(min = 1, max = 255)
		String email
) {}
