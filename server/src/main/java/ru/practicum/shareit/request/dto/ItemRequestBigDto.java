package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemLowDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(makeFinal = true)
public class ItemRequestBigDto {

		Long id;

		String description;

		UserDto requestor;

		@JsonProperty("items")
		List<ItemLowDto> itemDtos;

		@JsonProperty("created")
		LocalDateTime createdDate;
}
