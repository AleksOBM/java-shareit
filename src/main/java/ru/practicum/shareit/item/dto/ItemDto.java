package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.util.validate.Marker;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NotNull
@Builder
@FieldDefaults(makeFinal = true)
public class ItemDto {

	Long id;

	@NotBlank(groups = Marker.OnCreate.class)
	@Size(min = 1, max = 255)
	String name;

	@NotBlank(groups = Marker.OnCreate.class)
	@Size(min = 1, max = 255)
	String description;

	@NotNull(groups = Marker.OnCreate.class)
	Boolean available;

	Long requestId;
}
