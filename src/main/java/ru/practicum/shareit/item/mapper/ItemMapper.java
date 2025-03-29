package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

	public static Item mapToItem(Long ownerId, NewItemRequest request) {
		Item item = new Item();
		item.setName(request.getName());
		item.setDescription(request.getDescription());
		item.setAvailable(request.getAvailable());
		item.setOwnerId(ownerId);
		item.setRequestId(request.getRequestId());

		return item;
	}

	public static ItemDto mapToItemDto(Item item) {
		ItemDto itemDto = new ItemDto();
		itemDto.setId(item.getId());
		itemDto.setName(item.getName());
		itemDto.setDescription(item.getDescription());
		itemDto.setAvailable(item.getAvailable());
		itemDto.setOwnerId(item.getOwnerId());
		itemDto.setRequestId(item.getRequestId());

		return itemDto;
	}

	public static Item updateItemFields(Item item, UpdateItemRequest request) {
		item.setName(request.getName());
		item.setDescription(request.getDescription());
		item.setAvailable(request.getAvailable());

		return item;
	}
}