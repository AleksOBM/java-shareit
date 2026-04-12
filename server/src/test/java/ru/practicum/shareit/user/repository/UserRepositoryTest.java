package ru.practicum.shareit.user.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest(properties = {
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.url=jdbc:h2:mem:shareit"
})
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;

	private User addNewUser() {
		return userRepository.save(new User()
				.setName("username")
				.setEmail("email@mail.ru")
		);
	}

	@Test
	void existsByEmail() {
		addNewUser();
		assertTrue(userRepository.existsByEmail("email@mail.ru"));
		assertFalse(userRepository.existsByEmail("username@mail.ru"));
	}

	@Test
	void deleteUserById() {
		User user = addNewUser();
		int count = userRepository.deleteUserById(user.getId());
		assertEquals(1, count);
	}
}