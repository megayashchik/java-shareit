package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
	private Long id;

	@Email(message = "Адрес электронной почты должен быть в формате example@domain.ru")
	private String email;

	private String name;
}