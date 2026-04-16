package ru.practicum.shareit.util.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
public class HibernateEqualsAndHashCodeTest {

	@PersistenceContext
	private EntityManager em;

	final TestUtils testUtils = new TestUtils();

	@Test
	void shouldHandleHibernateProxyCorrectly_byUser() {
		User user = new User()
				.setName("John")
				.setEmail("john@test.com");

		em.persist(user);
		em.flush();
		em.clear();

		User proxy = em.getReference(User.class, user.getId());
		Class<?> clazz = HibernateEqualsAndHashCode.persistentClass(proxy);

		assertEquals(User.class, clazz);
	}

	@Test
	void shouldBeEqualForProxyAndEntityWithSameId_byUser() {
		User user = new User()
				.setName("John")
				.setEmail("john@test.com");

		em.persist(user);
		em.flush();
		em.clear();

		User proxy = em.getReference(User.class, user.getId());
		User entity = em.find(User.class, user.getId());

		assertEquals(proxy, entity);
		assertEquals(entity, proxy);
		assertEquals(entity.hashCode(), proxy.hashCode());
	}

	@Test
	void shouldHandleHibernateProxyCorrectly_byRequest() {
		ItemRequest request = new ItemRequest()
				.setDescription("John")
				.setCreatedDate(LocalDateTime.now())
				.setRequestor(testUtils.makeNewUser(1));

		em.persist(request);
		em.flush();
		em.clear();

		ItemRequest proxy = em.getReference(ItemRequest.class, request.getId());
		Class<?> clazz = HibernateEqualsAndHashCode.persistentClass(proxy);

		assertEquals(ItemRequest.class, clazz);
	}

	@Test
	void shouldBeEqualForProxyAndEntityWithSameId_byRequest() {
		ItemRequest request = new ItemRequest()
				.setDescription("John")
				.setCreatedDate(LocalDateTime.now())
				.setRequestor(testUtils.makeNewUser(1));

		em.persist(request);
		em.flush();
		em.clear();

		ItemRequest proxy = em.getReference(ItemRequest.class, request.getId());
		ItemRequest entity = em.find(ItemRequest.class, request.getId());

		assertEquals(proxy, entity);
		assertEquals(entity, proxy);
		assertEquals(entity.hashCode(), proxy.hashCode());
	}

	@Test
	void shouldHandleHibernateProxyCorrectly_byComment() {
		Comment comment = new Comment()
				.setText("John")
				.setCreatedDate(LocalDateTime.now())
				.setAuthor(testUtils.makeNewUser(1))
				.setItem(testUtils.makeNewFastItem(10));

		em.persist(comment);
		em.flush();
		em.clear();

		Comment proxy = em.getReference(Comment.class, comment.getId());
		Class<?> clazz = HibernateEqualsAndHashCode.persistentClass(proxy);

		assertEquals(Comment.class, clazz);
	}

	@Test
	void shouldBeEqualForProxyAndEntityWithSameId_byComment() {
		Comment comment = new Comment()
				.setText("John")
				.setCreatedDate(LocalDateTime.now())
				.setAuthor(testUtils.makeNewUser(1))
				.setItem(testUtils.makeNewFastItem(10));

		em.persist(comment);
		em.flush();
		em.clear();

		Comment proxy = em.getReference(Comment.class, comment.getId());
		Comment entity = em.find(Comment.class, comment.getId());

		assertEquals(proxy, entity);
		assertEquals(entity, proxy);
		assertEquals(entity.hashCode(), proxy.hashCode());
	}

	@Test
	void shouldHandleHibernateProxyCorrectly_byBooking() {
		Booking booking = new Booking()
				.setBooker(testUtils.makeNewUser(1))
				.setItem(testUtils.makeNewFastItem(10))
				.setStart(LocalDateTime.now())
				.setEnd(LocalDateTime.now().plusDays(1))
				.setStatus(BookingStatus.WAITING);

		em.persist(booking);
		em.flush();
		em.clear();

		Booking proxy = em.getReference(Booking.class, booking.getId());
		Class<?> clazz = HibernateEqualsAndHashCode.persistentClass(proxy);

		assertEquals(Booking.class, clazz);
	}

	@Test
	void shouldBeEqualForProxyAndEntityWithSameId_byBooking() {
		Booking booking = new Booking()
				.setBooker(testUtils.makeNewUser(1))
				.setItem(testUtils.makeNewFastItem(10))
				.setStart(LocalDateTime.now())
				.setEnd(LocalDateTime.now().plusDays(1))
				.setStatus(BookingStatus.WAITING);

		em.persist(booking);
		em.flush();
		em.clear();

		Booking proxy = em.getReference(Booking.class, booking.getId());
		Booking entity = em.find(Booking.class, booking.getId());

		assertEquals(proxy, entity);
		assertEquals(entity, proxy);
		assertEquals(entity.hashCode(), proxy.hashCode());
	}

	@Test
	void shouldHandleHibernateProxyCorrectly_byItem() {
		Item item = new Item()
				.setDescription("John")
				.setName("name")
				.setAvailable(true)
				.setOwner(testUtils.makeNewUser(1))
				.setRequest(testUtils.makeNewItemRequest(10, testUtils.makeNewUser(2)));

		em.persist(item);
		em.flush();
		em.clear();

		Item proxy = em.getReference(Item.class, item.getId());
		Class<?> clazz = HibernateEqualsAndHashCode.persistentClass(proxy);

		assertEquals(Item.class, clazz);
	}

	@Test
	void shouldBeEqualForProxyAndEntityWithSameId_byItem() {
		Item item = new Item()
				.setDescription("John")
				.setName("name")
				.setAvailable(true)
				.setOwner(testUtils.makeNewUser(1))
				.setRequest(testUtils.makeNewItemRequest(10, testUtils.makeNewUser(2)));

		em.persist(item);
		em.flush();
		em.clear();

		Item proxy = em.getReference(Item.class, item.getId());
		Item entity = em.find(Item.class, item.getId());

		assertEquals(proxy, entity);
		assertEquals(entity, proxy);
		assertEquals(entity.hashCode(), proxy.hashCode());
	}
}
