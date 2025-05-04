package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.comment.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
	@Mock
	private ItemRepository itemRepository;

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RequestRepository requestRepository;

	@InjectMocks
	private ItemServiceImpl itemService;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void should_create_item_successfully() {
		Long ownerId = 1L;
		Long requestId = 1L;

		CreateItemRequest request =
				new CreateItemRequest("name", "description", true, ownerId, requestId);

		User owner = new User(ownerId, "email", "name");
		Item item = new Item(1L, "name", "description", true, owner, requestId);
		ItemDto expectedDto = ItemMapper.mapToItemDto(item);

		when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
		when(requestRepository.findById(eq(requestId))).thenReturn(Optional.of(new ItemRequest()));
		when(itemRepository.save(any(Item.class))).thenReturn(item);

		ItemDto result = itemService.create(ownerId, request);

		assertNotNull(result);
		assertEquals("name", result.getName());
		assertTrue(result.getAvailable());
		assertEquals(ownerId, result.getOwnerId());
		assertEquals(requestId, result.getRequestId());

		verify(userRepository).findById(ownerId);
		verify(requestRepository).findById(eq(requestId));
		verify(itemRepository).save(any(Item.class));
	}

	@Test
	void should_fail_add_comment_when_user_no_booked_item() {
		when(requestRepository.findById(anyLong())).thenReturn(Optional.of(new ItemRequest()));
		when(requestRepository.existsById(anyLong())).thenReturn(true);

		CreateCommentRequest newComment = new CreateCommentRequest("comment", 1L, 1L);

		CreateUserRequest newUser = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

		UserDto userDto = userService.create(newUser);
		User user = new User(1L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);
		when(itemRepository.save(any())).thenReturn(item);

		ItemDto findItem = itemService.create(1L, newItem);

		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(),
				anyLong(), any())).thenReturn(Boolean.FALSE);
		NotBookedException thrown = assertThrows(NotBookedException.class, () -> {
			itemService.addComment(1L, 1L, newComment);
		});

		assertEquals(String.format("Пользователь %s не может оставить комментарий, " +
				"так как не пользовался вещью %s", user.getName(), item.getName()), thrown.getMessage());
	}

	@Test
	void should_fail_delete_item_when_user_not_owner() {
		when(requestRepository.findById(anyLong())).thenReturn(Optional.of(new ItemRequest()));
		when(requestRepository.existsById(anyLong())).thenReturn(true);

		CreateUserRequest newUser = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

		UserDto userDto = userService.create(newUser);
		User user = new User(1L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

		CreateItemRequest newItem = new CreateItemRequest("name", "description",
				Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);
		when(itemRepository.save(any())).thenReturn(item);

		ItemDto findItem = itemService.create(1L, newItem);

		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		NotOwnerException thrown = assertThrows(NotOwnerException.class, () -> {
			itemService.delete(2L, 1L);
		});

		assertEquals("Только владелец вещи может её удалить", thrown.getMessage());
	}

	@Test
	void should_fail_update_item_when_user_not_owner() {
		when(requestRepository.findById(anyLong())).thenReturn(Optional.of(new ItemRequest()));
		when(requestRepository.existsById(anyLong())).thenReturn(true);

		CreateUserRequest newUser = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

		UserDto userDto = userService.create(newUser);
		User user = new User(1L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

		CreateItemRequest newItem = new CreateItemRequest("name", "description",
				Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);
		when(itemRepository.save(any())).thenReturn(item);

		ItemDto findItem = itemService.create(1L, newItem);

		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		NotOwnerException thrown = assertThrows(NotOwnerException.class, () -> {
			itemService.update(1L, null, 2L);
		});

		assertEquals("Редактировать данные вещи может только её владелец", thrown.getMessage());
	}

	@Test
	void should_return_empty_list_when_search_text_blank() {
		Collection<ItemDto> items = itemService.findItemsByBooker(1L, null);

		assertEquals(items, new ArrayList<>());
	}

	@Test
	void should_return_empty_list_when_owner_id_not_valid() {
		when(userRepository.findById(999L))
				.thenReturn(Optional.empty());

		NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
			itemService.findAll(999L);
		});

		assertEquals("Пользователь с id = 999 не найден", thrown.getMessage());
	}


	@Test
	void should_throw_when_create_item_request_is_null() {
		Long ownerId = 1L;
		assertThrows(IllegalArgumentException.class, () -> {
			itemService.create(ownerId, null);
		});
	}

	@Test
	void should_throw_when_item_name_is_blank() {
		Long ownerId = 1L;
		CreateItemRequest request =
				new CreateItemRequest(" ", "desc", true, ownerId, null);
		assertThrows(IllegalArgumentException.class, () -> {
			itemService.create(ownerId, request);
		});
	}

	@Test
	void should_throw_when_item_description_is_blank() {
		Long ownerId = 1L;
		CreateItemRequest request =
				new CreateItemRequest("name", " ", true, ownerId, null);
		assertThrows(IllegalArgumentException.class, () -> {
			itemService.create(ownerId, request);
		});
	}

	@Test
	void should_throw_when_item_availability_is_null() {
		Long ownerId = 1L;
		CreateItemRequest request =
				new CreateItemRequest("name", "desc", null, ownerId, null);
		assertThrows(DuplicateEmailException.class, () -> {
			itemService.create(ownerId, request);
		});
	}

	@Test
	void should_update_item_successfully() {
		Long ownerId = 1L;
		User user = new User(ownerId, "email", "name");
		Item item = new Item(1L, "old", "oldDesc", true, user, null);

		when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
		when(itemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		UpdateItemRequest request = mock(UpdateItemRequest.class);
		when(request.getName()).thenReturn("newName");
		when(request.getDescription()).thenReturn("newDesc");
		when(request.getAvailable()).thenReturn(false);

		when(request.hasName()).thenReturn(true);
		when(request.hasDescription()).thenReturn(true);
		when(request.hasAvailable()).thenReturn(true);

		ItemDto result = itemService.update(1L, request, ownerId);

		assertEquals("newName", result.getName());
		assertEquals("newDesc", result.getDescription());
		assertFalse(result.getAvailable());
	}

	@Test
	void should_return_items_by_text() {
		String text = "поиск";
		User user = new User(1L, "email", "name");
		Item item = new Item(1L, "поиск", "описание", true, user, null);
		List<Item> items = List.of(item);

		when(itemRepository.findItemsByText(text)).thenReturn(items);

		List<ItemDto> results = itemService.findItemsByBooker(1L, text);
		assertEquals(1, results.size());
		assertEquals("поиск", results.get(0).getName());
	}
}