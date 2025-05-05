package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateItemRequest {
	String name;

	String description;

	@NotNull(message = "Статус вещи не может быть пустым. Укажите занята вещь или свободна")
	Boolean available;

	Long ownerId;

	Long requestId;

	public boolean hasRequestId() {
		return requestId != null;
	}
}