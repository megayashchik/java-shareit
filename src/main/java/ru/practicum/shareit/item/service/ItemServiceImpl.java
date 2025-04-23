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
import java.util.stream.Collectors;

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
	public ItemDto create(Long userId, CreateItemRequest request) {
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

		User foundUser = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь не найден"));
		Item item = ItemMapper.mapToItemDto(foundUser, request);
		Item createdItem = itemRepository.save(item);
		log.info("Создана вещь: {}", createdItem);

		return ItemMapper.mapToItemDto(createdItem);
	}

	@Override
	@Transactional
	public ItemDto update(Long itemId, Long ownerId, UpdateItemRequest request) {
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

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
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

		if (!item.getOwner().getId().equals(ownerId)) {
			throw new NotOwnerException("Только владелец вещи может её удалить");
		}

		itemRepository.deleteById(itemId);
		log.info("Удалена вещь c id={}: {}", itemId, item);
	}

	@Override
	@Transactional
	public ItemDto findById(Long itemId) {
		log.info("Поиск вещи с id = {}", itemId);
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
		LocalDateTime now = LocalDateTime.now();
		List<Long> itemIds = List.of(itemId);

		List<Booking> lastList = bookingRepository.findLastBookingByItemIds(itemIds, now, Status.APPROVED);
		List<Booking> nextList = bookingRepository.findNextBookingByItemIds(itemIds, now, Status.APPROVED);

		BookingResponse lastDto = lastList.stream()
				.findFirst()
				.map(BookingMapper::mapToBookingDto)
				.orElse(null);

		BookingResponse nextDto = nextList.stream()
				.findFirst()
				.map(BookingMapper::mapToBookingDto)
				.orElse(null);

		List<CommentResponse> comments = commentRepository
				         .findAllByItemIds(List.of(itemId))
				         .stream()
				         .map(CommentMapper::mapToCommentResponse)
				         .toList();

		ItemDto itemDto = ItemMapper.mapToItemDto(item);
		itemDto.setLastBooking(lastDto);
		itemDto.setNextBooking(nextDto);
		itemDto.setComments(comments);
		log.info("Найдена вещь с id = {}: {}", itemId, item);

		return itemDto;
	}

		@Override
		@Transactional(readOnly = true)
		public List<ItemDto> findByOwnerId (Long ownerId){
			log.info("Получение списка вещей владельца с id = {}", ownerId);
			userRepository.findById(ownerId)
					.orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
			List<Item> items = itemRepository.findAllByOwnerId(ownerId);
			List<Long> itemIds = items.stream()
					.map(Item::getId)
					.toList();
			LocalDateTime now = LocalDateTime.now();
			List<Booking> allLastBookings = bookingRepository.findLastBookingByItemIds(itemIds, now, Status.APPROVED);
			List<Booking> allNextBookings = bookingRepository.findNextBookingByItemIds(itemIds, now, Status.APPROVED);

			Map<Long, List<CommentResponse>> comments = commentRepository.findAllByItemIds(itemIds).stream()
					.collect(Collectors.groupingBy(c -> c.getItem().getId(),
							Collectors.mapping(CommentMapper::mapToCommentResponse, Collectors.toList())
					));


			Map<Long, BookingResponse> lastBookings = new HashMap<>();
			for (Booking booking : allLastBookings) {
				lastBookings.putIfAbsent(booking.getItem().getId(), BookingMapper.mapToBookingDto(booking));
			}

			Map<Long, BookingResponse> nextBookings = new HashMap<>();
			for (Booking booking : allNextBookings) {
				nextBookings.putIfAbsent(booking.getItem().getId(), BookingMapper.mapToBookingDto(booking));
			}

			List<ItemDto> result = new ArrayList<>();
			for (Item item : items) {
				ItemDto itemDto = ItemMapper.mapToItemDtoWithBookingAndComments(item, comments, lastBookings, nextBookings);
				result.add(itemDto);
			}

			log.info("Найдено {} вещей владельца с id = {}", items.size(), ownerId);

			return result;
		}

		@Override
		public List<ItemDto> findItemsByText (String text){
			log.info("Получение списка вещей по тексту: {}", text);
			if (text == null || text.isBlank()) {
				return List.of();
			} else {
				List<ItemDto> items = itemRepository.findItemsByText(text).stream()
						.map(ItemMapper::mapToItemDto)
						.toList();
				log.info("Найдено {} вещей по тексту: {}", items.size(), text);

				return items;
			}
		}

		@Override
		@Transactional
		public CommentResponse addComment (Long userId, Long itemId, CreateCommentRequest request){
			log.info("Добавление комментария от пользователя с id = {} к вещи с id = {}", userId, itemId);
			User author = userRepository.findById(userId)
					.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
			Item item = itemRepository.findById(itemId)
					.orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
			List<Booking> pastBookings = bookingRepository.findPastByBooker(userId, LocalDateTime.now());
			boolean hasBooked = pastBookings.stream()
					.anyMatch(booking -> booking.getItem().getId().equals(itemId) && booking.getStatus() == Status.APPROVED);

			if (!hasBooked) {
				throw new NotBookedException("Пользователь не брал эту вещь в аренду или аренда не завершена");
			}

			Comment comment = CommentMapper.mapToComment(author, item, request);
			Comment savedComment = commentRepository.save(comment);
			log.info("Комментарий успешно добавлен: {}", savedComment);

			return CommentMapper.mapToCommentResponse(savedComment);
		}
	}