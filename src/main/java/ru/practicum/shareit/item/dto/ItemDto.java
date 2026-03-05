package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.util.Marker;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NotNull
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

	Long id;

	@NotBlank(groups = Marker.OnCreate.class)
	@Size(min = 1, max = 100)
	String name;

	@NotBlank(groups = Marker.OnCreate.class)
	@Size(min = 1, max = 250)
	String description;

	@NotNull(groups = Marker.OnCreate.class)
	Boolean available;

	@JsonProperty("request")
	Long requestId;

	public ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
		this.id = id != null && id <= 0 ? null : id;
		this.name = name == null ? null : name.trim();
		this.description = description;
		this.available = available;
		this.requestId = requestId != null && requestId <= 0 ? null : requestId;
	}
}
