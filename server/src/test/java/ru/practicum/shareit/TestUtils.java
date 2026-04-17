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
				.setID((long) id)
				.setName(generateRandomText(10, "", ' '))
				.setEmail(generateRandomText(10, "@mail.ru"));
	}

	public User makeNewUser(long id) {
		return new User()
				.setID(id)
				.setName(generateRandomText(10, "", ' '))
				.setEmail(generateRandomText(10, "@mail.ru"));
	}

	public User makeNewUser() {
		return new User()
				.setName(generateRandomText(10, "", ' '))
				.setEmail(generateRandomText(10, "@mail.ru"));
	}

	public User getCopyOfUser(User user) {
		return new User()
				.setID(user.getId())
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
				.setID((long) id)
				.setName(generateRandomText(25, "", ' ', ' '))
				.setDescription(generateRandomText(50, "", ' ', ' ', ' ', ' '))
				.setAvailable(true)
				.setOwner(owner)
				.setRequest(request);
	}

	public Item makeNewItem(long id, User owner, ItemRequest request) {
		return new Item()
				.setID(id)
				.setName(generateRandomText(25, "", ' ', ' '))
				.setDescription(generateRandomText(50, "", ' ', ' ', ' ', ' '))
				.setAvailable(true)
				.setOwner(owner)
				.setRequest(request);
	}

	public Item makeNewItem(User owner, ItemRequest request) {
		return new Item()
				.setName(generateRandomText(25, "", ' ', ' '))
				.setDescription(generateRandomText(50, "", ' ', ' ', ' ', ' '))
				.setAvailable(true)
				.setOwner(owner)
				.setRequest(request);
	}

	public Item makeNewFastItem(int id) {
		return makeNewItem(id, makeNewUser(id + 10), makeNewItemRequest(id + 20, makeNewUser(id + 11)));
	}

	public ItemRequest makeNewItemRequest(int id, User requestor) {
		return new ItemRequest()
				.setID((long) id)
				.setDescription(generateRandomText(50, "", ' ', ' ', ' ', ' '))
				.setCreatedDate(LocalDateTime.now().minusHours(1))
				.setRequestor(requestor);
	}

	public ItemRequest makeNewItemRequest(int id, User requestor, LocalDateTime createdDate) {
		return new ItemRequest()
				.setID((long) id)
				.setDescription(generateRandomText(50, "", ' ', ' ', ' ', ' '))
				.setCreatedDate(createdDate)
				.setRequestor(requestor);
	}

	public ItemRequest makeNewItemRequest(User requestor, LocalDateTime createdDate) {
		return new ItemRequest()
				.setDescription(generateRandomText(50, "", ' ', ' ', ' ', ' '))
				.setCreatedDate(createdDate)
				.setRequestor(requestor);
	}

	public Comment makeNewComment(int id, Item item, User author, LocalDateTime date) {
		return new Comment()
				.setID((long) id)
				.setText(generateRandomText(50, "", ' ', ' ', ' ', ' '))
				.setItem(item)
				.setAuthor(author)
				.setCreatedDate(date);
	}

	public Booking makeNewAnyFullFastBooking(int id, LocalDateTime date, BookingStatus status) {
		User owner = makeNewUser(id + 10);
		User booker = makeNewUser(id + 11);
		Item item = makeNewItem(id + 20, owner, makeNewItemRequest(id + 30, booker, date.plusHours(11)));
		return new Booking()
				.setID((long) id)
				.setItem(item)
				.setBooker(booker)
				.setStart(date)
				.setEnd(date.plusHours(10))
				.setStatus(status);
	}

	public Booking makeNewAnyFullFastBooking(int id, LocalDateTime date, BookingStatus status, ItemRequest request) {
		User owner = makeNewUser(id + 10);
		User booker = makeNewUser(id + 11);
		Item item = makeNewItem(id + 20, owner, request);
		return new Booking()
				.setID((long) id)
				.setItem(item)
				.setBooker(booker)
				.setStart(date)
				.setEnd(date.plusHours(10))
				.setStatus(status);
	}

	public Booking makeNewAnyFullFastBooking(LocalDateTime date, BookingStatus status, ItemRequest request) {
		User owner = makeNewUser();
		User booker = makeNewUser();
		Item item = makeNewItem(owner, request);
		return new Booking()
				.setItem(item)
				.setBooker(booker)
				.setStart(date)
				.setEnd(date.plusHours(10))
				.setStatus(status);
	}

	public Booking makeNewAnyFullFastBooking(long id, LocalDateTime date, BookingStatus status, ItemRequest request) {
		User owner = makeNewUser(id + 10);
		User booker = makeNewUser(id + 11);
		Item item = makeNewItem(id + 20, owner, request);
		return new Booking()
				.setID(id)
				.setItem(item)
				.setBooker(booker)
				.setStart(date)
				.setEnd(date.plusHours(10))
				.setStatus(status);
	}

	public Booking makeNewAnyFullBooking(int id, User booker, User owner, LocalDateTime date, BookingStatus status) {
		Item item = makeNewItem(id + 20, owner, makeNewItemRequest(id + 30, booker));
		return new Booking()
				.setID((long) id)
				.setItem(item)
				.setBooker(booker)
				.setStart(date)
				.setEnd(date.plusHours(10))
				.setStatus(status);
	}

	public Item makeCopyOfItem(Item item) {
		return new Item()
				.setID(item.getId())
				.setName(item.getName())
				.setDescription(item.getDescription())
				.setAvailable(item.isAvailable())
				.setOwner(item.getOwner())
				.setRequest(item.getRequest());
	}

	public Booking makeCopyOfBooking(Booking booking) {
		return new Booking()
				.setID(booking.getId())
				.setItem(booking.getItem())
				.setBooker(booking.getBooker())
				.setStart(booking.getStart())
				.setEnd(booking.getEnd())
				.setStatus(booking.getStatus());
	}

	public ItemRequest makeCopyOfRequest(ItemRequest request) {
		return new ItemRequest()
				.setID(request.getId())
				.setDescription(request.getDescription())
				.setCreatedDate(request.getCreatedDate())
				.setRequestor(request.getRequestor());
	}

	public Comment makeCopyOfComment(Comment comment) {
		return new Comment()
				.setID(comment.getId())
				.setText(comment.getText())
				.setItem(comment.getItem())
				.setAuthor(comment.getAuthor())
				.setCreatedDate(comment.getCreatedDate());
	}
}
