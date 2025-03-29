package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class User {
	private Long id;

	@NotBlank(message = "Имя не может быть пустым")
	private String name;

	@NotBlank(message = "Email не может быть пустым")
	@Email(message = "Адрес электронной почты должен быть в формате user@domain.ru")
	private String email;
}