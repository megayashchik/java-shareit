package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {

	public static Item mapToItemDto(User owner, CreateItemRequest request) {
		Item item = new Item();
		item.setName(request.getName());
		item.setDescription(request.getDescription());
		item.setAvailable(request.getAvailable());
		item.setOwner(owner);

		return item;
	}

	public static ItemDto mapToItemDto(Item item) {
		ItemDto itemDto = new ItemDto();
		itemDto.setId(item.getId());
		itemDto.setName(item.getName());
		itemDto.setDescription(item.getDescription());
		itemDto.setAvailable(item.getAvailable());
		itemDto.setOwnerId(item.getOwner().getId());

		return itemDto;
	}

	public static ItemDto mapToItemDtoWithBookingsAndComments(Item item,
	                                                          BookingResponse lastBooking,
	                                                          BookingResponse nextBooking,
	                                                          List<CommentResponse> comments) {
		ItemDto itemDto = mapToItemDto(item);
		itemDto.setLastBooking(lastBooking);
		itemDto.setNextBooking(nextBooking);
		itemDto.setComments(comments != null ? comments : List.of());

		return itemDto;
	}
}