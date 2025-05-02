package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.comment.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
	@Mock
	private ItemRepository itemRepository;

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private ItemServiceImpl itemService;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void should_fail_add_comment_when_user_not_booked_item() {
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
	void should_return_empty_list_when_owner_id_invalid() {
		when(userRepository.findById(999L))
				.thenReturn(Optional.empty());

		NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
			itemService.findAll(999L);
		});

		assertEquals("Пользователь с id = 999 не найден", thrown.getMessage());
	}
}