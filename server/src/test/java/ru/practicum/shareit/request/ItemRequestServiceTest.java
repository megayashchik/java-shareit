package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
	@Mock
	private RequestRepository requestRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private ItemRequestServiceImpl itemRequestService;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void should_fail_update_item_request_when_email_already_exists() {
		UpdateRequest updItemRequest = new UpdateRequest(1L, "description1", 1L,
				LocalDateTime.of(2023, 7, 3, 19, 30, 1));

		CreateUserRequest newUser = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

		UserDto userDto = userService.create(newUser);

		when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(new User(1L, "john.doe@mail.com", "John Doe")));

		updItemRequest.setId(null);

		ValidationException thrown = assertThrows(ValidationException.class, () -> {
			itemRequestService.update(1L, updItemRequest);
		});

		assertEquals("ID запроса должен быть указан", thrown.getMessage());
	}
}