package ru.practicum.shareit.util;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.error.ErrorHandler;

@ExtendWith(MockitoExtension.class)
class UtilServiceTest {

	@InjectMocks
	private UtilService utilService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private ItemRequestRepository itemRequestRepository;

	private UserDto userDto;

	@Autowired
	ErrorHandler errorHandler;

	@BeforeEach
	void setUp() {
		userDto = UserDto.builder()
				.id(1L)
				.name("John")
				.email("john.doe@mail.com")
				.build();
	}

	@Test
	void checkUser() {
	}

	@Test
	void getUser() {
	}

	@Test
	void getItem() {
	}

	@Test
	void getItemRequest() {
	}
}