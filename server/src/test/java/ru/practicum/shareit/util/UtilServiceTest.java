package ru.practicum.shareit.util;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private ItemRequestRepository itemRequestRepository;

	@InjectMocks
	private UtilService utilService;

	final TestUtils testUtils = new TestUtils();

	@Test
	void getUser() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> utilService.getUser(anyLong()));
	}

	@Test
	void getItem() {
		when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> utilService.getItem(anyLong()));
	}

	@Test
	void getItemRequest() {
		when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> utilService.getItemRequest(anyLong()));
	}
}