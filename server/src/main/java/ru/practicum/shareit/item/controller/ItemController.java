package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
	private final ItemService itemService;
	private final String id = "/{item-id}";
	private final String search = "/search";
	private final String comment = "/comment";
	private final String itemComment = id + comment;

	private final String headerUserId = "X-Sharer-User-Id";
	private final String pvItemId = "item-id";


	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                      @Valid @RequestBody CreateItemRequest request) {
		log.info("Запрос на добавление новой вещи от пользователя с id = {}: {}", userId, request);
		ItemDto createdItem = itemService.create(userId, request);
		log.info("Создана новая вещь: {}", createdItem);

		return (createdItem);
	}

	@PatchMapping(id)
	@ResponseStatus(HttpStatus.OK)
	public ItemDto update(@PathVariable("itemId") Long itemId,
	                      @RequestHeader("X-Sharer-User-Id") Long ownerId,
	                      @Valid @RequestBody UpdateItemRequest request) {
		log.info("Запрос на обновление описания вещи c id = {} от пользователя {}: {}", itemId, ownerId, request);
		ItemDto updatedItem = itemService.update(itemId, request, ownerId);
		log.info("Обновлена вещь с id = {}: {}", itemId, updatedItem);

		return updatedItem;
	}

	@GetMapping(id)
	@ResponseStatus(HttpStatus.OK)
	public ItemDetailsDto findById(@PathVariable("itemId") Long itemId,
	                               @RequestHeader("X-Sharer-User-Id") Long userId) {
		log.info("Запрос на поиск вещи по id = : {} от пользователя с id = {}", itemId, userId);
		ItemDetailsDto foundItem = itemService.findItemById(userId, itemId);
		log.info("Найдена вещь с id = {}: {}", itemId, foundItem);
		return foundItem;
	}

	@DeleteMapping(id)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
	                       @PathVariable("itemId") Long itemId) {
		log.info("Запрос на удаление вещи с id = {} от владельца {}", itemId, ownerId);
		itemService.delete(ownerId, itemId);
		log.info("Удалена вещь с id = {}", itemId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ItemDetailsDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
		log.info("Запрос на все вещи от владельца с id = {}", ownerId);
		List<ItemDetailsDto> allItems = itemService.findAll(ownerId);
		log.info("Список вещей владельца с id = {}: {}", ownerId, allItems);

		return allItems;
	}

	@GetMapping(search)
	@ResponseStatus(HttpStatus.OK)
	public List<ItemDto> findItemsByText(@RequestHeader(headerUserId) Long ownerId,
	                                           @RequestParam(name = "text", defaultValue = "") String text) {
		log.info("Запрос на поиск вещей по тексту: {}", text);
		List<ItemDto> allItems = itemService.findItemsByBooker(ownerId, text);
		log.info("Список вещей: {}", allItems);

		return allItems;
	}

	@PostMapping(itemComment)
	@ResponseStatus(HttpStatus.CREATED)
	public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
	                             @PathVariable Long itemId,
	                             @Valid @RequestBody CreateCommentRequest request) {
		log.info("Запрос на добавление комментария к вещи с id = {} от пользователя с id = {}", itemId, userId);
		CommentDto comment = itemService.addComment(itemId, userId, request);
		log.info("Комментарий добавлен: {}", comment);

		return comment;
	}
}