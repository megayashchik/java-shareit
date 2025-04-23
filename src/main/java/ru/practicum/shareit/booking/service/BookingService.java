package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
	BookingResponse create(Long userId, CreateBookingRequest request);

	BookingResponse approveBooking(Long userId, Long bookingId, Boolean approve);

	BookingResponse findById(Long userId, Long bookingId);

	List<BookingResponse> findAllByBooker(Long userId, State state);

	List<BookingResponse> findAllByOwner(Long userId, State state);
}