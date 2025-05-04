package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
	private final RequestRepository repository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	@Transactional
	public ItemRequestDto create(Long userId, CreateRequest request) {
		log.info("Создание запроса на вещь от пользователя с id = {}, запрос: {}", userId, request);

		User findUser = findUserById(userId);

		ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(request, findUser, LocalDateTime.now());
		itemRequest = repository.save(itemRequest);
		log.info("Создан запрос на вещь: {}", itemRequest);

		return ItemRequestMapper.mapToItemRequestDto(itemRequest);
	}

	@Override
	@Transactional
	public ItemRequestDto update(Long userId, UpdateRequest request) {
		log.info("Обновление запроса на вещь от пользователя с id = {}, запрос: {}", userId, request);

		if (request.getId() == null) {
			throw new ValidationException("ID запроса должен быть указан");
		}

		User findUser = findUserById(userId);
		ItemRequest existingRequest = findRequestById(request.getId());

		if (!existingRequest.getRequestor().getId().equals(userId)) {
			throw new NotOwnerException("Пользователь с id = " + userId +
					" не является автором запроса с id = " + request.getId());
		}

		ItemRequest updatedItem =
				ItemRequestMapper.updateItemFields(existingRequest, request, findUser);
		updatedItem = repository.save(updatedItem);
		log.info("Обновлён запрос на вещь от пользователя c id = {}", userId);

		return ItemRequestMapper.mapToItemRequestDto(updatedItem);
	}

	@Override
	@Transactional
	public void delete(Long itemRequestId) {
		log.info("Удаление запроса вещи с id = {}", itemRequestId);
		ItemRequest itemRequest = findRequestById(itemRequestId);
		log.info("Удалён запрос на вещь c id = {}", itemRequestId);

		repository.delete(itemRequest);
	}

	@Override
	@Transactional(readOnly = true)
	public ItemRequestDto findItemRequest(Long itemRequestId) {
		log.info("Поиск запроса вещи по id = {}", itemRequestId);
		ItemRequest itemRequest = findRequestById(itemRequestId);

		List<Item> items = itemRepository.findByRequestId(itemRequestId);
		log.info("Найден запрос на вещь по id = {}", itemRequestId);

		return ItemRequestMapper.mapToItemRequestDto(itemRequest, items);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequestDto> findAllByRequestorId(Long requestorId) {
		log.info("Поиск запросов на вещи от пользователя с id = {}", requestorId);

		User findUser = findUserById(requestorId);

		List<ItemRequest> requests = repository.findByRequestorId(requestorId);
		log.info("Найдено запросов {} на вещи от пользователя с id = {}", requests.size(), requestorId);

		return fillRequestsData(requests)
				.stream()
				.sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
				.collect(toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequestDto> findAllOfAnotherRequestors(Long requestorId) {
		log.info("Поиск запросов на вещи от другого пользователя с id = {}", requestorId);

		return repository.findByRequestorIdNotOrderByCreatedDesc(requestorId)
				.stream()
				.map(ItemRequestMapper::mapToItemRequestDto)
				.collect(toList());
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
	}

	private ItemRequest findRequestById(Long itemRequestId) {
		return repository.findById(itemRequestId)
				.orElseThrow(() -> new NotFoundException("Запрос на вещь с id = " + itemRequestId + " не найден"));
	}

	private List<ItemRequestDto> fillRequestsData(List<ItemRequest> requests) {

		List<Long> requestIds = requests.stream()
				.map(ItemRequest::getId)
				.toList();

		Map<Long, List<Item>> requestItems = itemRepository
				.findByRequestIdIn(requestIds)
				.stream()
				.collect(groupingBy(Item::getRequestId, toList()));

		List<ItemRequestDto> requestsList = new ArrayList<>();
		for (ItemRequest request : requests) {

			requestsList.add(ItemRequestMapper.mapToItemRequestDto(request,
					requestItems.getOrDefault(request.getId(), Collections.emptyList()))
			);
		}

		return requestsList;
	}
}