package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
	private final CreateUserRequest newUser = new CreateUserRequest("john.doe@mail.com", "John Doe");
	private final UpdateUserRequest updUser = new UpdateUserRequest(1L, "john.doe@mail.com", "John Doe");
	private final User user = new User(1L, "john.doe@mail.com", "John Doe");
	private final UserDto dto = new UserDto(1L, "john.doe@mail.com", "John Doe");

	private final UpdateUserRequest emptyUpdUser = new UpdateUserRequest(1L, "", "");

	@Test
	public void should_map_user_to_dto_correctly() {
		UserDto userDto = UserMapper.mapToUserDto(user);
		assertThat(userDto, equalTo(dto));
	}

	@Test
	public void should_map_create_request_to_user_correctly() {
		User us = UserMapper.mapToUser(newUser);
		assertThat(us.getName(), equalTo(user.getName()));
		assertThat(us.getEmail(), equalTo(user.getEmail()));
		assertThat(us.getName(), equalTo(user.getName()));
	}

	@Test
	public void should_update_user_fields_correctly() {
		User us = UserMapper.updateUser(user, updUser);
		assertThat(us.getId(), equalTo(user.getId()));
		assertThat(us.getName(), equalTo(user.getName()));
		assertThat(us.getEmail(), equalTo(user.getEmail()));
	}

	@Test
	public void should_ignore_empty_fields_when_updating_user() {
		User us = UserMapper.updateUser(user, emptyUpdUser);
		assertThat(us.getId(), equalTo(user.getId()));
		assertThat(us.getName(), equalTo(user.getName()));
		assertThat(us.getEmail(), equalTo(user.getEmail()));
	}

	@Test
	void should_not_update_user_when_fields_are_null() {
		User user = new User(1L, "initial@mail.com", "Initial Name");
		UpdateUserRequest request = new UpdateUserRequest(1L, null, null);

		User result = UserMapper.updateUser(user, request);

		assertEquals("initial@mail.com", result.getEmail());
		assertEquals("Initial Name", result.getName());
	}

	@Test
	void should_not_update_email_if_same_as_existing() {
		User user = new User(1L, "same@mail.com", "Name");
		UpdateUserRequest request = new UpdateUserRequest(1L, "same@mail.com", "Updated Name");

		User result = UserMapper.updateUser(user, request);

		assertEquals("same@mail.com", result.getEmail());
		assertEquals("Updated Name", result.getName());
	}
}