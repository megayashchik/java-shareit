package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

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

	public static Item updateItemFields(Item item, UpdateItemRequest request) {
		item.setName(request.getName());
		item.setDescription(request.getDescription());
		item.setAvailable(request.getAvailable());

		return item;
	}

	public static ItemDto mapToItemDtoWithBookingAndComments(Item item,
	                                                         Map<Long, List<CommentResponse>> comments,
	                                                         Map<Long, BookingResponse> lastBookings,
	                                                         Map<Long, BookingResponse> nextBookings) {
		ItemDto itemDto = mapToItemDto(item);
		itemDto.setComments(comments.getOrDefault((item.getId()), List.of()));
		itemDto.setLastBooking(lastBookings.get(item.getId()));
		itemDto.setNextBooking(nextBookings.get(item.getId()));

		return itemDto;
	}

	public static ItemDto mapToItemDtoWithBookings(Item item,
	                                               BookingResponse last,
	                                               BookingResponse next) {
		ItemDto itemDto = mapToItemDto(item);
		itemDto.setLastBooking(last);
		itemDto.setNextBooking(next);

		return itemDto;
	}
}