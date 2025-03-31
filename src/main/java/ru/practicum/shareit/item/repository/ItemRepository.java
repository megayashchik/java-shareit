package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
	Item create(Item item);

	Item update(Item newItem);

	Item findById(Long itemId);

	void deleteById(Long itemId);

	Collection<Item> findAllByOwner(Long ownerId);

	Collection<Item> findItemsByText(String text);
}