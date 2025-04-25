package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

	public static User mapToUserDto(CreateUserRequest request) {
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());

		return user;
	}

	public static UserResponse mapToUserDto(User user) {
		UserResponse userResponse = new UserResponse();
		userResponse.setId(user.getId());
		userResponse.setName(user.getName());
		userResponse.setEmail(user.getEmail());

		return userResponse;
	}

	public static User updateUserFields(User user, UpdateUserRequest request) {
		if (request.getName() != null) {
			user.setName(request.getName());
		}

		if (request.getEmail() != null) {
			user.setEmail(request.getEmail());
		}

		return user;
	}
}