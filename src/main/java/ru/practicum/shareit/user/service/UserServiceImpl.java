package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserDto getUser(long userId) {
		return UserMapper.toUserDto(userRepository.findOne(userId));
	}

	@Override
	public List<UserDto> getAllUsers() {
		return userRepository.findAll().stream()
				.map(UserMapper::toUserDto)
				.toList();
	}

	@Override
	public UserDto saveUser(UserDto userDto) {
		return UserMapper.toUserDto(userRepository.save(userDto));
	}

	@Override
	public void deleteUser(long userId) {
		userRepository.remove(userId);
	}

	@Override
	public UserDto updateUser(long userId, UserDto userDto) {
		return UserMapper.toUserDto(userRepository.update(userId, userDto));
	}
}
