package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestBigDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

	final ItemRequestService requestService;

	@PostMapping
	public ItemRequestDto addNewRequest(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid ItemRequestDto requestDto
	) {
		return requestService.addNewRequest(userId, requestDto);
	}

	@GetMapping
	public List<ItemRequestBigDto> getRequestListByUser(@RequestHeader("X-Sharer-User-Id") long requestorId) {
		return requestService.getRequestListByUser(requestorId);
	}

	@GetMapping("/all")
	public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
		return requestService.getAllRequests(userId);
	}

	@GetMapping("/{requestId}")
	public ItemRequestBigDto getRequestById(@PathVariable long requestId) {
		return requestService.getRequestById(requestId);
	}
}
