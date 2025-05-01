package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateItemRequest {
	@NotBlank(message = "Название не может быть пустым")
	String name;

	@NotBlank(message = "Описание вещи не может быть пустым")
	String description;

	@NotNull(message = "Статус вещи должен быть указан (доступна ли вещь или занята)")
	Boolean available;

	Long ownerId;

	Long requestId;

	public boolean hasRequestId() {
		return requestId != null;
	}
}