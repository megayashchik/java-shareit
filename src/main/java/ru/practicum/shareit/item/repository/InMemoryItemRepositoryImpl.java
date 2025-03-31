package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {
	private final Map<Long, Item> items = new HashMap<>();

	@Override
	public Item create(Item item) {
		log.info("Создание новой вещи: {}", item);
		item.setId(getNextId());
		items.put(item.getId(), item);
		log.info("Создана вещь с id={}: {}", item.getId(), item);

		return item;
	}

	@Override
	public Item update(Item newItem) {
		log.info("Обновление вещи {} с id={}", newItem, newItem.getId());
		if (!items.containsKey(newItem.getId())) {
			log.warn("попытка обновить не существующую вещь с id={}", newItem.getId());
			throw new NotFoundException("Вещь с id=" + newItem.getId() + " не найдена");
		}
		items.put(newItem.getId(), newItem);
		log.info("Обновлена вещь с id={}: {}", newItem.getId(), newItem);

		return newItem;
	}

	@Override
	public Item findById(Long itemId) {
		log.info("Получение вещи с id={}", itemId);
		Item item = Optional.ofNullable(items.get(itemId))
				.orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена"));
		log.info("Найдена вещь {} с id={}", item, itemId);

		return item;
	}

	@Override
	public void deleteById(Long itemId) {
		log.info("Удаление вещи с id={}", itemId);
		Item deletedItem = items.remove(itemId);
		if (deletedItem == null) {
			throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
		}
		log.info("Удалена вещь с id={}", itemId);
	}

	@Override
	public Collection<Item> findAllByOwner(Long ownerId) {
		log.info("Получение списка вещей владельца с id={}", ownerId);
		Collection<Item> ownerItems = items.values().stream()
				.filter(item -> item.getOwnerId().equals(ownerId))
				.toList();
		log.info("Найдено {} вещей владельца с id={}", ownerItems.size(), ownerId);

		return ownerItems;
	}

	@Override
	public Collection<Item> findItemsByText(String text) {
		log.info("Поиск вещей по тесту: {}", text);
		if (text == null || text.isBlank()) {
			log.info("Текст поиска пустой, возвращен пустой список");
			return Collections.emptyList();
		}

		Collection<Item> foundItems = filterItemsByText(text.toLowerCase());
		log.info("Найдено {} вещей по тексту: {}", foundItems.size(), text);

		return foundItems;
	}

	private Collection<Item> filterItemsByText(String searchText) {
		return items.values().stream()
				.filter(item -> item.getAvailable() != null && item.getAvailable())
				.filter(item -> containsIgnoreCase(item.getName(), searchText) ||
						containsIgnoreCase(item.getDescription(), searchText))
				.toList();
	}

	private boolean containsIgnoreCase(String text, String search) {
		if (text == null || search == null) {
			return false;
		}
		return text.toLowerCase().contains(search.toLowerCase());
	}

	private long getNextId() {
		long currentMaxId = items.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);

		return ++currentMaxId;
	}
}