package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Controller
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
	private final UserClient userClient;
	private final String id = "/{user-id}";

	private final String pvUserId = "user-id";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> create(@Valid @RequestBody CreateUserRequest request) {
		log.info("Создание пользователя {}", request);
		return userClient.create(request);
	}

	@PatchMapping(id)
	public ResponseEntity<Object> update(@PathVariable(pvUserId) Long userId,
	                                     @Valid @RequestBody UpdateUserRequest request) {
		log.info("Обновление пользователя с id = {}", userId);
		return userClient.update(userId, request);
	}

	@DeleteMapping(id)
	public ResponseEntity<Object> delete(@PathVariable(pvUserId) Long userId) {
		log.info("Удаление пользователя с id = {}", userId);
		return userClient.delete(userId);
	}

	@GetMapping(id)
	public ResponseEntity<Object> findUser(@PathVariable(pvUserId) Long userId) {
		log.info("Получение пользователя с id = {}", userId);
		return userClient.findUser(userId);
	}

	@GetMapping
	public ResponseEntity<Object> getUsers() {
		log.info("Получение пользователей");
		return userClient.findUsers();
	}
}