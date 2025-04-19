package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

	public static Item mapToItem(User owner, NewItemRequest request) {
		Item item = new Item();
		item.setName(request.getName());
		item.setDescription(request.getDescription());
		item.setAvailable(request.getAvailable());
		item.setOwner(owner);
		item.setRequestId(request.getRequestId());

		return item;
	}

	public static ItemDto mapToItemDto(Item item) {
		ItemDto itemDto = new ItemDto();
		itemDto.setId(item.getId());
		itemDto.setName(item.getName());
		itemDto.setDescription(item.getDescription());
		itemDto.setAvailable(item.getAvailable());
		itemDto.setOwnerId(item.getOwner().getId());
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