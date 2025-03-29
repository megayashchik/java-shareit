package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;

	@Override
	public ItemDto create(Long userId, NewItemRequest request) {
		userRepository.findById(userId);
		Item item = ItemMapper.mapToItem(userId, request);
		Item createdItem = itemRepository.create(item);

		return ItemMapper.mapToItemDto(createdItem);
	}

	@Override
	public ItemDto update(Long ownerId, Long itemId, UpdateItemRequest request) {
		Item existingItem = itemRepository.findById(itemId);

		if (!existingItem.getOwnerId().equals(ownerId)) {
			throw new NotOwnerException("Только владелец вещи может её редактировать");
		}

		Item updatedItem = itemRepository.update(ItemMapper.updateItemFields(existingItem, request));

		return ItemMapper.mapToItemDto(updatedItem);
	}

	@Override
	public ItemDto findById(Long itemId) {
		return ItemMapper.mapToItemDto(itemRepository.findById(itemId));
	}

	@Override
	public boolean delete(Long ownerId, Long itemId) {
		Item item = itemRepository.findById(itemId);

		if (!item.getOwnerId().equals(ownerId)) {
			throw new NotOwnerException("Только владелец вещи может её удалить");
		}

		return itemRepository.deleteById(itemId);
	}

	@Override
	public Collection<ItemDto> findAllByOwner(Long ownerId) {
		return itemRepository.findAllByOwner(ownerId).stream()
				.map(ItemMapper::mapToItemDto)
				.toList();
	}

	@Override
	public Collection<ItemDto> findItemsByText(String text) {
		return itemRepository.findItemsByText(text).stream()
				.map(ItemMapper::mapToItemDto)
				.toList();
	}
}