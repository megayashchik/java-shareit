package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
	private final ItemService itemService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                      @Valid @RequestBody NewItemRequest request) {
		log.info("Запрос на добавление новой вещи от пользователя {}: {}", userId, request);
		ItemDto createdItem = itemService.create(userId, request);
		log.info("Создана новая вещь: {}", createdItem);

		return (createdItem);
	}

	@PatchMapping("/{itemId}")
	@ResponseStatus(HttpStatus.OK)
	public ItemDto update(@PathVariable("itemId") Long itemId,
	                      @RequestHeader("X-Sharer-User-Id") Long ownerId,
	                      @Valid @RequestBody UpdateItemRequest request) {
		log.info("Запрос на обновление описания вещи c id={} от пользователя {}: {}", itemId, ownerId, request);
		ItemDto updatedItem = itemService.update(itemId, ownerId, request);
		log.info("Обновлена вещь с id={}: {}", itemId, updatedItem);

		return updatedItem;
	}

	@GetMapping("/{itemId}")
	@ResponseStatus(HttpStatus.OK)
	public ItemDto findById(@PathVariable("itemId") Long itemId) {
		log.info("Запрос на поиск вещи по id: {}", itemId);
		ItemDto foundItem = itemService.findById(itemId);
		log.info("Найдена вещь с id={}: {}", itemId, foundItem);

		return foundItem;
	}

	@DeleteMapping("/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
	                       @PathVariable("itemId") Long itemId) {
		log.info("Запрос на удаление вещи с id={} от владельца {}", itemId, ownerId);
		itemService.delete(ownerId, itemId);
		log.info("Удалена вещь с id={}", itemId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Collection<ItemDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
		log.info("Запрос на все вещи от владельца с id={}", ownerId);
		Collection<ItemDto> allItems = itemService.findAllByOwner(ownerId);
		log.info("Список вещей владельца с id={}: {}", ownerId, allItems);

		return allItems;
	}

	@GetMapping("/search")
	@ResponseStatus(HttpStatus.OK)
	public Collection<ItemDto> findItemsByText(@RequestParam(name = "text", defaultValue = "") String text) {
		log.info("Запрос на поиск вещей по тексту: {}", text);
		Collection<ItemDto> allItems = itemService.findItemsByText(text);
		log.info("Список вещей: {}", allItems);

		return allItems;
	}
}