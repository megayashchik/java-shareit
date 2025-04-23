package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	@Transactional
	public BookingResponse create(Long userId, CreateBookingRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Запрос на бронирование не может быть пустым");
		}
		User booker = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
		Item item = itemRepository.findById(request.getItemId())
				.orElseThrow(() -> new NotFoundException("Вещь с id = " + request.getItemId() + " не найдена"));

		if (!item.getAvailable()) {
			throw new IllegalArgumentException("Вещь не доступна для бронирования");
		}

		if (item.getOwner().getId().equals(userId)) {
			throw new IllegalArgumentException("Нельзя забронировать свою вещь");
		}

		Booking booking = bookingRepository.save(BookingMapper.mapToBooking(request, booker, item));

		return BookingMapper.mapToBookingDto(booking);
	}

	@Override
	@Transactional
	public BookingResponse approveBooking(Long userId, Long bookingId, Boolean approve) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
		Item item = booking.getItem();

		if (!item.getOwner().getId().equals(userId)) {
			throw new NotOwnerException("Только владелец вещи может подтверждать бронирование");
		}

		if (booking.getStatus() != Status.WAITING) {
			throw new IllegalArgumentException("Бронирование подтверждено или отклонено");
		}

		if (approve) {
			booking.setStatus(Status.APPROVED);
		} else {
			booking.setStatus(Status.REJECTED);
		}

		Booking updatedBooking = bookingRepository.save(booking);

		return BookingMapper.mapToBookingDto(updatedBooking);
	}

	@Override
	public BookingResponse findById(Long userId, Long bookingId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));

		return BookingMapper.mapToBookingDto(booking);
	}

	@Override
	public List<BookingResponse> findAllByBooker(Long bookerId, State state) {
		userRepository.findById(bookerId)
				.orElseThrow(() ->
						new NotFoundException("Пользователь бронирования с id = " + bookerId + " не найден"));

		LocalDateTime now = LocalDateTime.now();
		List<Booking> bookings = switch (state) {
			case ALL -> bookingRepository.findAllByBooker(bookerId);
			case CURRENT -> bookingRepository.findCurrentByBooker(bookerId, now);
			case PAST -> bookingRepository.findPastByBooker(bookerId, now);
			case FUTURE -> bookingRepository.findFutureByBooker(bookerId, now);
			case WAITING -> bookingRepository.findByBookerAndStatus(bookerId, Status.WAITING);
			case REJECTED -> bookingRepository.findByBookerAndStatus(bookerId, Status.REJECTED);
		};

		return BookingMapper.mapToBookingList(bookings);
	}

	@Override
	public List<BookingResponse> findAllByOwner(Long ownerId, State state) {
		userRepository.findById(ownerId)
				.orElseThrow(() -> new NotFoundException("Владелец вещи с id = " + ownerId + " не найден"));

		LocalDateTime now = LocalDateTime.now();
		List<Booking> bookings = switch (state) {
			case ALL -> bookingRepository.findAllByOwner(ownerId);
			case CURRENT -> bookingRepository.findCurrentByOwner(ownerId, now);
			case PAST -> bookingRepository.findPastByOwner(ownerId, now);
			case FUTURE -> bookingRepository.findFutureByOwner(ownerId, now);
			case WAITING -> bookingRepository.findByOwnerAndStatus(ownerId, Status.WAITING);
			case REJECTED -> bookingRepository.findByOwnerAndStatus(ownerId, Status.REJECTED);
		};

		return BookingMapper.mapToBookingList(bookings);
	}
}