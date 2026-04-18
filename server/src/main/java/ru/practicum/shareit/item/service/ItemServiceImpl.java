package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UtilService;
import ru.practicum.shareit.util.exception.ForbiddenException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ParameterNotValidException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class ItemServiceImpl implements ItemService {

	ItemRepository itemRepository;
	BookingRepository bookingRepository;
	CommentRepository commentRepository;
	UtilService utilService;

	@Override
	public ItemBigDto getItem(long userId, long itemId) {
		Item item = utilService.getItem(itemId);

		if (item.getOwner().getId() != userId) {
			return ItemMapper.toBigDto(
					item,
					null,
					null,
					getCommentDtos(itemId)
			);
		}
		return ItemMapper.toBigDto(
				item,
				bookingRepository.findLastBookingDate(itemId),
				bookingRepository.findNextBookingDate(itemId),
				getCommentDtos(itemId)
		);
	}

	@Override
	public List<ItemBigDto> getAllItemsOfUser(long userId) {
		utilService.checkUser(userId);
		Map<Long, Item> items = itemRepository.findAllByOwner_Id(userId)
				.stream()
				.collect(Collectors.toMap(Item::getId, Function.identity()));
		List<Booking> bookings = bookingRepository.findAllBookingByItemIdIn(items.keySet());
		Map<Long, Comment> comments = commentRepository.findAllByItemIdIn(items.keySet()).stream()
				.collect(Collectors.toMap(comment -> comment.getItem().getId(), Function.identity()));

		List<ItemBigDto> result = new ArrayList<>();
		for (Item item : items.values()) {
			result.add(ItemMapper.toBigDto(
					item,
					getLastBookingDate(bookings, item.getId()),
					getNextBookingDate(bookings, item.getId()),
					comments.entrySet().stream()
							.filter(entry -> entry.getKey().equals(item.getId()))
							.map(Map.Entry::getValue)
							.map(CommentMapper::toDto)
							.toList()
			));
		}

		return result;
	}

	@Override
	public List<ItemDto> search(long userId, String text) {
		utilService.checkUser(userId);
		return itemRepository.search(text).stream()
				.filter(item -> item.getOwner().getId() != userId)
				.map(ItemMapper::toDto)
				.toList();
	}

	@Override
	public ItemDto addNewItem(Long userId, @NonNull ItemDto itemDto) {
		User user = utilService.getUser(userId);
		Long itemRequestId = itemDto.getRequestId();
		if (itemDto.getRequestId() == null) {
			return ItemMapper.toDto(itemRepository.save(ItemMapper.toEntity(itemDto, user, null)));
		} else {
			ItemRequest request = utilService.getItemRequest(itemRequestId);
			return ItemMapper.toDto(itemRepository.save(ItemMapper.toEntity(itemDto, user, request)));
		}
	}

	@Override
	public ItemDto updateItem(long userId, long itemId, @NonNull ItemDto itemDto) {
		utilService.checkUser(userId);
		Item oldItem = getItemWithOwner(itemId, userId);
		Item item = new Item()
				.setID(itemId)
				.setName(itemDto.getName() == null ? oldItem.getName() : itemDto.getName())
				.setDescription(itemDto.getDescription() == null ? oldItem.getDescription() : itemDto.getDescription())
				.setAvailable(itemDto.getAvailable() == null ? oldItem.isAvailable() : itemDto.getAvailable())
				.setOwner(utilService.getUser(userId))
				.setRequest(itemDto.getRequestId() == null ? null : utilService.getItemRequest(itemDto.getRequestId()));
		return ItemMapper.toDto(itemRepository.save(item));
	}

	@Override
	public void deleteItem(long userId, long itemId) {
		utilService.checkUser(userId);
		getItemWithOwner(itemId, userId);
		itemRepository.deleteById(itemId);
	}

	@Override
	public CommentDto addComment(long userId, long itemId, @NonNull CommentDto commentDto) {
		User booker = utilService.getUser(userId);
		Item item = utilService.getItem(itemId);
		if (item.getOwner().getId().equals(userId)) {
			throw new ForbiddenException("Нельзя оставить комментарий к своей вещи");
		}

		List<Booking> bookings = bookingRepository.findAllBookingByItem_Id(item.getId()).stream()
				.filter(booking -> booking.getBooker().getId() == userId)
				.toList();

		if (bookings.isEmpty() || bookings.stream()
				.noneMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now()))) {
			throw new ParameterNotValidException(
					"userId", "Комментарии доступны только тем, кто уже по пользовался вещью"
			);
		}

		Comment commentToSave = CommentMapper.toComment(commentDto, item, booker);
		Comment savedComment = commentRepository.save(commentToSave);
		return CommentMapper.toDto(savedComment);
	}

	@NonNull
	private Item getItemWithOwner(long itemId, long userId) {
		Item item = utilService.getItem(itemId);
		if (item.getOwner().getId() == userId) {
			return item;
		}
		throw new NotFoundException("Пользователь с id=" + userId + " не является владельцем вещи с id=" + itemId);
	}

	private LocalDateTime getLastBookingDate(@NonNull List<Booking> bookings, Long itemId) {
		LocalDateTime now = LocalDateTime.now();
		return bookings.stream()
				.filter(booking -> booking.getItem().getId().equals(itemId))
				.map(Booking::getStart)
				.filter(start -> !start.isAfter(now))
				.max(LocalDateTime::compareTo)
				.orElse(null);
	}

	private LocalDateTime getNextBookingDate(@NonNull List<Booking> bookings, Long itemId) {
		LocalDateTime now = LocalDateTime.now();
		return bookings.stream()
				.filter(booking -> booking.getItem().getId().equals(itemId))
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
