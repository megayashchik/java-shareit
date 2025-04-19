package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public ItemDto create(Long userId, NewItemRequest request) {
		log.info("Создание вещи для пользователя с id={}, запрос: {}", userId, request);

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
		Item item = ItemMapper.mapToItem(foundUser, request);
		Item createdItem = itemRepository.save(item);
		log.info("Создана вещь: {}", createdItem);

		return ItemMapper.mapToItemDto(createdItem);
	}

	@Override
	@Transactional
	public ItemDto update(Long itemId, Long ownerId, UpdateItemRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Запрос на обновление не может быть null");
		}

		Item existingItem = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь не найдена"));

		if (!existingItem.getOwner().getId().equals(ownerId)) {
			throw new NotOwnerException("Предмет не принадлежит пользователю");
		}

		if (request.getName() != null && request.getName().isBlank()) {
			throw new IllegalArgumentException("Название не может быть пустым");
		}

		if (request.getDescription() != null && request.getDescription().isBlank()) {
			throw new IllegalArgumentException("Описание не может быть пустым");
		}

		Item updatedItem = ItemMapper.updateItemFields(existingItem, request);
		Item savedItem = itemRepository.save(updatedItem);

		return ItemMapper.mapToItemDto(savedItem);
	}

	@Override
	@Transactional
	public void delete(Long ownerId, Long itemId) {
		log.info("Удаление вещи с id={} от владельца {}", itemId, ownerId);
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь не найдена"));

		if (!item.getOwner().getId().equals(ownerId)) {
			throw new NotOwnerException("Только владелец вещи может её удалить");
		}

		itemRepository.deleteById(itemId);
		log.info("Удалена вещь c id={}: {}", itemId, item);
	}

	@Override
	public ItemDto findById(Long itemId) {
		log.info("Поиск вещи с id={}", itemId);
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь не найдена"));
		log.info("Найдена вещь с id={}: {}", itemId, item);

		return ItemMapper.mapToItemDto(item);
	}

	@Override
	public List<ItemDto> findAllByOwner(Long ownerId) {
		log.info("Получение списка вещей владельца с id= {}", ownerId);
		List<ItemDto> items = itemRepository.findAllByOwnerId(ownerId).stream()
				.map(ItemMapper::mapToItemDto)
				.toList();
		log.info("Найдено {} вещей владельца с id={}", items.size(), ownerId);

		return items;
	}

	@Override
	public List<ItemDto> findItemsByText(String text) {
		log.info("Получение списка вещей по тексту:{}", text);
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
}