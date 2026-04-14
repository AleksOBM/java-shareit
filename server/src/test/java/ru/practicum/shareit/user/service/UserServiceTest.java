package ru.practicum.shareit.user.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestUtils;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.UtilService;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	UtilService utilService;

	@InjectMocks
	UserServiceImpl userService;

	final TestUtils testUtils = new TestUtils();

	@Nested
	class SaveUser {

		@Test
		void returnActualUserDto() {
			User user = testUtils.makeNewUser(1);
			UserDto userDto = UserDto.builder()
					.id(user.getId())
					.name(user.getName())
					.email(user.getEmail())
					.build();
			when(userRepository.save(user)).thenReturn(user);

			UserDto resultDto = userService.saveUser(userDto);
			assertThat(resultDto, testUtils.matchesUserDto(user));
		}
	}

	@Nested
	class GetUser {

		@Test
		void returnActualUserDto() {
			User user = testUtils.makeNewUser(1);
			when(utilService.getUser(user.getId())).thenReturn(user);

			UserDto resultDto = userService.getUser(user.getId());

			assertThat(resultDto, testUtils.matchesUserDto(user));
		}
	}

	@Nested
	class GetAllUsers {

		@Test
		public void returnListOfActualUserDtos() {
			List<User> users = List.of(
					testUtils.makeNewUser(1),
					testUtils.makeNewUser(2),
					testUtils.makeNewUser(3)
			);
			when(userRepository.findAll()).thenReturn(users);

			List<UserDto> userDtos = userService.getAllUsers();

			assertThat(userDtos, contains(
					testUtils.matchesUserDto(users.get(0)),
					testUtils.matchesUserDto(users.get(1)),
					testUtils.matchesUserDto(users.get(2))
			));
		}
	}

	@Nested
	class UpdateUser {

		@Test
		void whenUserWasRenamed_returnActualUserDto() {
			User oldUser = testUtils.makeNewUser(1);
			UserDto requestDto = UserDto.builder()
					.name("new name")
					.build();
			User newUser = new User()
					.setId(oldUser.getId())
					.setName(requestDto.getName())
					.setEmail(oldUser.getEmail());

			when(utilService.getUser(oldUser.getId())).thenReturn(testUtils.getCopyOfUser(oldUser));
			when(userRepository.save(newUser)).thenReturn(newUser);

			UserDto resultDto = userService.updateUser(oldUser.getId(), requestDto);

			assertNotEquals(resultDto.getName(), oldUser.getName());
			assertThat(resultDto, testUtils.matchesUserDto(newUser));
			verify(userRepository).save(newUser);
		}

		@Test
		void whenUserEmailWasChanged_returnActualUserDto() {
			User oldUser = testUtils.makeNewUser(1);
			UserDto requestDto = UserDto.builder()
					.email(testUtils.generateRandomText(10, "@mail.com"))
					.build();
			User newUser = new User()
					.setId(oldUser.getId())
					.setName(oldUser.getName())
					.setEmail(requestDto.getEmail());

			when(utilService.getUser(oldUser.getId())).thenReturn(testUtils.getCopyOfUser(oldUser));
			when(userRepository.save(newUser)).thenReturn(newUser);

			UserDto resultDto = userService.updateUser(oldUser.getId(), requestDto);

			assertNotEquals(resultDto.getEmail(), oldUser.getEmail());
			assertThat(resultDto, testUtils.matchesUserDto(newUser));
			verify(userRepository).save(newUser);
		}

		@Test
		@SneakyThrows
		void whenUserEmailIsConflicted_returnDuplicatedDataException() {
			User oldUser = testUtils.makeNewUser(1);

			Method checkEmail = userService.getClass().getDeclaredMethod("checkEmail", String.class);
			checkEmail.setAccessible(true);
			when(userRepository.existsByEmail(oldUser.getEmail())).thenReturn(true);

			assertThrows(InvocationTargetException.class,
					() -> checkEmail.invoke(userService, oldUser.getEmail()));
		}
	}

	@Nested
	class DeleteUser {

		@Test
		void whenUserWasDeleted_returnVoid() {
			User user = testUtils.makeNewUser(1);
			when(userRepository.existsById(user.getId())).thenReturn(true);
			when(userRepository.deleteUserById(user.getId())).thenReturn(1);

			userService.deleteUser(user.getId());
			verify(userRepository).existsById(user.getId());
			verify(userRepository).deleteUserById(user.getId());
		}

		@Test
		void whenUserWasNotDeleted_returnRuntimeException() {
			User user = testUtils.makeNewUser(1);
			when(userRepository.existsById(user.getId())).thenReturn(true);
			when(userRepository.deleteUserById(user.getId())).thenReturn(0);

			assertThrows(RuntimeException.class, () -> userService.deleteUser(user.getId()));

			verify(userRepository).existsById(user.getId());
			verify(userRepository).deleteUserById(user.getId());
		}

		@Test
		void whenUserIsNotFound_returnNotFoundException() {
			User user = testUtils.makeNewUser(1);
			when(userRepository.existsById(user.getId())).thenReturn(false);

			assertThrows(NotFoundException.class,
					() -> userService.deleteUser(user.getId()));

			verify(userRepository).existsById(user.getId());
			verify(userRepository, never()).deleteUserById(user.getId());
		}
	}
}
