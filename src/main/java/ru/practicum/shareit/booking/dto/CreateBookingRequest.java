package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateBookingRequest {
	@NotNull(message = "Id вещи должен быть указан")
	private Long itemId;

	@NotNull(message = "Дата начала бронирования должна быть указана")
	@FutureOrPresent(message = "Дата начала должна быть сегодня или в будущем")
	private LocalDateTime start;

	@NotNull(message = "Дата окончания бронирования должна быть указана")
	@Future(message = "Дата окончания бронирования должна быть в будущем")
	private LocalDateTime end;
}
