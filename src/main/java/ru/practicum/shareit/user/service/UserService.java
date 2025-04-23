package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
	UserDto create(CreateUserRequest request);

	UserDto update(Long userId, UpdateUserRequest request);

	UserDto findById(Long userId);

	void delete(Long userId);

	Collection<UserDto> findAll();
}