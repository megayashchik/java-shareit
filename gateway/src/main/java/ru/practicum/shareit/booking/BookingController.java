package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.enums.State;

@Slf4j
@Controller
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
	private final BookingClient bookingClient;
	private final String id = "/{booking-id}";
	private final String owner = "/owner";

	private final String headerUserId = "X-Sharer-User-Id";
	private final String pvBookingId = "booking-id";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> create(@RequestHeader(headerUserId) Long userId,
	                                     @Valid @RequestBody CreateBookingRequest request) {
		log.info("Создание запроса от пользователя с id = {}: {}", userId, request);
		return bookingClient.create(userId, request);
	}

	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> update(@RequestHeader(headerUserId) Long userId,
	                                     @Valid @RequestBody UpdateBookingRequest request) {
		log.info("Обновление запроса от пользователя с id = {}: {}", userId, request);
		return bookingClient.update(userId, request);
	}

	@DeleteMapping(id)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Object> delete(@PathVariable(pvBookingId) Long bookingId) {
		log.info("Удаление запроса от с id = {}", bookingId);
		return bookingClient.delete(bookingId);
	}

	@PatchMapping(id)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> approve(@PathVariable(pvBookingId) Long bookingId,
	                                      @RequestHeader(headerUserId) Long userId,
	                                      @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
		log.info("Подтверждение статуса бронирования с id = {}", bookingId);
		return bookingClient.approve(bookingId, userId, approved);
	}

	@GetMapping(id)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> findById(@RequestHeader(headerUserId) Long userId,
	                                       @PathVariable(pvBookingId) Long bookingId) {
		log.info("Запрос на поиск бронирования по id = {} от пользователя с id = {}", bookingId, userId);
		return bookingClient.findBooking(userId, bookingId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> findAllByBooker(@RequestHeader(headerUserId) Long userId,
	                                              @RequestParam(name = "state", defaultValue = "ALL")
	                                              String stateParam) {
		log.info("Получения всех бронирований пользователя с id = {}, и статусом {}", userId, stateParam);
		State state = State.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Неизвестное состояние: " + stateParam));
		return bookingClient.findBookings(null, userId, state);
	}

	@GetMapping(owner)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> findAllByOwner(@RequestHeader(headerUserId) Long userId,
	                                             @RequestParam(name = "state", defaultValue = "ALL")
	                                             String stateParam) {
		log.info("Получение всех бронирований вещей со статусом {} от владельца с id = {}", stateParam, userId);
		State state = State.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Неизвестное состояние: " + stateParam));
		return bookingClient.findBookings(owner, userId, state);
	}
}