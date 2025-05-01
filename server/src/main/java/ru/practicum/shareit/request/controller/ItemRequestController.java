package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
	private final ItemRequestService itemRequestService;
	private final String id = "/{request-id}";
	private final String all = "/all";

	private final String headerUserId = "X-Sharer-User-Id";
	private final String pvRequestId = "request-id";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemRequestDto create(@RequestHeader(headerUserId) Long userId,
	                             @RequestBody CreateRequest request) {
		log.info("Запрос на добавление новой вещи от пользователя с id = {}: {}", userId, request);
		return itemRequestService.create(userId, request);
	}

	@GetMapping(id)
	public ItemRequestDto findItemRequest(@PathVariable(pvRequestId) Long requestId) {
		return itemRequestService.findItemRequest(requestId);
	}

	@GetMapping
	public List<ItemRequestDto> findAllByRequestorId(@RequestHeader(headerUserId) Long requestorId) {
		return itemRequestService.findAllByRequestorId(requestorId);
	}

	@GetMapping(all)
	public List<ItemRequestDto> findAllOfAnotherRequestors(@RequestHeader(headerUserId) Long requestorId) {
		return itemRequestService.findAllOfAnotherRequestors(requestorId);
	}

	@PutMapping
	public ItemRequestDto update(@RequestHeader(headerUserId) Long userId,
	                             @RequestBody UpdateRequest request) {
		return itemRequestService.update(userId, request);
	}

	@DeleteMapping(id)
	public void delete(@PathVariable(pvRequestId) Long requestId) {
		itemRequestService.delete(requestId);
	}
}