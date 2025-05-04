package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookingRequest {

	@NotNull(message = "Дата начала бронирования не может быть пустой")
	@FutureOrPresent(message = "Дата начала бронирования должна быть в настоящем или будущем")
	LocalDateTime start;

	@NotNull(message = "Дата окончания бронирования не может быть пустой")
	@Future(message = "Дата окончания бронирования должна быть в будущем")
	LocalDateTime end;

	@NotNull(message = "ID вещи не может быть пустым")
	Long itemId;

	Long bookerId;

	@AssertTrue(message = "Дата окончания должна быть позже даты начала")
	public boolean isEndAfterStart() {
		return start != null && end != null && end.isAfter(start);
	}
}