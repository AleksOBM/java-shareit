package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemBigDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

	final ItemService itemService;

	@GetMapping("/{itemId}")
	public ItemBigDto getItem(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable long itemId
	) {
		return itemService.getItem(userId, itemId);
	}

	@GetMapping
	public List<ItemBigDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
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
	public ItemDto addNewItem(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody ItemDto item
	) {
		return itemService.addNewItem(userId, item);
	}

	@PostMapping("/{itemId}/comment")
	public CommentDto addComment(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable long itemId,
			@RequestBody CommentDto commentDto
	) {
		return itemService.addComment(userId, itemId, commentDto);
	}

	@PatchMapping("/{itemId}")
	public ItemDto updateItem(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable long itemId,
			@RequestBody ItemDto itemDto
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
