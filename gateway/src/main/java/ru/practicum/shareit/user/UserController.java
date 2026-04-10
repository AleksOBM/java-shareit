package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.GUserDto;
import ru.practicum.shareit.util.Marker;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

	final UserClient userClient;

	@GetMapping("/{userId}")
	public ResponseEntity<Object> getUserById(@PathVariable long userId) {
		log.info("Get user by id={}", userId);
		return userClient.getUser(userId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllUsers(
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("Get all users");
		return userClient.getAllUsers(from, size);
	}

	@PostMapping
	@Validated(Marker.OnCreate.class)
	public ResponseEntity<Object> addNewUser(@RequestBody @Valid GUserDto userDto) {
		log.info("Add new user");
		return userClient.saveUser(userDto);
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<Object> updateUser(@PathVariable long userId,
	                                         @RequestBody @Valid GUserDto userDto
	) {
		log.info("Update user with id={}", userId);
		return userClient.updateUser(userId, userDto);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable long userId) {
		log.info("Delete user with id={}", userId);
		ResponseEntity<?> response = userClient.deleteUser(userId);
		if (response.getStatusCode().is2xxSuccessful()) {
			return new ResponseEntity<>("", HttpStatus.OK);
		}
		return response;
	}
}
