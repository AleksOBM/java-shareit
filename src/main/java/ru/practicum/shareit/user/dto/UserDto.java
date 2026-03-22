package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.util.Marker;

@Data
@NotNull
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

	Long id;

	@NotBlank(groups = Marker.OnCreate.class)
	@Size(min = 1, max = 255)
	String name;

	@NotBlank(groups = Marker.OnCreate.class)
	@Size(min = 1, max = 512)
	@Email
	String email;

	public UserDto(Long id, String name, String email) {
		this.id = id != null && id <= 0 ? null : id;
		this.name = name == null ? null : name.trim();
		this.email = email == null ? null : email.trim();
	}
}
