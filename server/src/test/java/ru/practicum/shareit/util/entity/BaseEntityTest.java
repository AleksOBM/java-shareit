package ru.practicum.shareit.util.entity;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class BaseEntityTest {

	@Test
	void equals_shouldReturnTrue_whenSameInstance() {
		User entity = new User().setName("name").setEmail("email");

		assertEquals(entity, entity);
	}

	@Test
	void equals_shouldReturnFalse_whenNull() {
		User entity = new User().setName("name").setEmail("email");

		assertNotEquals(null, entity);
	}

	@Test
	void equals_shouldReturnFalse_whenDifferentClasses() {
		User entity = new User().setName("name").setEmail("email");
		Object other = new Object();

		assertNotEquals(entity, other);
	}

	@Test
	void equals_shouldReturnFalse_whenIdsAreNull() {
		User e1 = new User().setName("name1").setEmail("email1");
		User e2 = new User().setName("name2").setEmail("email2");

		assertNotEquals(e1, e2);
	}

	@Test
	void equals_shouldReturnTrue_whenSameId() throws Exception {
		User e1 = new User().setID(1L).setName("name1").setEmail("email1");
		User e2 = new User().setID(1L).setName("name2").setEmail("email2");

		assertEquals(e1, e2);
	}

	@Test
	void equals_shouldReturnFalse_whenDifferentIds() throws Exception {
		User e1 = new User().setID(1L).setName("name1").setEmail("email1");
		User e2 = new User().setID(2L).setName("name2").setEmail("email2");

		assertNotEquals(e1, e2);
	}

	@Test
	void hashCode_shouldBeSameForSameClass() {
		User e1 = new User();
		User e2 = new User();

		assertEquals(e1.hashCode(), e2.hashCode());
	}
}