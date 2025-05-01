package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

	public static User mapToUser(CreateUserRequest request) {
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());

		return user;
	}

	public static UserDto mapToUserDto(User user) {
		UserDto userResponse = new UserDto();
		userResponse.setId(user.getId());
		userResponse.setName(user.getName());
		userResponse.setEmail(user.getEmail());

		return userResponse;
	}

	public static User updateUser(User user, UpdateUserRequest request) {
		if (request.getName() != null) {
			user.setName(request.getName());
		}

		if (request.getEmail() != null) {
			user.setEmail(request.getEmail());
		}

		return user;
	}
}