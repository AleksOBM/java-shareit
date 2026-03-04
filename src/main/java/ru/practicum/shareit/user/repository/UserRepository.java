package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserRepository {

	/// Получить одного пользователя если есть
	User findOne(long userId);

	/// Получить всех пользователей
	Collection<User> findAll();

	/// Сохранить пользовател в базу
	User save(UserDto userDto);

	/// Обновить данные пользователя по id
	User update(long userId, UserDto userDto);

	/// Удалить пользователя по id
	void remove(long userId);
}
