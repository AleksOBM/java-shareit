package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.util.validate.Marker;

@Data
@NotNull
@Builder
@FieldDefaults(makeFinal = true)
public class UserDto {

	Long id;

	@NotBlank(groups = Marker.OnCreate.class)
	@Size(min = 1, max = 255)
	String name;

	@NotBlank(groups = Marker.OnCreate.class)
	@Size(min = 1, max = 512)
	@Email
	String email;
}
