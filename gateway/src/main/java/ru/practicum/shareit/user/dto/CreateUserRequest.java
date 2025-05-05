package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
	@NotBlank(message = "Email не может быть пустым")
	@Email(message = "Адрес электронной почты должен быть в формате user@domain.ru")
	String email;

	@NotBlank(message = "Имя не может быть пустым")
	String name;
}