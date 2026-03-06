package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NotNull
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
	Long id;
	String name;
	String description;
	boolean available;
	User owner;
	ItemRequest request;
}
