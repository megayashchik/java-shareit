package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

	private static ResponseDto mapToResponseDto(Item item) {
		ResponseDto dto = new ResponseDto();
		dto.setId(item.getId());
		dto.setName(item.getName());
		dto.setOwnerId(item.getUser().getId());

		return dto;
	}

	public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
		ItemRequestDto dto = new ItemRequestDto();
		dto.setId(itemRequest.getId());
		dto.setDescription(itemRequest.getDescription());
		dto.setRequestorId(itemRequest.getRequestor().getId());
		dto.setCreated(itemRequest.getCreated());
		dto.setItems(Collections.emptyList());

		return dto;
	}

	public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<Item> items) {
		ItemRequestDto dto = new ItemRequestDto();
		dto.setId(itemRequest.getId());
		dto.setDescription(itemRequest.getDescription());
		dto.setRequestorId(itemRequest.getRequestor().getId());
		dto.setCreated(itemRequest.getCreated());
		dto.setItems(items.stream().map(ItemRequestMapper::mapToResponseDto).toList());

		return dto;
	}

	public static ItemRequest mapToItemRequest(CreateRequest request, User findUser, LocalDateTime now) {
		ItemRequest itemRequest = new ItemRequest();
		itemRequest.setDescription(request.getDescription());
		itemRequest.setRequestor(findUser);
		itemRequest.setCreated(now);

		return itemRequest;
	}

	public static ItemRequest updateItemFields(ItemRequest itemRequest, UpdateRequest request, User findUser) {
		itemRequest.setDescription(request.getDescription());

		return itemRequest;
	}
}