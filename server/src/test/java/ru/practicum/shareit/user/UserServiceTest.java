package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void should_create_user_successfully() {
		CreateUserRequest request = new CreateUserRequest("john.doe@mail.com", "John Doe");
		User user = new User(1L, request.getEmail(), request.getName());

		when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
		when(userRepository.save(any())).thenReturn(user);

		UserDto result = userService.create(request);

		assertNotNull(result);
		assertEquals(user.getId(), result.getId());
		assertEquals(user.getEmail(), result.getEmail());
		assertEquals(user.getName(), result.getName());
	}

	@Test
	void should_fail_create_user_when_request_is_null() {
		assertThrows(IllegalArgumentException.class, () -> userService.create(null));
	}

	@Test
	void should_fail_create_user_when_name_is_blank() {
		CreateUserRequest request = new CreateUserRequest("test@mail.com", " ");
		assertThrows(IllegalArgumentException.class, () -> userService.create(request));
	}

	@Test
	void should_fail_create_user_when_email_is_blank() {
		CreateUserRequest request = new CreateUserRequest(" ", "John");
		assertThrows(IllegalArgumentException.class, () -> userService.create(request));
	}

	@Test
	void should_fail_find_user_when_id_not_found() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> userService.findById(2L));
	}

	@Test
	void should_fail_create_user_when_email_exists() {
		CreateUserRequest newUser = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);

		DuplicateEmailException thrown = assertThrows(DuplicateEmailException.class, () -> {
			userService.create(newUser);
		});

		assertEquals(String.format("Этот E-mail \"%s\" уже используется", newUser.getEmail()), thrown.getMessage());
	}

	@Test
	void should_update_user_successfully() {
		Long userId = 1L;
		User existingUser = new User(userId, "old@mail.com", "Old Name");

		UpdateUserRequest request = new UpdateUserRequest(userId, "new@mail.com", "New Name");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
		when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		UserDto result = userService.update(userId, request);

		assertEquals("new@mail.com", result.getEmail());
		assertEquals("New Name", result.getName());
	}

	@Test
	void should_fail_update_user_when_email_exists() {
		Long userId = 1L;
		String existingEmail = "existing@mail.com";
		String newEmail = "new@mail.com";

		UpdateUserRequest updateRequest = new UpdateUserRequest(userId, newEmail, "New Name");

		User existingUser = new User(userId, "old@mail.com", "Old Name");
		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

		when(userRepository.existsByEmail(newEmail)).thenReturn(true);

		DuplicateEmailException thrown = assertThrows(DuplicateEmailException.class, () -> {
			userService.update(userId, updateRequest);
		});

		assertEquals("Этот email " + newEmail + " уже используется", thrown.getMessage());
	}

	@Test
	void should_fail_update_user_when_id_missing() {
		UpdateUserRequest newUser = new UpdateUserRequest(1L, "john.doe@mail.com", "John Doe");

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			userService.update(null, newUser);
		});

		assertEquals("id пользователя должен быть указан", thrown.getMessage());
	}

	@Test
	void should_fail_update_user_when_name_is_blank() {
		Long userId = 1L;
		User existingUser = new User(userId, "old@mail.com", "Old Name");
		UpdateUserRequest request = new UpdateUserRequest(userId, "new@mail.com", " ");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

		assertThrows(IllegalArgumentException.class, () -> userService.update(userId, request));
	}

	@Test
	void should_fail_update_user_when_email_is_blank() {
		Long userId = 1L;
		User existingUser = new User(userId, "old@mail.com", "Old Name");
		UpdateUserRequest request = new UpdateUserRequest(userId, " ", "New Name");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

		assertThrows(IllegalArgumentException.class, () -> userService.update(userId, request));
	}

	@Test
	void should_delete_user_successfully() {
		Long userId = 1L;
		User user = new User(userId, "mail@mail.com", "User");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		doNothing().when(userRepository).deleteById(userId);

		userService.delete(userId);

		verify(userRepository).deleteById(userId);
	}

	@Test
	void should_fail_delete_when_user_not_found() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> userService.delete(1L));
	}

	@Test
	void should_return_all_users() {
		List<User> users = List.of(
				new User(1L, "a@mail.com", "Alice"),
				new User(2L, "b@mail.com", "Bob")
		);

		when(userRepository.findAll()).thenReturn(users);

		List<UserDto> result = userService.findAll();

		assertEquals(2, result.size());
	}
}