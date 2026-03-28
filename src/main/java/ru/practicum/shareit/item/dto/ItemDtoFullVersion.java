package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NotNull
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoFullVersion {

	Long id;
	String name;
	String description;
	Boolean available;

	@JsonProperty("request")
	Long requestId;

	@JsonProperty("lastBooking")
	LocalDateTime lastBookingDate;

	@JsonProperty("nextBooking")
	LocalDateTime nextBookingDate;

	List<CommentDto> comments;
}
