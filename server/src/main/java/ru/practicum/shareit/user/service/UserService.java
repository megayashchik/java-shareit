package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
	UserDto create(CreateUserRequest request);

	UserDto update(Long userId, UpdateUserRequest request);

	void delete(Long userId);

	UserDto findById(Long userId);

	List<UserDto> findAll();
}