package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.error.ErrorHandler;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	final ObjectMapper mapper = new ObjectMapper();

	@InjectMocks
	UserController userController;

	@Mock
	UserService userService;

	MockMvc mvc;

	UserDto userDto;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(userController)
				.setControllerAdvice(new ErrorHandler())
				.build();

		userDto = UserDto.builder()
				.id(1L)
				.name("John")
				.email("john.doe@mail.com")
				.build();
	}

	@Nested
	class GetUser {

		@Test
		@SneakyThrows
		void returnResponseWithStatusOkAndBodyOfActualUserDto() {
			long userId = 1L;
			when(userService.getUser(userId)).thenReturn(userDto);

			mvc.perform(get("/users/{userId}", userId)
							.content(mapper.writeValueAsString(userDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
					.andExpect(jsonPath("$.name", is(userDto.getName())))
					.andExpect(jsonPath("$.email", is(userDto.getEmail())));

			verify(userService).getUser(userId);
		}

		@Test
		@SneakyThrows
		void returnResponseWithStatus404AndBodyOfNotFoundException() {
			long userId = anyLong();
			when(userService.getUser(userId)).thenThrow(NotFoundException.class);

			mvc.perform(get("/users/{userId}", userId)
							.content(mapper.writeValueAsString(userDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andDo(MockMvcResultHandlers.print())
					.andExpect(status().is(404));

			verify(userService).getUser(userId);
		}
	}

	@Nested
	class GetAllUsers {

		@Test
		@SneakyThrows
		void returnResponseWithStatusOkAndBodyOfListWithActualUserDto() {
			when(userService.getAllUsers()).thenReturn(List.of(userDto));

			mvc.perform(get("/users")
					.content(mapper.writeValueAsString(userDto))
					.characterEncoding(StandardCharsets.UTF_8)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.[0].id",  is(userDto.getId()), Long.class))
					.andExpect(jsonPath("$.[0].name",  is(userDto.getName()), String.class))
					.andExpect(jsonPath("$.[0].email",  is(userDto.getEmail()), String.class));

			verify(userService).getAllUsers();
		}

		@Test
		@SneakyThrows
		void returnResponseWithStatusOkAndBodyOfEmptyList() {
			when(userService.getAllUsers()).thenReturn(Collections.emptyList());

			mvc.perform(get("/users")
							.content(mapper.writeValueAsString(userDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", is(Collections.emptyList())));

			verify(userService).getAllUsers();
		}
	}

	@Nested
	class AddNewUser {

		@Test
		@SneakyThrows
		void returnResponseWithStatusOkAndBodyOfActualUserDto() {
			when(userService.saveUser(any()))
					.thenReturn(userDto);

			String result = mvc.perform(post("/users")
							.content(mapper.writeValueAsString(userDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andReturn()
					.getResponse()
					.getContentAsString();

			verify(userService).saveUser(any());
			assertEquals(result, mapper.writeValueAsString(userDto));
		}
	}

	@Nested
	class UpdateUser {

		@Test
		@SneakyThrows
		void test() {
			UserDto newUserDto = UserDto.builder().name("NewUser").build();
			UserDto resultUserDto = UserDto.builder()
					.id(userDto.getId())
					.name(newUserDto.getName())
					.email(userDto.getEmail())
					.build();
			when(userService.updateUser(userDto.getId(), newUserDto)).thenReturn(resultUserDto);

			mvc.perform(patch("/users/{userId}", 1L)
							.content(mapper.writeValueAsString(newUserDto))
							.characterEncoding(StandardCharsets.UTF_8)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id", is(resultUserDto.getId()), Long.class))
					.andExpect(jsonPath("$.name", is(resultUserDto.getName()), String.class))
					.andExpect(jsonPath("$.email", is(resultUserDto.getEmail()), String.class));

			verify(userService).updateUser(userDto.getId(), newUserDto);
		}
	}

	@Nested
	class DeleteUser {

		@Test
		@SneakyThrows
		void test() {
			mvc.perform(delete("/users/{userId}", 1L))
					.andExpect(status().isOk());

			verify(userService).deleteUser(1L);
		}
	}
}