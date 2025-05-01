package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

	@Autowired
	ObjectMapper mapper;

	@MockBean
	UserService userService;

	@Autowired
	private MockMvc mvc;

	private final String urlTemplate = "/users";

	private UserDto makeUserDto(Long id, String email, String name, LocalDate date) {
		UserDto dto = new UserDto();
		dto.setId(id);
		dto.setEmail(email);
		dto.setName(name);

		return dto;
	}

	@Test
	void should_create_user_with() throws Exception {
		UserDto userDto =
				makeUserDto(1L, "john.doe@mail.com", "John Doe",
						LocalDate.of(2022, 7, 3));

		when(userService.create(any())).thenReturn(userDto);

		mvc.perform(post(urlTemplate)
						.content(mapper.writeValueAsString(userDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
				.andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
				.andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
	}

	@Test
	void should_update_user() throws Exception {
		UserDto userDto = makeUserDto(1L, "john.doe@mail.com", "John Doe",
				LocalDate.of(2022, 7, 3));

		when(userService.update(anyLong(), any())).thenReturn(userDto);

		mvc.perform(patch(urlTemplate + "/" + userDto.getId())
						.content(mapper.writeValueAsString(userDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
				.andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
				.andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
	}

	@Test
	void should_delete_user() throws Exception {
		mvc.perform(delete(urlTemplate + "/" + anyLong()))
				.andExpect(status().isOk());

		verify(userService, times(1)).delete(anyLong());
	}

	@Test
	void should_find_user_by_id() throws Exception {
		UserDto findUser = makeUserDto(1L, "ivan@email", "Ivan Ivanov",
				LocalDate.of(2022, 7, 3));

		when(userService.findById(anyLong())).thenReturn(findUser);

		mvc.perform(get(urlTemplate + "/" + findUser.getId())
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.id", is(findUser.getId()), Long.class))
				.andExpect(jsonPath("$.email", is(findUser.getEmail()), String.class))
				.andExpect(jsonPath("$.name", is(findUser.getName()), String.class));
	}

	@Test
	void should_find_all_users() throws Exception {
		List<UserDto> newUsers = List.of(
				makeUserDto(1L, "ivan@email", "Ivan Ivanov",
						LocalDate.of(2022, 7, 3)),
				makeUserDto(2L, "petr@email", "Petr Petrov",
						LocalDate.of(2022, 8, 4)));

		when(userService.findAll()).thenReturn(newUsers);

		mvc.perform(get(urlTemplate)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value(is(newUsers.getFirst().getId()), Long.class))
				.andExpect(jsonPath("$[0].email").value(is(newUsers.getFirst().getEmail())))
				.andExpect(jsonPath("$[0].name").value(is(newUsers.getFirst().getName())))
				.andExpect(jsonPath("$[1].id").value(is(newUsers.getLast().getId()), Long.class))
				.andExpect(jsonPath("$[1].email").value(is(newUsers.getLast().getEmail())))
				.andExpect(jsonPath("$[1].name").value(is(newUsers.getLast().getName())));
	}
}