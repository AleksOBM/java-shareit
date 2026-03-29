package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {

	/// Получить DTO всех пользователей
	List<UserDto> getAllUsers();

	/// Создать и сохранить пользователя в базу
	@Transactional
	UserDto saveUser(UserDto userDto);

	/// Удалить пользователя по id
	@Transactional
	void deleteUser(long userId);

	/// Обновить данные пользователя по id
	@Transactional(propagation = Propagation.REQUIRED)
	UserDto updateUser(long userId, UserDto userDto);

	/// Получчить пользователя по id
	UserDto getUser(long userId);
}
