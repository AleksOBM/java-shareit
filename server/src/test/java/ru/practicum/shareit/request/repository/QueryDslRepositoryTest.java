package ru.practicum.shareit.request.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@Transactional
@Import({QuerydslTestConfig.class, QueryDslRepository.class})
public class QueryDslRepositoryTest {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	QueryDslRepository queryDslRepository;

	final TestUtils testUtils = new TestUtils();

	private User addNewUser() {
		User user = testUtils.makeNewUser();
		entityManager.persist(user);
		return user;
	}

	private ItemRequest addNewItemRequest(User requestor) {
		ItemRequest itemRequest = testUtils.makeNewItemRequest(requestor, LocalDateTime.now());
		entityManager.persist(itemRequest);
		return itemRequest;
	}

	private Item addNewItem(User owner, ItemRequest request) {
		Item item = testUtils.makeNewItem(owner, request);
		entityManager.persist(item);
		return item;
	}

	@Test
	void getMapOfItemsByRequestId() {
		List<User> users = List.of(
				addNewUser(),
				addNewUser(),
				addNewUser(),
				addNewUser(),
				addNewUser(),
				addNewUser()
		);

		List<ItemRequest> itemRequests = List.of(
				addNewItemRequest(users.get(0)),
				addNewItemRequest(users.get(1)),
				addNewItemRequest(users.get(2))
		);

		List<Item> items = List.of(
				addNewItem(users.get(0), itemRequests.get(2)),
				addNewItem(users.get(1), itemRequests.get(0)),
				addNewItem(users.get(2), itemRequests.get(1)),
				addNewItem(users.get(3), itemRequests.get(0)),
				addNewItem(users.get(4), itemRequests.get(1)),
				addNewItem(users.get(5), itemRequests.get(2))
		);

		Map<Long, List<Item>> localResult = new HashMap<>();
		for (Item item : items) {
			localResult.computeIfAbsent(
					item.getRequest().getId(), k -> new ArrayList<>())
					.add(item);
		}

		List<Long> itemIds = localResult.keySet().stream().toList();

		Map<Long, List<Item>> repositoryResult = queryDslRepository.getMapOfItemsByRequestId(itemIds);

		assertThat(localResult, is(repositoryResult));
	}

	@Test
	void getAllRequestsWithoutByUser() {
		List<User> users = List.of(
				addNewUser(),
				addNewUser(),
				addNewUser()
		);

		List<ItemRequest> itemRequests = List.of(
				addNewItemRequest(users.get(0)),
				addNewItemRequest(users.get(1)),
				addNewItemRequest(users.get(2))
		);

		List<ItemRequest> result = queryDslRepository.getAllRequestsWithoutThisUserRequests(users.getFirst().getId());

		assertThat(result, is(List.of(itemRequests.get(2), itemRequests.get(1))));
	}

	@Test
	void getAllItemsByRequest() {
		List<User> users = List.of(
				addNewUser(),
				addNewUser(),
				addNewUser()
		);

		List<ItemRequest> itemRequests = List.of(
				addNewItemRequest(users.get(0)),
				addNewItemRequest(users.get(1))
		);

		List<Item> items = List.of(
				addNewItem(users.get(0), itemRequests.getLast()),
				addNewItem(users.get(1), itemRequests.getFirst()),
				addNewItem(users.get(2), itemRequests.getLast()),
				addNewItem(users.get(0), itemRequests.getFirst()),
				addNewItem(users.get(1), itemRequests.getLast()),
				addNewItem(users.get(2), itemRequests.getLast())
		);

		List<Item> first = items.stream()
				.filter(item -> item.getRequest().equals(itemRequests.getFirst()))
				.toList();

		List<Item> last = items.stream()
				.filter(item -> item.getRequest().equals(itemRequests.getLast()))
				.toList();

		List<Item> resultFirst = queryDslRepository.getAllItemsByRequest(itemRequests.getFirst().getId());
		List<Item> resultLast = queryDslRepository.getAllItemsByRequest(itemRequests.getLast().getId());

		assertThat(first, is(resultFirst));
		assertThat(last, is(resultLast));
	}
}

