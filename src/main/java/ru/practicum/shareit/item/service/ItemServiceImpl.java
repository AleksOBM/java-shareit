package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.ForbiddenException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ParameterNotValidException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	ItemRepository itemRepository;
	UserRepository userRepository;
	BookingRepository bookingRepository;
	CommentRepository commentRepository;

	@Override
	public ItemDtoWithComments getItem(long itemId) {
		Item item = getItemWithCheckPresent(itemId);
		List<Booking> bookings = bookingRepository.findAllBookingByItem_Id(item.getId());

		return ItemMapper.toItemDtoWithComments(
				item,
				getLastBookingDate(bookings),
				getNextBookingDate(bookings),
				commentRepository.findAllByItemId(itemId).stream()
						.map(CommentDto::from)
						.toList()
				);
	}

	@Override
	public List<ItemDtoWithDates> getAllItemsOfUser(long userId) {
		checkUser(userId);
		Map<Long, Item> items = itemRepository.findAllByOwner_Id(userId)
				.stream()
				.collect(Collectors.toMap(Item::getId, Function.identity()));
		List<Booking> bookings = bookingRepository.findAllBookingByItemIdIn(items.keySet());

		return items.values().stream()
				.map(item -> ItemMapper.toItemDtoWithDates(
						item,
						getLastBookingDate(bookings),
						getNextBookingDate(bookings)
				)).toList();
	}

	@Override
	public List<ItemDto> search(long userId, String text) {
		if (text == null || text.isEmpty()) {
			return Collections.emptyList();
		}
		return itemRepository.search(text).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	public ItemDto addNewItem(Long userId, ItemDto itemDto) {
		User user = getUserWithCheckPresent(userId);
		return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, user)));
	}

	@Override
	public ItemDto updateItem(long userId, long itemId, @NonNull ItemDto itemDto) {
		checkUser(userId);
		Item oldItem = getItemWithCheckPresentAndOwner(itemId, userId);
		Item item = new Item(
				itemId,
				itemDto.getName() == null ? oldItem.getName() : itemDto.getName(),
				itemDto.getDescription() == null ? oldItem.getDescription() : itemDto.getDescription(),
				itemDto.getAvailable() == null ? oldItem.isAvailable() : itemDto.getAvailable(),
				getUserWithCheckPresent(userId),
				null
		);
		return ItemMapper.toItemDto(itemRepository.save(item));
	}

	@Override
	public void deleteItem(long userId, long itemId) {
		checkUser(userId);
		getItemWithCheckPresentAndOwner(itemId, userId);
		itemRepository.deleteById(itemId);
	}

	@Override
	public CommentDto addComment(long userId, long itemId, @NonNull CommentDto commentDto) {
		String text = commentDto.getText();
		if (text == null || text.isBlank()) {
			throw new ParameterNotValidException("text", "Текст комментария не может быть пустым");
		}
		User user = getUserWithCheckPresent(userId);
		Item item = getItemWithCheckPresent(itemId);
		if (item.getOwner().getId().equals(userId)) {
			throw new ForbiddenException("Нельзя оставить комментарий к своей вещи");
		}
		if (bookingRepository.findAllBookingByItem_Id(item.getId()).stream()
				.noneMatch(booking -> booking.getBooker().getId().equals(userId) &&
						booking.getEnd().isBefore(LocalDateTime.now()))
		) {
			throw new ParameterNotValidException(
					"userId", "Комментарии доступны только тем, кто уже по пользовался вешью"
			);
		}
		return CommentDto.from(commentRepository.save(
				new Comment(null, item, user, LocalDateTime.now(), text.trim())
				)
		);
	}

	private void checkUser(long userId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
		}
	}

	@NonNull
	private User getUserWithCheckPresent(long userId) {
		return userRepository.findById(userId).orElseThrow(() ->
				new NotFoundException("Пользователь с id=" + userId + " не найден.")
		);
	}

	@NonNull
	private Item getItemWithCheckPresent(long itemId) {
		return itemRepository.findById(itemId).orElseThrow(() ->
				new NotFoundException("Вещь с id=" + itemId + " не найдена.")
		);
	}

	@NonNull
	private Item getItemWithCheckPresentAndOwner(long itemId, long userId) {
		Item item = getItemWithCheckPresent(itemId);
		if (item.getOwner().getId() == userId) {
			return item;
		}
		throw new NotFoundException("Пользователь с id=" + userId + " не является владельцем вещи с id=" + itemId);
	}

	private LocalDateTime getLastBookingDate(@NonNull List<Booking> bookings) {
		return bookings.stream()
				.filter(booking -> Duration.between(booking.getStart(), booking.getEnd()).toMinutes() > 30)
				.map(Booking::getStart)
				.filter(ldt -> ldt.isBefore(LocalDateTime.now()) ||
						ldt.isEqual(LocalDateTime.now()))
				.sorted()
				.findFirst().orElse(null);
	}

	private LocalDateTime getNextBookingDate(@NonNull List<Booking> bookings) {
		return bookings.stream()
				.filter(booking -> Duration.between(booking.getStart(), booking.getEnd()).toMinutes() > 30)
				.map(Booking::getStart)
				.filter(ldt -> ldt.isAfter(LocalDateTime.now()))
				.sorted()
				.findFirst().orElse(null);
	}
}
