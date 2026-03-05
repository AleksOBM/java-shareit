package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Marker;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	@GetMapping("/{itemId}")
	public ItemDto getItem(@PathVariable long itemId) {
		return itemService.getItem(itemId);
	}

	@GetMapping
	public List<ItemDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
		return itemService.getAllItemsOfUser(userId);
	}

	@GetMapping("/search")
	public List<ItemDto> search(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam String text
	) {
		return itemService.search(userId, text);
	}

	@PostMapping
	@Validated(Marker.OnCreate.class)
	public ItemDto addNewItem(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@Valid @RequestBody ItemDto item
	) {
		return itemService.addNewItem(userId, item);
	}

	@PatchMapping("/{itemId}")
	public ItemDto updateItem(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable long itemId,
			@Valid @RequestBody ItemDto itemDto
	) {
		return itemService.updateItem(userId, itemId, itemDto);
	}

	@DeleteMapping("/{itemId}")
	public void deleteItem(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable(name = "itemId") long itemId
	) {
		itemService.deleteItem(userId, itemId);
	}
}
