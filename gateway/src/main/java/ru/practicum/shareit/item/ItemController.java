package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@Controller
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
	private final ItemClient itemClient;
	private final String id = "/{item-id}";
	private final String search = "/search";
	private final String comment = "/comment";
	private final String itemComment = id + comment;

	private final String headerUserId = "X-Sharer-User-Id";
	private final String pvItemId = "item-id";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> create(@RequestHeader(headerUserId) Long ownerId,
	                                     @Valid @RequestBody CreateItemRequest request) {
		log.info("Создание вещи от пользователя с id = {}: {}", ownerId, request);
		return itemClient.create(ownerId, request);
	}

	@PatchMapping(id)
	public ResponseEntity<Object> updateItem(@PathVariable(pvItemId) Long itemId,
	                                         @Valid @RequestBody UpdateItemRequest request,
	                                         @RequestHeader(headerUserId) Long ownerId) {
		log.info("Обновление вещи от пользователя с id = {} на вещь с id = {}: {}", ownerId, itemId, request);
		return itemClient.update(itemId, ownerId, request);
	}


	@DeleteMapping(id)
	public ResponseEntity<Object> delete(@RequestHeader(headerUserId) Long ownerId,
	                                     @PathVariable(pvItemId) Long itemId) {
		log.info("Удаление вещи с id = {} от пользователя  с id = {}", itemId, ownerId);
		return itemClient.delete(itemId, ownerId);
	}

	@GetMapping(id)
	public ResponseEntity<Object> findItem(@RequestHeader(headerUserId) Long ownerId,
	                                       @PathVariable(pvItemId) Long itemId) {
		log.info("Поиск вещи с id = {} от пользователя  с id = {}", itemId, ownerId);
		return itemClient.findItem(ownerId, itemId);
	}

	@GetMapping(search)
	public ResponseEntity<Object> findItemsForBooker(@RequestHeader(headerUserId) Long ownerId,
	                                                 @RequestParam(name = "text", defaultValue = "") String text) {
		log.info("Поиск вещи по тексту: {} от пользователя  с id = {}", text, ownerId);
		return itemClient.findItems(search, ownerId, text);
	}

	@GetMapping
	public ResponseEntity<Object> findAll(@RequestHeader(headerUserId) Long ownerId) {
		log.info("Поиск вещей от пользователя  с id = {}", ownerId);
		return itemClient.findItems(null, ownerId, null);
	}

	@PostMapping(itemComment)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> addComment(@PathVariable(pvItemId) Long itemId,
	                                         @RequestHeader(headerUserId) Long userId,
	                                         @Valid @RequestBody CreateCommentRequest request) {
		log.info("Добавление коммента к вещи с id = {}, от пользователя с id = {}", itemId, userId);
		return itemClient.addComment("/" + itemId + this.comment, userId, request);
	}
}