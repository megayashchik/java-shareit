package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {
	@NotBlank(message = "Комментарий не может быть пустым")
	private String text;
}