package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
	private final BookingService bookingService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookingResponse create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                              @Valid @RequestBody CreateBookingRequest request) {
		log.info("Создание запроса от пользователя с id = {}", userId);
		BookingResponse newBooking = bookingService.create(userId, request);
		log.info("Создан запрос {} от пользователя с id = {}", newBooking, userId);

		return newBooking;
	}

	@PatchMapping("/{bookingId}")
	@ResponseStatus(HttpStatus.OK)
	public BookingResponse approve(@RequestHeader("X-Sharer-User-Id") Long userId,
	                               @PathVariable Long bookingId,
	                               @RequestParam("approved") Boolean approve) {
		log.info("Подтверждение статуса бронирования с id = {}", bookingId);
		BookingResponse approvedResponse = bookingService.approveBooking(userId, bookingId, approve);
		log.info("Подтверждение статуса бронирования с id = {} завершено, статус бронирования {}", bookingId, approve);

		return approvedResponse;
	}

	@GetMapping("/{bookingId}")
	@ResponseStatus(HttpStatus.OK)
	public BookingResponse findById(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                @PathVariable Long bookingId) {
		log.info("Запрос на поиск бронирования по id = {} от пользователя с id = {}", bookingId, userId);
		BookingResponse booking = bookingService.findById(userId, bookingId);
		log.info("Найдено бронирование {}", booking);

		return booking;
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<BookingResponse> findAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                             @RequestParam(defaultValue = "ALL") State state) {
		log.info("Получения всех бронирования пользователя с id = {}, и статусом {}", userId, state);
		List<BookingResponse> bookings = bookingService.findAllByBooker(userId, state);
		log.info("Получено {} бронирований пользователя с id = {}", bookings.size(), userId);

		return bookings;
	}

	@GetMapping("/owner")
	@ResponseStatus(HttpStatus.OK)
	public List<BookingResponse> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
	                                            @RequestParam(defaultValue = "ALL") State state) {
		log.info("Получение всех бронирований вещей со статусом {} от владельца с id = {}", state, ownerId);
		List<BookingResponse> bookings = bookingService.findAllByOwner(ownerId, state);
		log.info("Получено {} бронирований вещей владельца с id = {}", bookings.size(), ownerId);

		return bookings;
	}
}