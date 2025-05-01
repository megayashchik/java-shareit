package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ItemMapper {

	public static Item mapToItem(User owner, CreateItemRequest request) {
		Item item = new Item();
		item.setName(request.getName());
		item.setDescription(request.getDescription());
		item.setAvailable(request.getAvailable());
		item.setUser(owner);
		if (request.hasRequestId()) {
			item.setRequestId(request.getRequestId());
		}

		return item;
	}

	public static ItemDto mapToItemDto(Item item) {
		ItemDto itemDto = new ItemDto();
		itemDto.setId(item.getId());
		itemDto.setName(item.getName());
		itemDto.setDescription(item.getDescription());
		itemDto.setAvailable(item.getAvailable());
		itemDto.setOwnerId(item.getUser().getId());
		if (item.getRequestId() != null) {
			itemDto.setRequestId(item.getRequestId());
		}

		return itemDto;
	}

	public static Item updateItem(Item item, UpdateItemRequest request) {
		if (request.hasName()) {
			item.setName(request.getName());
		}

		if (request.hasDescription()) {
			item.setDescription(request.getDescription());
		}

		if (request.hasAvailable()) {
			item.setAvailable(request.getAvailable());
		}

		return item;
	}

	public static ItemDetailsDto mapToItemDetailsDto(Item item, List<Comment> comments) {
		ItemDetailsDto dto = new ItemDetailsDto();
		dto.setId(item.getId());
		dto.setName(item.getName());
		dto.setDescription(item.getDescription());
		dto.setAvailable(item.getAvailable());
		dto.setOwnerId(item.getUser().getId());
		dto.setComments(comments.stream().map(CommentMapper::mapToCommentDto).toList());
		if (item.getRequestId() != null) {
			dto.setRequestId(item.getRequestId());
		}

		return dto;
	}

	public static ItemDetailsDto mapToItemDetailsDto(Item item,
	                                                 List<Comment> comments,
	                                                 Optional<LocalDateTime> lastBooking,
	                                                 Optional<LocalDateTime> nextBooking) {
		ItemDetailsDto dto = new ItemDetailsDto();
		dto.setId(item.getId());
		dto.setName(item.getName());
		dto.setDescription(item.getDescription());
		dto.setAvailable(item.getAvailable());
		dto.setOwnerId(item.getUser().getId());
		lastBooking.ifPresent(dto::setLastBooking);
		nextBooking.ifPresent(dto::setNextBooking);
		dto.setComments(comments.stream().map(CommentMapper::mapToCommentDto).toList());
		if (item.getRequestId() != null) {
			dto.setRequestId(item.getRequestId());
		}

		return dto;
	}
}