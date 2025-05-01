package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
	ItemResponse create(Long userId, CreateItemRequest request);

	ItemResponse update(Long itemId, Long ownerId, UpdateItemRequest request);

	ItemResponse findById(Long itemId, Long userId);

	void delete(Long ownerId, Long itemId);

	List<ItemResponse> findByOwnerId(Long ownerId);

	List<ItemResponse> findItemsByText(String text);

	CommentResponse addComment(Long userId, Long itemId, CreateCommentRequest request);
}