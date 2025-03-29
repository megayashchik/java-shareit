package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
	private final ItemService itemService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                      @Valid @RequestBody NewItemRequest newItemRequest) {
		return itemService.create(userId, newItemRequest);
	}

	@PatchMapping("/{itemId}")
	@ResponseStatus(HttpStatus.OK)
	public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
	                      @PathVariable("itemId") Long itemId,
	                      @Valid @RequestBody UpdateItemRequest updateItemRequest) {
		return itemService.update(ownerId, itemId, updateItemRequest);
	}

	@GetMapping("/{itemId}")
	@ResponseStatus(HttpStatus.OK)
	public ItemDto findById(@PathVariable("itemId") Long itemId) {
		return itemService.findById(itemId);
	}

	@DeleteMapping("/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public boolean deleteById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
	                          @PathVariable("itemId") Long itemId) {
		return itemService.delete(ownerId, itemId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Collection<ItemDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
		return itemService.findAllByOwner(ownerId);
	}

	@GetMapping("/search")
	@ResponseStatus(HttpStatus.OK)
	public Collection<ItemDto> findItemsByText(@RequestParam(name = "text", defaultValue = "") String text) {
		return itemService.findItemsByText(text);
	}
}