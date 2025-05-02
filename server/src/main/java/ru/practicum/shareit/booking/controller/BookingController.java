package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
	private final BookingService bookingService;
	private final String id = "/{booking-id}";
	private final String owner = "/owner";

	private final String headerUserId = "X-Sharer-User-Id";
	private final String pvBookingId = "booking-id";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookingDto create(@RequestHeader(headerUserId) Long userId,
	                         @Valid @RequestBody CreateBookingRequest request) {
		log.info("Создание запроса бронирования от пользователя с id = {}", userId);
		BookingDto newBooking = bookingService.create(userId, request);
		log.info("Создан запрос {} от пользователя с id = {}", newBooking, userId);

		return newBooking;
	}

	@PutMapping
	public BookingDto update(@RequestHeader(headerUserId) Long userId,
	                         @RequestBody UpdateBookingRequest newBooking) {
		log.info("Обновление запроса бронирования от пользователя с id = {}", userId);
		BookingDto updateBooking = bookingService.update(userId, newBooking);
		log.info("Обновлён запрос бронирования от пользователя с id = {}", userId);

		return updateBooking;
	}

	@DeleteMapping(id)
	public void delete(@PathVariable(pvBookingId) Long bookingId) {
		log.info("Удаление запроса бронирования от пользователя с id = {}", bookingId);
		bookingService.delete(bookingId);
		log.info("Удалён запрос бронирования от пользователя с id = {}", bookingId);
	}

	@PatchMapping(id)
	@ResponseStatus(HttpStatus.OK)
	public BookingDto approve(@PathVariable(pvBookingId) Long bookingId,
	                          @RequestHeader(headerUserId) Long userId,
	                          @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
		log.info("Подтверждение статуса бронирования с id = {}", bookingId);
		BookingDto approvedResponse = bookingService.approveBooking(bookingId, userId, approved);
		log.info("Подтверждение статуса бронирования с id = {} завершено, статус бронирования {}", bookingId, approved);

		return approvedResponse;
	}

	@GetMapping(id)
	@ResponseStatus(HttpStatus.OK)
	public BookingDto findById(@RequestHeader(headerUserId) Long userId,
	                           @PathVariable(pvBookingId) Long bookingId) {
		log.info("Запрос на поиск бронирования по id = {} от пользователя с id = {}", bookingId, userId);
		BookingDto booking = bookingService.findBookingById(bookingId, userId);
		log.info("Найдено бронирование {}", booking);

		return booking;
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<BookingDto> findAllByBooker(@RequestHeader(headerUserId) Long userId,
	                                        @RequestParam(name = "state", defaultValue = "ALL") String state) {
		log.info("Получения всех бронирования пользователя с id = {}, и статусом {}", userId, state);
		List<BookingDto> bookings = bookingService.findAllByBooker(userId, state);
		log.info("Получено {} бронирований пользователя с id = {}", bookings.size(), userId);

		return bookings;
	}

	@GetMapping(owner)
	@ResponseStatus(HttpStatus.OK)
	public List<BookingDto> findAllByOwner(@RequestHeader(headerUserId) Long ownerId,
	                                       @RequestParam(name = "state", defaultValue = "ALL") String state) {
		log.info("Получение всех бронирований вещей со статусом {} от владельца с id = {}", state, ownerId);
		List<BookingDto> bookings = bookingService.findAllByOwner(ownerId, state);
		log.info("Получено {} бронирований вещей владельца с id = {}", bookings.size(), ownerId);

		return bookings;
	}
}