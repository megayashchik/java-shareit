package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.Collection;

public interface UserService {
	UserResponse create(CreateUserRequest request);

	UserResponse update(Long userId, UpdateUserRequest request);

	UserResponse findById(Long userId);

	void delete(Long userId);

	Collection<UserResponse> findAll();
}