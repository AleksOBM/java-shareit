package ru.practicum.shareit;

import org.hamcrest.Matcher;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

public class TestUtils {

	public final LocalDateTime pastDate = LocalDateTime.parse("2023-01-01T00:00:01");
	public final LocalDateTime futureDate = LocalDateTime.parse("2028-01-01T00:00:01");

	public String generateRandomText(int length, String addAfterStr, Character... addSynbols) {
		java.util.List<Character> symbols = new ArrayList<>(List.of(
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z'
		));
		symbols.addAll(Arrays.asList(addSynbols));
		Random random = new Random();
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(symbols.size());
			sb.append(symbols.get(index));
		}

		return sb.append(addAfterStr).toString();
	}

	public User makeNewUser(int id) {
		return new User()
				.setId((long) id)
				.setName(generateRandomText(10, "", ' '))
				.setEmail(generateRandomText(10, "@mail.ru"));
	}

	public User makeNewUser(long id) {
		return new User()
				.setId(id)
				.setName(generateRandomText(10, "", ' '))
				.setEmail(generateRandomText(10, "@mail.ru"));
	}

	public User makeNewUser() {
		return new User()
				.setId(null)
				.setName(generateRandomText(10, "", ' '))
				.setEmail(generateRandomText(10, "@mail.ru"));
	}

	public User getCopyOfUser(User user) {
		return new User()
				.setId(user.getId())
				.setName(user.getName())
				.setEmail(user.getEmail());
	}

	public Matcher<UserDto> matchesUserDto(User user) {
		return allOf(
				hasProperty("id", notNullValue()),
				hasProperty("name", equalTo(user.getName())),
				hasProperty("email", equalTo(user.getEmail()))
		);
	}

	public Item makeNewItem(int id, User owner, ItemRequest request) {
		return new Item()
				.setId((long) id)
				.setName(generateRandomText(25, "", ' ', ' '))
				.setDescription(generateRandomText(50, "", ' ', ' ', ' ', ' '))
				.setAvailable(true)
				.setOwner(owner)
				.setRequest(request);
	}

	public Item makeNewFastItem(int id) {
		return makeNewItem(id, makeNewUser(id + 1), makeNewItemRequest(id + 10, makeNewUser(id + 2)));
	}

	public ItemRequest makeNewItemRequest(int id, User requestor) {
		return new ItemRequest()
				.setId(id)
				.setDescription(generateRandomText(50, "", ' ', ' ', ' ', ' '))
				.setCreatedDate(LocalDateTime.now().minusHours(1))
				.setRequestor(requestor);
	}

	public Comment makeNewComment(int id, Item item, User author, LocalDateTime date) {
		return new Comment()
				.setId((long) id)
				.setText(generateRandomText(50, "", ' ', ' ', ' ', ' '))
				.setItem(item)
				.setAuthor(author)
				.setCreatedDate(date);
	}

	public Booking makeNewAnyFullFastBooking(int id, LocalDateTime date, BookingStatus status) {
		User owner = makeNewUser(id + 10);
		User booker = makeNewUser(id + 11);
		Item item = makeNewItem(id + 20, owner, makeNewItemRequest(id + 30, booker));
		return new Booking()
				.setId((long) id)
				.setItem(item)
				.setBooker(booker)
				.setStart(date)
				.setEnd(date.plusHours(10))
				.setStatus(status);
	}

	public Booking makeNewAnyFullBooking(int id, User booker, User owner, LocalDateTime date, BookingStatus status) {
		Item item = makeNewItem(id + 20, owner, makeNewItemRequest(id + 30, booker));
		return new Booking()
				.setId((long) id)
				.setItem(item)
				.setBooker(booker)
				.setStart(date)
				.setEnd(date.plusHours(10))
				.setStatus(status);
	}

	public Item getCopyOfItem(Item item) {
		return new Item()
				.setId(item.getId())
				.setName(item.getName())
				.setDescription(item.getDescription())
				.setAvailable(item.isAvailable())
				.setOwner(item.getOwner())
				.setRequest(item.getRequest());
	}
}
