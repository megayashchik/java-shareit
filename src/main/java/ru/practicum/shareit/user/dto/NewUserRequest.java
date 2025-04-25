package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequest {
	private Long id;

	@NotBlank(message = "Email не может быть пустым")
	@Email(message = "Адрес электронной почты должен быть в формате user@domain.ru")
	private String email;

	@NotBlank(message = "Имя не может быть пустым")
	private String name;
}