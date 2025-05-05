package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateRequest;

import java.util.List;

public interface ItemRequestService {
	ItemRequestDto create(Long userId, CreateRequest request);

	void delete(Long itemRequestId);

	ItemRequestDto update(Long user, UpdateRequest request);

	ItemRequestDto findItemRequest(Long itemRequestId);

	List<ItemRequestDto> findAllByRequestorId(Long requestorId);

	List<ItemRequestDto> findAllOfAnotherRequestors(Long requestorId);
}