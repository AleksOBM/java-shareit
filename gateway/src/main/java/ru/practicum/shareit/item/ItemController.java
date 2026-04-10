package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.GCommentDto;
import ru.practicum.shareit.item.dto.GItemDto;
import ru.practicum.shareit.util.Marker;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

	final ItemClient itemClient;

	@GetMapping("/{itemId}")
	public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
	                                          @PathVariable long itemId
	) {
		log.info("Get item by id={} for user with id={}", itemId, userId);
		return itemClient.getItemById(userId, itemId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllItemsOfUser(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		log.info("Get all items for user with id={}", userId);
		return itemClient.getAllItemsOfUser(userId, from, size);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> search(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam String text,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		return itemClient.search(userId, text, from, size);
	}

	@PostMapping
	@Validated(Marker.OnCreate.class)
	public ResponseEntity<Object> addNewItem(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@Valid @RequestBody GItemDto item
	) {
		log.info("Add new item for user with id={}", userId);
		return itemClient.addNewItem(userId, item);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<Object> addComment(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable long itemId,
			@RequestBody GCommentDto commentDto
	) {
		log.info("Add new comment for item with id={} for user with id={}", itemId, userId);
		return itemClient.addComment(userId, itemId, commentDto);
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<Object> updateItem(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable long itemId,
			@RequestBody GItemDto itemDto
	) {
		log.info("Update item with id={} for user with id={}", itemId, userId);
		return itemClient.updateItem(userId, itemId, itemDto);
	}

	@DeleteMapping("/{itemId}")
	public ResponseEntity<?> deleteItem(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable(name = "itemId") long itemId
	) {
		log.info("Delete item with id={} for user with id={}", itemId, userId);
		ResponseEntity<?> response = itemClient.deleteItem(userId, itemId);
		if (response.getStatusCode().is2xxSuccessful()) {
			return new ResponseEntity<>("", HttpStatus.OK);
		}
		return response;
	}
}
