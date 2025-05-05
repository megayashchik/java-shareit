package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	@Transactional
	public BookingDto create(Long userId, CreateBookingRequest request) {
		log.info("Запрос на создание бронирования от пользователя с id = {}", userId);
		User booker = findUserById(userId);
		Item item = itemRepository.findById(request.getItemId())
				.orElseThrow(() -> new NotFoundException("Вещь с id = " + request.getItemId() + " не найдена"));

		if (!item.getAvailable()) {
			throw new NotBookedException("Вещь не доступна для бронирования");
		}

		if (item.getUser().getId().equals(userId)) {
			throw new NotBookedException("Нельзя забронировать свою вещь");
		}

		Booking booking = bookingRepository.save(BookingMapper.mapToBooking(request, booker, item));
		log.info("Создано бронирование от пользователя с id = {}", userId);

		return BookingMapper.mapToBookingDto(booking);
	}

	@Override
	@Transactional
	public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
		log.info("Подтверждение или отклонение запроса на бронирование с id = {} владельца с id = {}",
				bookingId, userId);
		Booking booking = findBookingById(bookingId);
		Item item = booking.getItem();

		if (!item.getUser().getId().equals(userId)) {
			throw new NotOwnerException("Только владелец вещи может подтверждать бронирование");
		}

		if (booking.getStatus() != Status.WAITING) {
			throw new NotBookedException("Бронирование подтверждено или отклонено");
		}

		if (approved) {
			booking.setStatus(Status.APPROVED);
		} else {
			booking.setStatus(Status.REJECTED);
		}

		Booking updatedBooking = bookingRepository.save(booking);
		log.info("Подтверждён запрос с id = {} владельца с id = {}", bookingId, userId);

		return BookingMapper.mapToBookingDto(updatedBooking);
	}

	@Override
	public BookingDto findBookingById(Long bookingId, Long userId) {
		log.info("Получение бронирования c id = {} от пользователя с id = {}", bookingId, userId);
		User user = findUserById(userId);
		Booking booking = findBookingById(bookingId);

		if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getUser().getId().equals(userId)) {
			throw new NotOwnerException("Доступ к бронированию имеют только его автор или владелец вещи");
		}
		log.info("Найдено бронирование {} пользователя с id = {}", booking, userId);

		return BookingMapper.mapToBookingDto(booking);
	}

	@Override
	public List<BookingDto> findAllByBooker(Long userId, String state) {
		log.info("Получение списка бронирований со статусом {} пользователя с id = {}", state, userId);
		State currentState = State.valueOf(state);
		findUserById(userId);

		List<Booking> bookings = switch (currentState) {
			case ALL -> bookingRepository.findAllByBookerId(userId);
			case CURRENT -> bookingRepository.findAllCurrentByBooker(userId);
			case PAST -> bookingRepository.findPastByBooker(userId);
			case FUTURE -> bookingRepository.findAllFutureByBooker(userId);
			case WAITING -> bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING);
			case REJECTED -> bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED);
		};
		log.info("Найдено {} бронирований пользователя с id = {}", bookings.size(), userId);

		return bookings.stream()
				.map(BookingMapper::mapToBookingDto)
				.sorted(Comparator.comparing(BookingDto::getStart))
				.toList();
	}

	@Override
	public List<BookingDto> findAllByOwner(Long userId, String state) {
		log.info("Получение списка бронирований со статусом {} владельца вещи с id = {}", state, userId);
		State currentState = State.valueOf(state);
		findUserById(userId);

		List<Booking> bookings = switch (currentState) {
			case ALL -> bookingRepository.findAllByOwner(userId);
			case CURRENT -> bookingRepository.findAllCurrentByOwner(userId);
			case PAST -> bookingRepository.findAllPastBookingByOwnerId(userId);
			case FUTURE -> bookingRepository.findAllFutureBookingByOwnerId(userId);
			case WAITING -> bookingRepository.findAllByOwnerIdAndStatus(userId, Status.WAITING);
			case REJECTED -> bookingRepository.findAllByOwnerIdAndStatus(userId, Status.REJECTED);
		};
		log.info("Найдено {} бронирований владельца вещи с id = {}", bookings.size(), userId);

		return bookings.stream()
				.map(BookingMapper::mapToBookingDto)
				.sorted(Comparator.comparing(BookingDto::getStart))
				.toList();
	}

	@Override
	@Transactional
	public BookingDto update(Long userId, UpdateBookingRequest request) {
		log.debug("Обновление бронирования от пользователя с id = {}", userId);

		if (request.getId() == null) {
			throw new ValidationException("Должен быть указан id бронирования");
		}

		User owner = findUserById(userId);
		Booking findBooking = findBookingById(request.getId());

		if (!findBooking.getBooker().getId().equals(userId) && !owner.getId().equals(userId)) {
			throw new NotBookedException("Только владелец вещи или арендатор могут подтверждать бронирование");
		}

		Booking updatedBooking = BookingMapper.updateBooking(findBooking, request);
		updatedBooking = bookingRepository.save(updatedBooking);

		log.debug("Обновлено бронирование от пользователя с id = {}", userId);

		return BookingMapper.mapToBookingDto(updatedBooking);
	}

	@Override
	@Transactional
	public void delete(Long bookingId) {
		log.debug("Удаление бронирования с id = {}", bookingId);
		Booking booking = findBookingById(bookingId);
		log.debug("Удалено бронирование с id = {}", bookingId);
		bookingRepository.delete(booking);
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
	}

	private Booking findBookingById(Long bookingId) {
		return bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
	}
}