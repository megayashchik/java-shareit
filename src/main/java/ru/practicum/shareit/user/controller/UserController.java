package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto create(@Valid @RequestBody NewUserRequest request) {
		log.info("Запрос на создание пользователя: {}", request);
		UserDto createdUser = userService.create(request);
		log.info("Создан пользователь: {}", createdUser);

		return createdUser;
	}

	@PatchMapping("/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public UserDto update(@PathVariable("userId") Long userId,
	                      @Valid @RequestBody UpdateUserRequest request) {
		log.info("Запрос на обновление пользователя с id={}: {}", userId, request);
		UserDto updatedUser = userService.update(userId, request);
		log.info("Обновлен пользователь с id={}: {}", userId, updatedUser);

		return updatedUser;
	}

	@GetMapping("/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public UserDto findById(@PathVariable("userId") Long userId) {
		log.info("Запрос на поиск пользователя по id={}", userId);
		UserDto foundUser = userService.findById(userId);
		log.info("Найден пользователь с id={}: {}", userId, foundUser);

		return foundUser;
	}

	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable("userId") Long userId) {
		log.info("Запрос на удаление пользователя с id={}", userId);
		userService.delete(userId);
		log.info("Пользователь с id={} удалён", userId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Collection<UserDto> findAll() {
		log.info("Запрос на список пользователей");
		Collection<UserDto> foundUser = userService.findAll();
		log.info("Список пользователей {}", foundUser);

		return foundUser;
	}
}