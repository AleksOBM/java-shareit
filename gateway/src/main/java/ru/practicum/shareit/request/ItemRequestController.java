package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GItemRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

	final ItemRequestClient itemRequestClient;

	@PostMapping
	public ResponseEntity<Object> addNewRequest(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid GItemRequestDto requestDto
	) {
		log.info("Add new request for user with id={}", userId);
		return itemRequestClient.addNewRequest(userId, requestDto);
	}

	@GetMapping
	public ResponseEntity<Object> getRequestListByRequestor(
			@RequestHeader("X-Sharer-User-Id") long requestorId,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		log.info("Get all requests by requestor with id={}", requestorId);
		return itemRequestClient.getRequestListByRequestor(requestorId, from, size);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getRequestListForUser(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		log.info("Get all requests for user with id={}", userId);
		return itemRequestClient.getRequestsListForUser(userId, from, size);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getRequestById(@PathVariable long requestId) {
		log.info("Get request with id={}", requestId);
		return itemRequestClient.getRequestById(requestId);
	}
}
