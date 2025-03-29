package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

	public static User mapToUser(NewUserRequest request) {
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());

		return user;
	}

	public static UserDto mapToUserDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setEmail(user.getEmail());

		return userDto;
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