package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.stub.MethodNotImplemented;

public class UserMapper {

	public static UserDto toUserDto(User user) {
		return new UserDto(user.getId(), user.getName(),  user.getEmail());
	}

	@MethodNotImplemented
	public static User toUser(UserDto userDto) {
		return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
	}
}
