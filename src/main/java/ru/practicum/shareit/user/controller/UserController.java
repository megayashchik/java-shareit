package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.sevice.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto create(@Valid @RequestBody NewUserRequest request) {
		return userService.create(request);
	}

	@PatchMapping("/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public UserDto update(@PathVariable("userId") Long userId,
	                      @Valid @RequestBody UpdateUserRequest request) {
		return userService.update(userId, request);
	}

	@GetMapping("/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public UserDto findById(@PathVariable("userId") Long userId) {
		return userService.findById(userId);
	}

	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public boolean deleteById(@PathVariable("userId") Long userId) {
		return userService.delete(userId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Collection<UserDto> findAll() {
		return userService.findAll();
	}
}