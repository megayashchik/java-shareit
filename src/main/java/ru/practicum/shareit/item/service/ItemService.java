package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
	ItemDto create(Long userId, CreateItemRequest request);

	ItemDto update(Long itemId, Long ownerId, UpdateItemRequest request);

	ItemDto findById(Long itemId);

	void delete(Long ownerId, Long itemId);

	List<ItemDto> findByOwnerId(Long ownerId);

	List<ItemDto> findItemsByText(String text);

	CommentResponse addComment(Long userId, Long itemId, CreateCommentRequest request);
}