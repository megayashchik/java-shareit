package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {
	ItemDto create(Long userId, NewItemRequest request);

	ItemDto update(Long ownerId, Long itemId, UpdateItemRequest request);

	ItemDto findById(Long itemId);

	void delete(Long ownerId, Long itemId);

	Collection<ItemDto> findAllByOwner(Long ownerId);

	Collection<ItemDto> findItemsByText(String text);
}