package ru.practicum.shareit.item.dto;

import io.micrometer.common.util.StringUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class UpdateItemRequest {
	Long id;

	String name;

	String description;

	Boolean available;

	Long ownerId;

	Long requestId;

	public boolean hasName() {
		return !StringUtils.isBlank(this.name);
	}

	public boolean hasDescription() {
		return !StringUtils.isBlank(this.description);
	}

	public boolean hasAvailable() {
		return this.available != null;
	}
}