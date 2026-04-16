package ru.practicum.shareit.booking.repository;

import com.sun.istack.NotNull;
import jakarta.persistence.EntityManager;
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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {

	final TestUtils testUtils = new TestUtils();

	final EntityManager entityManager;

	final UserRepository userRepository;
	final ItemRequestRepository requestRepository;
	final ItemRepository itemRepository;
	final BookingRepository bookingRepository;

	private User getCheckedUser(@NotNull User user) {
		return user.getId() == null ?
				userRepository.save(user) :
				entityManager.merge(user);
	}

	private Item getCheckedItem(@NotNull Item item) {
		return item.getId() == null ?
				magicSaveItem(item) :
				entityManager.merge(item);
	}

	private ItemRequest getCheckedRequest(ItemRequest request) {
		return request.getId() == null ?
				magicSaveRequest(request) :
				entityManager.merge(request);
	}

	private Booking magicSaveBooking(Booking booking) {
		return bookingRepository.save(new Booking()
				.setBooker(getCheckedUser(booking.getBooker()))
				.setItem(getCheckedItem(booking.getItem()))
				.setStart(booking.getStart())
				.setEnd(booking.getEnd())
				.setStatus(booking.getStatus())
		);
	}

	private Item magicSaveItem(Item item) {
		return itemRepository.save(
				new Item()
						.setName(item.getName())
						.setDescription(item.getDescription())
						.setAvailable(item.isAvailable())
						.setOwner(getCheckedUser(item.getOwner()))
						.setRequest(item.getRequest() == null ? null : getCheckedRequest(item.getRequest()))
		);
	}

	private ItemRequest magicSaveRequest(ItemRequest request) {
		return requestRepository.save(
				new ItemRequest()
						.setRequestor(getCheckedUser(request.getRequestor()))
						.setDescription(request.getDescription())
						.setCreatedDate(request.getCreatedDate())
		);
	}

	Booking saveNewMagicBooking(LocalDateTime date, BookingStatus status) {
		return magicSaveBooking(
				testUtils.makeNewAnyFullFastBooking(date, status, null));
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
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now(), BookingStatus.WAITING),
				saveNewMagicBooking(LocalDateTime.now(), BookingStatus.WAITING),
				saveNewMagicBooking(LocalDateTime.now(), BookingStatus.WAITING)
		);
		User owner = bookings.get(0).getItem().getOwner();
		entityManager.merge(bookings.get(1).getItem().setOwner(owner));
		// endregion setup

		List<Booking> bookingList = bookingRepository.findAllByOwner(owner.getId());

		assertThat(bookingList, hasSize(2));
		assertThat(bookingList, contains(matchesBooking(bookings.get(1)), matchesBooking(bookings.get(0))));
	}

	@Test
	void findCurrentByOwner() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusHours(5), BookingStatus.WAITING), // current
				saveNewMagicBooking(LocalDateTime.now().minusHours(5), BookingStatus.WAITING) // current
		);
		User owner = bookings.get(0).getItem().getOwner();
		entityManager.merge(bookings.get(1).getItem().setOwner(owner));
		// endregion setup

		List<Booking> bookingList = bookingRepository.findCurrentByOwner(owner.getId());

		assertThat(bookingList, hasSize(1));
		assertThat(bookingList, contains(matchesBooking(bookings.get(1))));
	}

	@Test
	void findPastByOwner() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING) // past
		);
		User owner = bookings.get(0).getItem().getOwner();
		entityManager.merge(bookings.get(1).getItem().setOwner(owner));
		// endregion setup

		List<Booking> bookingList = bookingRepository.findPastByOwner(owner.getId());

		assertThat(bookingList, hasSize(1));
		assertThat(bookingList, contains(matchesBooking(bookings.get(1))));
	}

	@Test
	void findFutureByOwner() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING) // past
		);
		User owner = bookings.get(0).getItem().getOwner();
		entityManager.merge(bookings.get(1).getItem().setOwner(owner));
		// endregion setup

		List<Booking> bookingList = bookingRepository.findFutureByOwner(owner.getId());

		assertThat(bookingList, hasSize(1));
		assertThat(bookingList, contains(matchesBooking(bookings.getFirst())));
	}

	@Test
	void findAllByOwnerAndStatusOrderByStartDesc() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.REJECTED) // past
		);
		User owner = bookings.get(0).getItem().getOwner();
		entityManager.merge(bookings.get(1).getItem().setOwner(owner));
		// endregion setup

		List<Booking> bookingList = bookingRepository
				.findAllByOwnerAndStatusOrderByStartDesc(owner.getId(), BookingStatus.WAITING);

		assertThat(bookingList, hasSize(2));
		assertThat(bookingList, contains(matchesBooking(bookings.get(0)), matchesBooking(bookings.get(1))));
	}

	@Test
	void findCurrentByBooker() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusHours(5), BookingStatus.WAITING), // current
				saveNewMagicBooking(LocalDateTime.now().minusHours(5), BookingStatus.WAITING) // current
		);
		User booker = bookings.get(0).getBooker();
		entityManager.merge(bookings.get(1).setBooker(booker));
		// endregion setup

		List<Booking> bookingList = bookingRepository.findCurrentByBooker(booker.getId());

		assertThat(bookingList, hasSize(1));
		assertThat(bookingList, contains(matchesBooking(bookings.get(1))));
	}

	@Test
	void findPastByBooker() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING) // past
		);
		User booker = bookings.get(0).getBooker();
		entityManager.merge(bookings.get(1).setBooker(booker));
		// endregion setup

		List<Booking> bookingList = bookingRepository.findPastByBooker(booker.getId());

		assertThat(bookingList, hasSize(1));
		assertThat(bookingList, contains(matchesBooking(bookings.get(1))));
	}

	@Test
	void findFutureByBooker() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING) // past
		);
		User booker = bookings.get(0).getBooker();
		entityManager.merge(bookings.get(1).setBooker(booker));
		// endregion setup

		List<Booking> bookingList = bookingRepository.findFutureByBooker(booker.getId());

		assertThat(bookingList, hasSize(1));
		assertThat(bookingList, contains(matchesBooking(bookings.getFirst())));
	}

	@Test
	void findLastBookingDate() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().minusDays(6), BookingStatus.WAITING) // past
		);
		Item item = bookings.get(0).getItem();
		entityManager.merge(bookings.get(1).setItem(item));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		// endregion setup
		LocalDateTime date = bookingRepository.findLastBookingDate(item.getId());

		assertThat(date.truncatedTo(ChronoUnit.SECONDS).toString(),
				equalTo(bookings.get(1).getStart().truncatedTo(ChronoUnit.SECONDS).format(formatter))
		);
	}

	@Test
	void findNextBookingDate() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().minusDays(1), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().plusDays(5), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().plusDays(4), BookingStatus.WAITING) // future
		);
		Item item = bookings.get(0).getItem();
		entityManager.merge(bookings.get(1).setItem(item));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		// endregion setup
		LocalDateTime date = bookingRepository.findNextBookingDate(item.getId());

		assertThat(date.truncatedTo(ChronoUnit.SECONDS).toString(),
				equalTo(bookings.get(1).getStart().truncatedTo(ChronoUnit.SECONDS).format(formatter))
		);
	}

	@Test
	void findAllByBookerIdOrderByStartDesc() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().minusDays(1), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().plusDays(5), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().plusDays(4), BookingStatus.WAITING) // future
		);
		User booker = bookings.get(0).getBooker();
		entityManager.merge(bookings.get(1).setBooker(booker));
		// endregion setup

		List<Booking> bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId());

		assertThat(bookingList, hasSize(2));
		assertThat(bookingList, contains(matchesBooking(bookings.get(1)), matchesBooking(bookings.get(0))));
	}

	@Test
	void findAllByBooker_IdAndStatusOrderByStartDesc() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.REJECTED) // past
		);
		User booker = bookings.get(0).getBooker();
		entityManager.merge(bookings.get(1).setBooker(booker));
		// endregion setup

		List<Booking> bookingList = bookingRepository
				.findAllByBooker_IdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.WAITING);

		assertThat(bookingList, hasSize(2));
		assertThat(bookingList, contains(matchesBooking(bookings.get(0)), matchesBooking(bookings.get(1))));
	}

	@Test
	void findAllBookingByItem_Id() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().minusDays(6), BookingStatus.WAITING) // past
		);
		Item item = bookings.get(0).getItem();
		entityManager.merge(bookings.get(1).setItem(item));
		// endregion setup

		List<Booking> bookingList = bookingRepository.findAllBookingByItem_Id(item.getId());

		assertThat(bookingList, hasSize(2));
		assertThat(bookingList, contains(matchesBooking(bookings.get(0)), matchesBooking(bookings.get(1))));
	}

	@Test
	void findAllBookingByItemIdIn() {
		// region setup
		List<Booking> bookings = List.of(
				saveNewMagicBooking(LocalDateTime.now().plusDays(1), BookingStatus.WAITING), // future
				saveNewMagicBooking(LocalDateTime.now().minusDays(5), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().minusDays(6), BookingStatus.WAITING), // past
				saveNewMagicBooking(LocalDateTime.now().minusDays(6), BookingStatus.WAITING) // past
		);
		Item item1 = bookings.get(0).getItem();
		entityManager.merge(bookings.get(1).setItem(item1));
		Item item2 = bookings.get(2).getItem();
		// endregion setup

		List<Booking> bookingList = bookingRepository.findAllBookingByItemIdIn(Set.of(item1.getId(), item2.getId()));

		assertThat(bookingList, hasSize(3));
		assertThat(bookingList, contains(
				matchesBooking(bookings.get(0)),
				matchesBooking(bookings.get(1)),
				matchesBooking(bookings.get(2))
				));
	}
}