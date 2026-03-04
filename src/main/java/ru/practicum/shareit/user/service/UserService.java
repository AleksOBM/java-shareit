package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

	/// Получить DTO всех пользователей
	Collection<UserDto> getAllUsers();

	/// Создать и сохранить пользователя в базу
	UserDto saveUser(UserDto userDto);

	/// Удалить пользователя по id
	void deleteUser(long userId);

	/// Обновить данные пользователя по id
	UserDto updateUser(long userId, UserDto userDto);

	/// Получчить пользователя по id
	UserDto getUser(long userId);
}
