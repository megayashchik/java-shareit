package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserDtoTest {

	@Test
	public void should_create_userDto_withId() {
		UserDto userDto = new UserDto();
		Long id = 1L;

		userDto.setId(id);

		assertThat(userDto.getId()).isEqualTo(id);
	}

	@Test
	public void should_create_userDto_with_email() {
		UserDto userDto = new UserDto();
		String email = "test@email";

		userDto.setEmail(email);

		assertThat(userDto.getEmail()).isEqualTo(email);
	}

	@Test
	public void should_create_userDto_with_name() {
		UserDto userDto = new UserDto();
		String name = "Test Name";

		userDto.setName(name);

		assertThat(userDto.getName()).isEqualTo(name);
	}

	@Test
	public void should_create_create_user_request_with_email() {
		CreateUserRequest request = new CreateUserRequest();
		String email = "test@email";

		request.setEmail(email);

		assertThat(request.getEmail()).isEqualTo(email);
	}

	@Test
	public void should_create_create_user_request_with_name() {
		CreateUserRequest request = new CreateUserRequest();
		String name = "Test Name";

		request.setName(name);

		assertThat(request.getName()).isEqualTo(name);
	}

	@Test
	public void should_return_true_when_email_is_not_blank() {
		UpdateUserRequest request = new UpdateUserRequest();
		request.setEmail("test@email");

		assertThat(request.hasEmail()).isTrue();
	}

	@Test
	public void should_return_false_whenE_email_is_blank() {
		UpdateUserRequest request = new UpdateUserRequest();
		request.setEmail("");

		assertThat(request.hasEmail()).isFalse();
	}

	@Test
	public void should_return_true_when_name_is_not_blank() {
		UpdateUserRequest request = new UpdateUserRequest();
		request.setName("Test Name");

		assertThat(request.hasName()).isTrue();
	}

	@Test
	public void should_return_false_when_name_is_blank() {
		UpdateUserRequest request = new UpdateUserRequest();
		request.setName("");

		assertThat(request.hasName()).isFalse();
	}
}