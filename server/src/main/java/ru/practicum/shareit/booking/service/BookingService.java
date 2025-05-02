package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;

import java.util.List;

public interface BookingService {
	BookingDto create(Long userId, CreateBookingRequest request);

	BookingDto update(Long userId, UpdateBookingRequest request);

	void delete(Long bookingId);

	BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

	BookingDto findBookingById(Long bookingId, Long userId);

	List<BookingDto> findAllByBooker(Long userId, String state);

	List<BookingDto> findAllByOwner(Long userId, String state);
}