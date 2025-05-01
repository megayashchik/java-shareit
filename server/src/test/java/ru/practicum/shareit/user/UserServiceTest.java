package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void should_fail_find_user_when_id_not_found() {
		when((userRepository).findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> userService.findById(2L));
	}

	@Test
	void should_fail_create_user_when_email_exists() {
		CreateUserRequest newUser = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser.getEmail())).
				thenReturn(Optional.of(new User(1L, "john.doe@mail.com", "Some User")));

		DuplicateEmailException  thrown = assertThrows(DuplicateEmailException .class, () -> {
			userService.create(newUser);
		});

		assertEquals(String.format("Этот E-mail \"%s\" уже используется", newUser.getEmail()), thrown.getMessage());
	}

	@Test
	void should_fail_update_user_when_email_exists() {
		UpdateUserRequest newUser = new UpdateUserRequest(1L, "john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser.getEmail())).
				thenReturn(Optional.of(new User(1L, "john.doe@mail.com", "Some User")));

		DuplicateEmailException  thrown = assertThrows(DuplicateEmailException .class, () -> {
			userService.update(1L, newUser);
		});

		assertEquals(String.format("Этот E-mail \"%s\" уже используется", newUser.getEmail()), thrown.getMessage());
	}

	@Test
	void should_fail_update_user_when_id_missing() {
		UpdateUserRequest newUser = new UpdateUserRequest(1L, "john.doe@mail.com", "John Doe");

		ValidationException thrown = assertThrows(ValidationException.class, () -> {
			userService.update(null, newUser);
		});

		assertEquals("ID пользователя должен быть указан", thrown.getMessage());
	}
}