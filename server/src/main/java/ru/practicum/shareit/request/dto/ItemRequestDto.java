package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
@FieldDefaults(makeFinal = true)
public class ItemRequestDto {

	Long id;

	String description;

	UserDto requestor;

	@JsonProperty("created")
	LocalDateTime createdDate;
}
