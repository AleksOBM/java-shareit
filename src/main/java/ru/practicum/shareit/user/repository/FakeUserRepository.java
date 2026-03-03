package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.util.exception.DuplicatedDataException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.IdentifyService;
import ru.practicum.shareit.util.stub.MethodNotImplemented;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class FakeUserRepository implements UserRepository {

	private final Map<Long, User> users = new HashMap<>();

	private final IdentifyService identifyService;

	@Override
	public User findOne(long userId) {
		return users.get(userId);
	}

	@Override
	public Collection<User> findAll() {
		return users.values();
	}

	@Override
	public User save(UserDto userDto) {
		for (User u : users.values()) {
			if (u.getEmail().equals(userDto.getEmail())) {
				throw new DuplicatedDataException(this.getClass());
			}
		}

		User user = new User(
				identifyService.getNextId(users),
				userDto.getName(),
				userDto.getEmail()
				);

		users.put(user.getId(), user);
		return user;
	}

	@Override
	public User update(long userId, UserDto userDto) {
		for (User u : users.values()) {
			if (u.getEmail().equals(userDto.getEmail()) && u.getId() != userId) {
				throw new DuplicatedDataException(this.getClass());
			}
		}
		User user = users.get(userId);
		if (userDto.getName() != null) {
			user.setName(userDto.getName());
		}
		if (userDto.getEmail() != null) {
			user.setEmail(userDto.getEmail());
		}
		return user;
	}

	@Override
	public void remove(long userId) {
		users.remove(userId);
	}
}
