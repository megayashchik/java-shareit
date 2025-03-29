package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Item {
	private Long id;

	@NotBlank(message = "Название не может быть пустым")
	private String name;

	@NotBlank(message = "Описание вещи не может быть пустым")
	private String description;

	@NotNull(message = "Статус вещи должен быть указан (доступна ли вещь или занята)")
	private Boolean available;

	private Long ownerId;

	private Long requestId;
}