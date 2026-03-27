package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.DuplicatedDataException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserDto getUser(long userId) {
		return UserMapper.toUserDto(getUserWithCheckPresent(userId));
	}

	@Override
	public List<UserDto> getAllUsers() {
		return userRepository.findAll().stream()
				.map(UserMapper::toUserDto)
				.toList();
	}

	@Override
	public UserDto saveUser(UserDto userDto) {
		checkEmail(userDto.getEmail());
		return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
	}

	@Override
	public UserDto updateUser(long userId, UserDto userDto) {
		User user = getUserWithCheckPresent(userId);
		if (userDto.getName() != null) {
			user.setName(userDto.getName());
		}
		if (userDto.getEmail() != null) {
			checkEmail(userDto.getEmail());
			user.setEmail(userDto.getEmail());
		}
		return UserMapper.toUserDto(userRepository.save(user));
	}

	@Override
	public void deleteUser(long userId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
		}
		if (userRepository.deleteUserById(userId) < 1) {
			throw new RuntimeException("Удаление не удалось");
		}
	}

	private void checkEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new DuplicatedDataException("Пользователь с такой почтой " + email + " уже есть.");
		}
	}

	private User getUserWithCheckPresent(long userId) {
		return userRepository.findById(userId).orElseThrow(() ->
				new NotFoundException("Пользователь с id=" + userId + " не найден.")
		);
	}
}
