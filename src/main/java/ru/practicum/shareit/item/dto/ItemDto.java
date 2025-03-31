package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ItemDto {
	private Long id;

	private String name;

	private String description;

	private Boolean available;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long ownerId;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long requestId;
}