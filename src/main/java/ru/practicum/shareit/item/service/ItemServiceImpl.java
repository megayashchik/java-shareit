package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;
	private final BookingRepository bookingRepository;
	private final CommentRepository commentRepository;

	@Override
	@Transactional
	public ItemResponse create(Long userId, CreateItemRequest request) {
		log.info("Создание вещи для пользователя с id = {}, запрос: {}", userId, request);

		if (request == null) {
			throw new IllegalArgumentException("Запрос не может быть null");
		}

		if (request.getName() == null || request.getName().isBlank()) {
			throw new IllegalArgumentException("Название не может быть пустым");
		}

		if (request.getDescription() == null || request.getDescription().isBlank()) {
			throw new IllegalArgumentException("Описание не может быть пустым");
		}

		if (request.getAvailable() == null) {
			throw new DuplicateEmailException("Поле доступность должно быть указано");
		}

		User foundUser = userRepository.findById(userId).orElseThrow(()
				-> new NotFoundException("Пользователь с id = " + userId + " не найден"));
		Item item = ItemMapper.mapToItemDto(foundUser, request);
		Item createdItem = itemRepository.save(item);
		log.info("Создана вещь: {}", createdItem);

		return ItemMapper.mapToItemDto(createdItem);
	}

	@Override
	@Transactional
	public ItemResponse update(Long itemId, Long ownerId, UpdateItemRequest request) {
		Item item = itemRepository.findById(itemId).orElseThrow(()
				-> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

		if (request == null) {
			throw new IllegalArgumentException("Запрос на обновление не может быть null");
		}

		if (!item.getOwner().getId().equals(ownerId)) {
			throw new NotOwnerException("Предмет с id = " + itemId + " не принадлежит пользователю");
		}

		if (request.getName() != null && request.getName().isBlank()) {
			throw new IllegalArgumentException("Название не может быть пустым");
		}

		if (request.getDescription() != null && request.getDescription().isBlank()) {
			throw new IllegalArgumentException("Описание не может быть пустым");
		}

		if (request.getName() != null) {
			item.setName(request.getName());
		}

		if (request.getDescription() != null) {
			item.setDescription(request.getDescription());
		}

		if (request.getAvailable() != null) {
			item.setAvailable(request.getAvailable());
		}

		log.info("Перед сохранением вещи: name {}, description {}, available {}",
				item.getName(), item.getDescription(), item.getAvailable());
		Item updatedItem = itemRepository.save(item);
		log.info("После сохранения вещи: name {}, description {}, available {}",
				updatedItem.getName(), updatedItem.getDescription(), updatedItem.getAvailable());

		return ItemMapper.mapToItemDto(updatedItem);
	}

	@Override
	@Transactional
	public void delete(Long ownerId, Long itemId) {
		log.info("Удаление вещи с id = {} от владельца {}", itemId, ownerId);
		Item item = itemRepository.findById(itemId).orElseThrow(()
				-> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

		if (!item.getOwner().getId().equals(ownerId)) {
			throw new NotOwnerException("Только владелец вещи может её удалить");
		}

		itemRepository.deleteById(itemId);
		log.info("Удалена вещь c id = {}: {}", itemId, item);
	}

	@Override
	@Transactional(readOnly = true)
	public ItemResponse findById(Long itemId, Long userId) {
		Item item = itemRepository.findById(itemId).orElseThrow(() ->
				new NotFoundException("Вещь " + itemId + " не найдена"));
		BookingResponse lastBooking = null;
		BookingResponse nextBooking = null;

		if (item.getOwner().getId().equals(userId)) {
			List<Booking> lastBookings = bookingRepository.findPastBookings(itemId,
					LocalDateTime.now(), Status.APPROVED);
			if (!lastBookings.isEmpty()) {
				lastBooking = BookingMapper.mapToBookingDto(lastBookings.get(0));
			}

			List<Booking> nextBookings = bookingRepository.findFutureBookings(itemId,
					LocalDateTime.now());
			if (!nextBookings.isEmpty()) {
				nextBooking = BookingMapper.mapToBookingDto(nextBookings.get(0));
			}
		}

		List<CommentResponse> comments = CommentMapper.mapToCommentList(commentRepository.findAllByItemId(itemId));

		return ItemMapper.mapToItemDtoWithBookingsAndComments(item, lastBooking, nextBooking, comments);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemResponse> findByOwnerId(Long ownerId) {
		log.info("Получение списка вещей владельца с id = {}", ownerId);
		userRepository.findById(ownerId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
		List<Item> items = itemRepository.findByOwnerId(ownerId);
		List<Long> itemIds = items.stream().map(Item::getId).toList();
		LocalDateTime now = LocalDateTime.now();

		List<Booking> lastBookingList = bookingRepository.findLastBookingByItemIds(itemIds, now, Status.APPROVED);
		Map<Long, BookingResponse> lastBookings = new HashMap<>();
		for (Booking booking : lastBookingList) {
			lastBookings.put(booking.getItem().getId(), BookingMapper.mapToBookingDto(booking));
		}

		List<Booking> nextBookingList = bookingRepository.findNextBookingByItemIds(itemIds, now, Status.APPROVED);
		Map<Long, BookingResponse> nextBookings = new HashMap<>();
		for (Booking booking : nextBookingList) {
			nextBookings.put(booking.getItem().getId(), BookingMapper.mapToBookingDto(booking));
		}

		List<Comment> commentList = commentRepository.findAllByItemIds(itemIds);
		Map<Long, List<CommentResponse>> commentsMap = new HashMap<>();
		for (Comment comment : commentList) {
			Long itemId = comment.getItem().getId();
			List<CommentResponse> comments = commentsMap.getOrDefault(itemId, new ArrayList<>());
			comments.add(CommentMapper.mapToCommentResponse(comment));
			commentsMap.put(itemId, comments);
		}

		List<ItemResponse> result = new ArrayList<>();
		for (Item item : items) {
			ItemResponse itemResponse = ItemMapper.mapToItemDtoWithBookingsAndComments(
					item,
					lastBookings.get(item.getId()),
					nextBookings.get(item.getId()),
					commentsMap.getOrDefault(item.getId(), List.of())
			);
			result.add(itemResponse);
		}

		log.info("Найдено {} вещей владельца с id = {}", items.size(), ownerId);

		return result;
	}

	@Override
	public List<ItemResponse> findItemsByText(String text) {
		log.info("Получение списка вещей по тексту: {}", text);
		if (text == null || text.isBlank()) {
			return List.of();
		} else {
			List<ItemResponse> items = itemRepository.findItemsByText(text).stream()
					.map(ItemMapper::mapToItemDto)
					.toList();
			log.info("Найдено {} вещей по тексту: {}", items.size(), text);

			return items;
		}
	}

	@Override
	@Transactional
	public CommentResponse addComment(Long userId, Long itemId, CreateCommentRequest request) {
		log.info("Добавление комментария от пользователя с id = {} к вещи с id = {}", userId, itemId);
		User author = userRepository.findById(userId).orElseThrow(() ->
				new NotFoundException("Пользователь с id = " + userId + " не найден"));
		Item item = itemRepository.findById(itemId).orElseThrow(() ->
				new NotFoundException("Вещь с id = " + itemId + " не найдена"));

		List<Booking> pastBookings = bookingRepository
				.findUserBookings(itemId, userId, Status.APPROVED, LocalDateTime.now());
		log.info("Прошлые бронирования для пользователя {}: {}", userId, pastBookings);

		if (pastBookings.isEmpty()) {
			throw new NotBookedException("Пользователь не бронировал эту вещь или бронирование не завершено");
		}

		Comment comment = CommentMapper.mapToComment(author, item, request);
		Comment savedComment = commentRepository.save(comment);
		log.info("Сохранен комментарий с id = : {}", savedComment.getId());

		return CommentMapper.mapToCommentResponse(savedComment);
	}
}