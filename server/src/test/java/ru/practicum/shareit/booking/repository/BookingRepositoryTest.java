package ru.practicum.shareit.booking.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@DataJpaTest(properties = {
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.url=jdbc:h2:mem:shareit"
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {

	final TestUtils testUtils = new TestUtils();

	final UserRepository userRepository;
	final ItemRequestRepository requestRepository;
	final ItemRepository itemRepository;
	final BookingRepository bookingRepository;
	final CommentRepository commentRepository;

	private User addNewUser() {
		return userRepository.save(testUtils.makeNewUser());
	}

	private ItemRequest addNewItemRequest(User user) {
		return requestRepository.save(
				new ItemRequest()
						.setRequestor(user)
						.setDescription(testUtils.generateRandomText(50, "", ' ', ' '))
						.setCreatedDate(LocalDateTime.now())
		);
	}

	private Item addNewItemWithRequest(User owner, User requestor) {
		return itemRepository.save(
				new Item()
						.setName(testUtils.generateRandomText(10, "", ' '))
						.setDescription(testUtils.generateRandomText(50, "", ' ', ' '))
						.setAvailable(true)
						.setOwner(owner)
						.setRequest(addNewItemRequest(requestor))
		);
	}

	private Booking addNewFullBooking(User requestor, User owner, User booker) {
		LocalDateTime now = LocalDateTime.now();
		return bookingRepository.save(
				new Booking()
						.setBooker(booker)
						.setItem(addNewItemWithRequest(owner, requestor))
						.setStart(now)
						.setEnd(now.plusHours(1))
						.setStatus(BookingStatus.APPROVED)
		);
	}

	private Booking addNewBooking(Item item, User booker, BookingStatus status) {
		LocalDateTime now = LocalDateTime.now();
		return bookingRepository.save(
				new Booking()
						.setBooker(booker)
						.setItem(item)
						.setStart(now)
						.setEnd(now.plusHours(1))
						.setStatus(status)
		);
	}

	private Matcher<Booking> matchesBooking(Booking booking) {
		return allOf(
				hasProperty("id", notNullValue()),
				hasProperty("booker", equalTo(booking.getBooker())),
				hasProperty("item", equalTo(booking.getItem())),
				hasProperty("start", equalTo(booking.getStart())),
				hasProperty("end", equalTo(booking.getEnd())),
				hasProperty("status", equalTo(booking.getStatus()))
		);
	}

	@Test
	void findAllByOwner() {
		User requestor = addNewUser();
		User owner = addNewUser();
		User booker = addNewUser();

		Booking booking1 = addNewFullBooking(requestor, owner, requestor);
		Item item = booking1.getItem();
		Booking booking2 = addNewBooking(item, booker, BookingStatus.WAITING);

		List<Booking> bookingList = bookingRepository.findAllByOwner(owner.getId());

		assertThat(bookingList, hasSize(2));
		assertThat(bookingList, contains(matchesBooking(booking2), matchesBooking(booking1)));
	}

	@Test
	void findCurrentByOwner() {
	}

	@Test
	void findPastByOwner() {
	}

	@Test
	void findFutureByOwner() {
	}

	@Test
	void findAllByOwnerAndStatusOrderByStartDesc() {
	}

	@Test
	void findCurrentByBooker() {
	}

	@Test
	void findPastByBooker() {
	}

	@Test
	void findFutureByBooker() {
	}

	@Test
	void findLastBookingDate() {
	}

	@Test
	void findNextBookingDate() {
	}

	@Test
	void findAllByBookerIdOrderByStartDesc() {
	}

	@Test
	void findAllByBooker_IdAndStatusOrderByStartDesc() {
	}

	@Test
	void findAllBookingByItem_Id() {
	}

	@Test
	void findAllBookingByItemIdIn() {
	}
}