package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookingRequest {
	LocalDateTime start;

	LocalDateTime end;

	Long itemId;

	Long bookerId;
}



//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class CreateBookingRequest {
//	@NotNull(message = "Дата начала бронирования должна быть указана")
//	@FutureOrPresent(message = "Дата начала должна быть сегодня или в будущем")
//	LocalDateTime start;
//
//	@NotNull(message = "Дата окончания бронирования должна быть указана")
//	@Future(message = "Дата окончания бронирования должна быть в будущем")
//	LocalDateTime end;
//
//	//	@NotNull(message = "Id вещи должен быть указан")
//	Long itemId;
//
//	Long bookerId;
//}