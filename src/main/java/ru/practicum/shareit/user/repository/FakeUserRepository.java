package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.IdentifyService;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class FakeUserRepository implements UserRepository {

	private final Map<Long, User> users = new HashMap<>();

	private final IdentifyService identifyService;

	@Override
	public Optional<User> findOne(long userId) {
		User user = users.get(userId);
		return user == null ? Optional.empty() : Optional.of(user);
	}

	@Override
	public Collection<User> findAll() {
		return users.values();
	}

	@Override
	public User save(User user) {
		user.setId(identifyService.getNextId(users));
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public User update(User newUser) {
		users.remove(newUser.getId());
		users.put(newUser.getId(), newUser);
		return newUser;
	}

	@Override
	public boolean checkUserIsNotPresent(long userId) {
		return !users.containsKey(userId);
	}

	@Override
	public boolean checkEmailIsDuplicated(String email) {
		for (User u : users.values()) {
			if (u.getEmail().equals(email)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void remove(long userId) {
		users.remove(userId);
	}
}
