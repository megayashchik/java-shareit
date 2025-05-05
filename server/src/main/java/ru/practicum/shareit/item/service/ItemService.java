package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
	ItemDto create(Long ownerId, CreateItemRequest request);

	ItemDto update(Long itemId, UpdateItemRequest request, Long ownerId);

	void delete(Long ownerId, Long itemId);

	ItemDetailsDto findItemById(Long ownerId, Long itemI);

	List<ItemDto> findItemsByBooker(Long ownerId, String text);

	List<ItemDetailsDto> findAll(Long ownerId);

	CommentDto addComment(Long itemId, Long userId, CreateCommentRequest request);
}