package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
	private final ItemRequestClient itemRequestClient;
	private final String id = "/{request-id}";
	private final String all = "/all";

	private final String headerUserId = "X-Sharer-User-Id";
	private final String pvRequestId = "request-id";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> create(@RequestHeader(headerUserId) Long userId,
	                                     @Valid @RequestBody CreateRequest request) {
		log.info("Создание запроса на вещь от пользователя с id = {}", userId);
		return itemRequestClient.create(userId, request);
	}

	@PutMapping
	public ResponseEntity<Object> update(@RequestHeader(headerUserId) Long userId,
	                                     @Valid @RequestBody UpdateRequest request) {
		log.info("Обновление запроса на вещь от пользователя с id = {}", userId);
		return itemRequestClient.update(userId, request);
	}

	@DeleteMapping(id)
	public ResponseEntity<Object> delete(@PathVariable(pvRequestId) Long requestId) {
		log.info("Удаление запроса на вещь от пользователя с id = {}", requestId);
		return itemRequestClient.delete(requestId);
	}

	@GetMapping(id)
	public ResponseEntity<Object> findItemRequest(@PathVariable(pvRequestId) Long requestId) {
		log.info("Поиск запроса на вещь от пользователя с id = {}", requestId);
		return itemRequestClient.findItemRequest(requestId);
	}

	@GetMapping
	public ResponseEntity<Object> findAllByRequestorId(@RequestHeader(headerUserId) Long requestorId) {
		log.info("Поиск запросов на вещей от пользователя с id = {}", requestorId);
		return itemRequestClient.findItemRequests(null, requestorId);
	}

	@GetMapping(all)
	public ResponseEntity<Object> findAllOfAnotherRequestors(@RequestHeader(headerUserId) Long requestorId) {
		log.info("Поиск запросов на вещей от других пользователей с id = {}", requestorId);
		return itemRequestClient.findItemRequests(all, requestorId);
	}
}