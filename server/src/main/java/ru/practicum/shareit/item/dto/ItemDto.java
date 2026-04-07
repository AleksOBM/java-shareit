package ru.practicum.shareit.item.dto;


import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(makeFinal = true)
public class ItemDto {

	Long id;

	String name;

	String description;

	Boolean available;

	Long requestId;
}
