package ru.practicum.shareit.util.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BaseEntityJpaTest {
	@Autowired
	private TestEntityManager em;

	@Test
	void equals_shouldWorkWithHibernateProxy() {
		User entity = new User().setName("name").setEmail("email");
		em.persist(entity);
		em.flush();
		em.clear();

		User proxy = em.getEntityManager()
				.getReference(User.class, entity.getId());

		User found = em.find(User.class, entity.getId());

		assertEquals(proxy, found);
		assertEquals(found, proxy);
	}
}