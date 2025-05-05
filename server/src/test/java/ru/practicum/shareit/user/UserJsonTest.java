package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserJsonTest {
	@Autowired
	private JacksonTester<UserDto> json;

	@Test
	void should_serialize_user_dto_correctly() throws Exception {
		UserDto userDto = new UserDto(1L, "ivan@email", "Ivan Ivanov");

		JsonContent<UserDto> result = json.write(userDto);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
		assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
	}
}