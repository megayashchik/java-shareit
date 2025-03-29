package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {
	private final Map<Long, Item> items = new HashMap<>();

	@Override
	public Item create(Item item) {
		item.setId(getNextId());
		items.put(item.getId(), item);

		return item;
	}

	@Override
	public Item update(Item newItem) {
		items.put(newItem.getId(), newItem);

		return newItem;
	}

	@Override
	public Item findById(Long itemId) {
		return Optional.ofNullable(items.get(itemId))
				.orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена"));
	}

	@Override
	public boolean deleteById(Long itemId) {
		items.remove(itemId);

		return Optional.ofNullable(items.get(itemId)).isPresent();
	}

	@Override
	public Collection<Item> findAllByOwner(Long ownerId) {
		return items.values().stream()
				.filter(item -> item.getOwnerId().equals(ownerId))
				.toList();
	}

	@Override
	public Collection<Item> findItemsByText(String text) {
		if (text == null || text.isBlank()) {
			return Collections.emptyList();
		}

		return filterItemsByText(text.toLowerCase());
	}

	private Collection<Item> filterItemsByText(String searchText) {
		return items.values().stream()
				.filter(item -> item.getAvailable() != null && item.getAvailable())
				.filter(item -> containsIgnoreCase(item.getName(), searchText.toLowerCase()) ||
									 containsIgnoreCase(item.getDescription(), searchText.toLowerCase()))
				.toList();
	}

	private boolean containsIgnoreCase(String text, String search) {
		return text != null && text.toLowerCase().contains(search);
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