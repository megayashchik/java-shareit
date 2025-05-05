package ru.practicum.shareit.user.dto;

import io.micrometer.common.util.StringUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
	Long id;

	String email;

	String name;

	public boolean hasEmail() {
		return !StringUtils.isBlank(this.email);
	}

	public boolean hasName() {
		return !StringUtils.isBlank(this.name);
	}
}