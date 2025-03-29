package ru.practicum.shareit.user.sevice;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
	UserDto create(NewUserRequest request);

	UserDto update(Long userId, UpdateUserRequest request);

	UserDto findById(Long userId);

	boolean delete(Long userId);

	Collection<UserDto> findAll();
}