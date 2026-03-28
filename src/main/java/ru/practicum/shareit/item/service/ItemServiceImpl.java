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
	public ItemDtoFullVersion getItem(long userId, long itemId) {
		Item item = getItemWithCheckPresent(itemId);

		if (item.getOwner().getId() != userId) {
			return ItemMapper.toItemDtoFull(
					item,
					null,
					null,
					getCommentDtos(itemId)
			);
		}
		return ItemMapper.toItemDtoFull(
				item,
				bookingRepository.findLastBookingDate(itemId),
				bookingRepository.findNextBookingDate(itemId),
				getCommentDtos(itemId)
				);
	}

	@Override
	public List<ItemDtoFullVersion> getAllItemsOfUser(long userId) {
		checkUser(userId);
		Map<Long, Item> items = itemRepository.findAllByOwner_Id(userId)
				.stream()
				.collect(Collectors.toMap(Item::getId, Function.identity()));
		List<Booking> bookings = bookingRepository.findAllBookingByItemIdIn(items.keySet());

		return items.values().stream()
				.map(item -> ItemMapper.toItemDtoFull(
						item,
						getLastBookingDate(bookings),
						getNextBookingDate(bookings),
						getCommentDtos(item.getId())
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
					"userId", "Комментарии доступны только тем, кто уже по пользовался вещью"
			);
		}
		return CommentMapper.toDto(commentRepository.save(
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
		LocalDateTime now = LocalDateTime.now();
		return bookings.stream()
				.map(Booking::getStart)
				.filter(start -> !start.isAfter(now))
				.max(LocalDateTime::compareTo)
				.orElse(null);
	}

	private LocalDateTime getNextBookingDate(@NonNull List<Booking> bookings) {
		LocalDateTime now = LocalDateTime.now();
		return bookings.stream()
				.map(Booking::getStart)
				.filter(start -> start.isAfter(now))
				.min(LocalDateTime::compareTo)
				.orElse(null);
	}

	private List<CommentDto> getCommentDtos(long itemId) {
		return commentRepository.findAllByItemId(itemId).stream()
				.map(CommentMapper::toDto)
				.toList();
	}
}
