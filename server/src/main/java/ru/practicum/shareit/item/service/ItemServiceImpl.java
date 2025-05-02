package ru.practicum.shareit.item.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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
	public ItemDto create(Long ownerId, CreateItemRequest request) {
		log.info("Создание вещи для пользователя с id = {}, запрос: {}", ownerId, request);

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

		User foundUser = findUserById(ownerId);
		Item item = ItemMapper.mapToItem(foundUser, request);
		Item createdItem = itemRepository.save(item);
		log.info("Создана вещь: {}", createdItem);

		return ItemMapper.mapToItemDto(createdItem);
	}

	@Override
	@Transactional
	public ItemDto update(Long itemId, UpdateItemRequest request, Long ownerId) {
		log.info("Обновление вещи для пользователя с id = {}, запрос: {}", ownerId, request);
		Item item = findItemById(itemId);

		User foundUser = findUserById(ownerId);
		if (!item.getUser().getId().equals(ownerId)) {
			throw new NotOwnerException("Редактировать данные вещи может только её владелец");
		}

		if (request == null) {
			throw new IllegalArgumentException("Запрос на обновление не может быть null");
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
		User foundUser = findUserById(ownerId);
		Item item = findItemById(itemId);

		if (!foundUser.getId().equals(ownerId)) {
			throw new NotOwnerException("Только владелец вещи может её удалить");
		}

		itemRepository.deleteById(itemId);
		log.info("Удалена вещь c id = {}: {}", itemId, item);
	}

	@Override
	@Transactional(readOnly = true)
	public ItemDetailsDto findItemById(Long ownerId, Long itemId) {
		log.info("Поиск вещи по id = {}", itemId);
		Item item = findItemById(itemId);
		LocalDateTime now = LocalDateTime.now();

		if (item.getUser().getId().equals(ownerId)) {
			return ItemMapper.mapToItemDetailsDto(findById(itemId),
					commentRepository.findAllByItemId(itemId),
					getLastBookingEndDate(itemId, now),
					getNextBookingStartDate(itemId, now));
		}
		log.info("Найдена вещь с id = {}", itemId);

		return ItemMapper.mapToItemDetailsDto(findById(itemId), commentRepository.findAllByItemId(itemId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemDetailsDto> findAll(Long ownerId) {
		log.info("Получение списка вещей владельца с id = {}", ownerId);
		findUserById(ownerId);
		List<Item> userItems = itemRepository.findAllByUserId(ownerId);

		if (!userItems.isEmpty()) {
			List<ItemDetailsDto> result = fillItemData(userItems);
			log.info("Найдено {} вещей владельца с id = {}", result.size(), ownerId);

			return result;
		}

		log.info("Вещи для владельца с id = {} не найдены", ownerId);

		return List.of();
	}

	@Override
	public List<ItemDto> findItemsByBooker(Long ownerId, String text) {
		log.info("Получение списка вещей по тексту: {}", text);
		if (StringUtils.isBlank(text)) {
			return new ArrayList<>();
		}
		List<Item> items = itemRepository.findItemsByText(text);
		log.info("Найдено {} вещей по тексту: {}", items.size(), text);

		return items.stream()
				.map(ItemMapper::mapToItemDto)
				.toList();
	}

	@Override
	@Transactional
	public CommentDto addComment(Long itemId, Long userId, CreateCommentRequest request) {
		log.info("Добавление комментария от пользователя с id = {} к вещи с id = {}", userId, itemId);
		User author = findUserById(userId);
		Item item = findItemById(itemId);

		LocalDateTime current = LocalDateTime.now();
		if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, current)) {
			throw new NotBookedException(String.format("Пользователь %s не может оставить комментарий, " +
					"так как не пользовался вещью %s", author.getName(), item.getName()));
		}

		Comment comment = CommentMapper.mapToComment(author, item, request);
		Comment savedComment = commentRepository.save(comment);
		log.info("Сохранен комментарий с id = {}, текст: {}", savedComment.getId(), comment);

		return CommentMapper.mapToCommentDto(savedComment);
	}

	private Item findById(Long itemId) {
		return itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException(String.format("Вещь c ID %d не найдена", itemId)));
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
	}

	private Item findItemById(Long itemId) {
		return itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
	}

	private Optional<LocalDateTime> getLastBookingEndDate(Long itemId, LocalDateTime now) {
		return bookingRepository.findLastBookingEndByItemId(itemId, Status.APPROVED, now)
				.stream()
				.max(Comparator.naturalOrder());
	}

	private Optional<LocalDateTime> getNextBookingStartDate(Long itemId, LocalDateTime now) {
		return bookingRepository.findNextBookingStartByItemId(itemId, Status.APPROVED, now)
				.stream()
				.min(Comparator.naturalOrder());
	}


	private List<ItemDetailsDto> fillItemData(List<Item> userItems) {
		LocalDateTime now = LocalDateTime.now();
		List<Long> itemIds = userItems.stream().map(Item::getId).toList();

		Map<Item, LocalDateTime> lastItemBookingEndDate = bookingRepository
				.findByItemInAndEndBefore(itemIds, Status.APPROVED, now)
				.stream()
				.collect(Collectors.toMap(Booking::getItem, Booking::getEnd));

		Map<Item, LocalDateTime> nextItemBookingStartDate = bookingRepository
				.findByItemInAndStartAfter(itemIds, Status.APPROVED, now)
				.stream()
				.collect(Collectors.toMap(Booking::getItem, Booking::getStart));

		Map<Item, List<Comment>> itemsWithComments = commentRepository
				.findByItemIn(itemIds)
				.stream()
				.collect(groupingBy(Comment::getItem, toList()));

		List<ItemDetailsDto> itemsList = new ArrayList<>();
		for (Item item : userItems) {
			Optional<LocalDateTime> lastEndDate;
			if (!lastItemBookingEndDate.isEmpty()) {
				lastEndDate = Optional.of(lastItemBookingEndDate.get(item));
			} else {
				lastEndDate = Optional.empty();
			}

			Optional<LocalDateTime> nextStartDate;
			if (!nextItemBookingStartDate.isEmpty()) {
				nextStartDate = Optional.of(nextItemBookingStartDate.get(item));
			} else {
				nextStartDate = Optional.empty();
			}

			itemsList.add(ItemMapper.mapToItemDetailsDto(item,
							itemsWithComments.getOrDefault(item, Collections.emptyList()),
							lastEndDate,
							nextStartDate
					)
			);
		}

		return itemsList;
	}
}