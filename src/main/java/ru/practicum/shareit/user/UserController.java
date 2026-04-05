package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.validate.Marker;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Validated
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

	final UserService userService;

	@GetMapping("/{userId}")
	public UserDto getUser(@PathVariable long userId) {
		return userService.getUser(userId);
	}

	@GetMapping
	public List<UserDto> getAllUsers() {
		return userService.getAllUsers().stream().toList();
	}

	@PostMapping
	@Validated(Marker.OnCreate.class)
	public UserDto addNewUser(@Valid @RequestBody UserDto userDto) {
		return userService.saveUser(userDto);
	}

	@PatchMapping("/{userId}")
	public UserDto updateUser(
			@PathVariable long userId,
			@Valid @RequestBody UserDto userDto
	) {
		return userService.updateUser(userId, userDto);
	}

	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable long userId) {
		userService.deleteUser(userId);
	}
}
