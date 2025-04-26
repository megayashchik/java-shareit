package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
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

	public static ItemResponse mapToItemDto(Item item) {
		ItemResponse itemResponse = new ItemResponse();
		itemResponse.setId(item.getId());
		itemResponse.setName(item.getName());
		itemResponse.setDescription(item.getDescription());
		itemResponse.setAvailable(item.getAvailable());
		itemResponse.setOwnerId(item.getOwner().getId());

		return itemResponse;
	}

	public static ItemResponse mapToItemDtoWithBookingsAndComments(Item item,
	                                                               BookingResponse lastBooking,
	                                                               BookingResponse nextBooking,
	                                                               List<CommentResponse> comments) {
		ItemResponse itemResponse = mapToItemDto(item);
		itemResponse.setLastBooking(lastBooking);
		itemResponse.setNextBooking(nextBooking);
		itemResponse.setComments(comments != null ? comments : List.of());

		return itemResponse;
	}
}