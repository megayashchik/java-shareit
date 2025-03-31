package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;

	@Override
	public ItemDto create(Long userId, NewItemRequest request) {
		log.info("Создание вещи для пользователя с id={}, запрос: {}", userId, request);
		userRepository.findById(userId);
		Item item = ItemMapper.mapToItem(userId, request);
		Item createdItem = itemRepository.create(item);
		log.info("Создана вещь: {}", createdItem);

		return ItemMapper.mapToItemDto(createdItem);
	}

	@Override
	public ItemDto update(Long itemId, Long userId, UpdateItemRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Запрос на обновление не может быть null");
		}

		Item existingItem = itemRepository.findById(itemId);
		if (!existingItem.getOwnerId().equals(userId)) {
			throw new NotFoundException("Предмет не принадлежит пользователю");
		}

		if (request.getName() != null && request.getName().isBlank()) {
			throw new IllegalArgumentException("Название не может быть пустым");
		}

		if (request.getDescription() != null && request.getDescription().isBlank()) {
			throw new IllegalArgumentException("Описание не может быть пустым");
		}

		Item updatedItem = ItemMapper.updateItemFields(existingItem, request);
		Item savedItem = itemRepository.update(updatedItem);

		return ItemMapper.mapToItemDto(savedItem);
	}

	@Override
	public ItemDto findById(Long itemId) {
		log.info("Поиск вещи с id={}", itemId);
		Item item = itemRepository.findById(itemId);
		log.info("Найдена вещь с id={}: {}", itemId, item);

		return ItemMapper.mapToItemDto(item);
	}

	@Override
	public void delete(Long ownerId, Long itemId) {
		log.info("Удаление вещи с id={} от владельца {}", itemId, ownerId);
		Item item = itemRepository.findById(itemId);

		if (!item.getOwnerId().equals(ownerId)) {
			throw new NotOwnerException("Только владелец вещи может её удалить");
		}

		itemRepository.deleteById(itemId);
		log.info("Удалена вещь c id={}: {}", itemId, item);
	}

	@Override
	public Collection<ItemDto> findAllByOwner(Long ownerId) {
		log.info("Получение списка вещей владельца с id= {}", ownerId);
		Collection<ItemDto> items = itemRepository.findAllByOwner(ownerId).stream()
				.map(ItemMapper::mapToItemDto)
				.toList();
		log.info("Найдено {} вещей владельца с id={}", items.size(), ownerId);

		return items;
	}

	@Override
	public Collection<ItemDto> findItemsByText(String text) {
		log.info("Получение списка вещей по тексту:{}", text);
		Collection<ItemDto> items = itemRepository.findItemsByText(text).stream()
				.map(ItemMapper::mapToItemDto)
				.toList();
		log.info("Найдено {} вещей по тексту: {}", items.size(), text);

		return items;
	}
}