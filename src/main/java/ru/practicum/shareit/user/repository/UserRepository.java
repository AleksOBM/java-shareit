package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

	/// Получить одного пользователя если есть
	Optional<User> findOne(long userId);

	/// Получить всех пользователей
	Collection<User> findAll();

	/// Сохранить пользовател в базу
	User save(User user);

	/// Обновить данные пользователя по id
	User update(User newUser);

	/// Проверить что пользователя с таким id нет в базе
	boolean checkUserIsNotPresent(long userId);

	/// Проверить что этот пользователь с такой почтой уже есть в базе
	boolean checkEmailIsDuplicated(String email);

	/// Удалить пользователя по id
	void remove(long userId);
}
