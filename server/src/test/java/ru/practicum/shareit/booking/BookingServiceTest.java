package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceTest {
	@Mock
	private ItemRepository itemRepository;

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RequestRepository requestRepository;

	@InjectMocks
	private ItemServiceImpl itemService;

	@InjectMocks
	private UserServiceImpl userService;

	@InjectMocks
	private BookingServiceImpl bookingService;

	private User user1;
	private User user2;
	private User user3;
	private ItemRequest itemRequest;

	@BeforeEach
	void setUp() {
		user1 = new User(1L, "john.doe@mail.com", "John Doe");
		user2 = new User(2L, "jane.doe@mail.com", "Jane Doe");
		user3 = new User(999L, "other@mail.com", "Other");

		when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(user1).thenReturn(user2).thenReturn(user3);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
		when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
		when(userRepository.findById(999L)).thenReturn(Optional.of(user3));

		itemRequest = new ItemRequest();
		itemRequest.setId(1L);
		when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
	}

	@Test
	void should_fail_create_booking_when_item_unavailable() {
		CreateBookingRequest newBooking = new CreateBookingRequest(
				LocalDateTime.of(2024, 7, 1, 19, 30, 15),
				LocalDateTime.of(2024, 7, 2, 19, 30, 15),
				1L, 2L);

		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.FALSE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.FALSE, user1, 1L);
		when(itemRepository.save(any())).thenReturn(item);
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		NotBookedException thrown = assertThrows(NotBookedException.class, () -> {
			bookingService.create(1L, newBooking);
		});

		assertEquals("Вещь не доступна для бронирования", thrown.getMessage());
	}

	@Test
	void should_fail_create_booking_when_user_is_owner() {
		CreateBookingRequest newBooking = new CreateBookingRequest(
				LocalDateTime.of(2024, 7, 1, 19, 30, 15),
				LocalDateTime.of(2024, 7, 2, 19, 30, 15),
				1L, 2L);

		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
		when(itemRepository.save(any())).thenReturn(item);
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		NotBookedException thrown = assertThrows(NotBookedException.class, () -> {
			bookingService.create(1L, newBooking);
		});

		assertEquals("Нельзя забронировать свою вещь", thrown.getMessage());
	}

	@Test
	void should_fail_find_booking_when_user_not_owner_or_booker() {
		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
		when(itemRepository.save(any())).thenReturn(item);
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		CreateBookingRequest newBooking = new CreateBookingRequest(
				LocalDateTime.of(2024, 7, 1, 19, 30, 15),
				LocalDateTime.of(2024, 7, 2, 19, 30, 15),
				1L, 2L);
		Booking booking =
				new Booking(1L, LocalDateTime.of(2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(2024, 7, 2, 19, 30, 15),
						item, Status.WAITING, user2);
		when(bookingRepository.save(any())).thenReturn(booking);
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

		NotOwnerException thrown = assertThrows(NotOwnerException.class, () -> {
			bookingService.findBookingById(1L, 999L);
		});

		assertEquals("Доступ к бронированию имеют только его автор или владелец вещи", thrown.getMessage());
	}

	@Test
	void should_fail_update_booking_when_id_missing() {

		UpdateBookingRequest updBooking = new UpdateBookingRequest(
				1L, LocalDateTime.of(2026, 7, 1, 19, 30, 15),
				LocalDateTime.of(2026, 7, 2, 19, 30, 15), 1L,
				Status.REJECTED, 1L);
		updBooking.setId(null);

		ValidationException thrown = assertThrows(ValidationException.class, () -> {
			bookingService.update(1L, updBooking);
		});

		assertEquals("Должен быть указан id бронирования", thrown.getMessage());
	}

	@Test
	void should_fail_update_booking_when_user_not_owner_or_booker() {
		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

		CreateBookingRequest newBooking = new CreateBookingRequest(
				LocalDateTime.of(2024, 7, 1, 19, 30, 15),
				LocalDateTime.of(2024, 7, 2, 19, 30, 15),
				1L, 2L
		);
		Booking booking = new Booking(
				1L,
				LocalDateTime.of(2024, 7, 1, 19, 30, 15),
				LocalDateTime.of(2024, 7, 2, 19, 30, 15),
				item,
				Status.WAITING,
				user2
		);
		when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
		when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

		UpdateBookingRequest updateBooking = new UpdateBookingRequest();
		updateBooking.setId(1L);
		updateBooking.setStatus(Status.APPROVED);

		BookingDto result = bookingService.update(999L, updateBooking);

		assertNotNull(result);
		assertEquals(Status.APPROVED, result.getStatus());
	}

	@Test
	void should_fail_approve_booking_when_user_not_owner() {
		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
		when(itemRepository.save(any())).thenReturn(item);
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		CreateBookingRequest newBooking = new CreateBookingRequest(
				LocalDateTime.of(2024, 7, 1, 19, 30, 15),
				LocalDateTime.of(2024, 7, 2, 19, 30, 15),
				1L, 2L);
		Booking booking =
				new Booking(1L, LocalDateTime.of(2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(2024, 7, 2, 19, 30, 15),
						item, Status.WAITING, user2);
		when(bookingRepository.save(any())).thenReturn(booking);
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

		NotOwnerException thrown = assertThrows(NotOwnerException.class, () -> {
			bookingService.approveBooking(1L, 2L, Boolean.TRUE);
		});

		assertEquals("Только владелец вещи может подтверждать бронирование", thrown.getMessage());
	}

	@Test
	void should_fail_approve_booking_when_status_not_waiting() {
		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
		when(itemRepository.save(any())).thenReturn(item);
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		CreateBookingRequest newBooking = new CreateBookingRequest(
				LocalDateTime.of(2024, 7, 1, 19, 30, 15),
				LocalDateTime.of(2024, 7, 2, 19, 30, 15),
				1L, 2L);
		Booking booking =
				new Booking(1L, LocalDateTime.of(2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(2024, 7, 2, 19, 30, 15),
						item, Status.APPROVED, user2);
		when(bookingRepository.save(any())).thenReturn(booking);
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

		NotBookedException thrown = assertThrows(NotBookedException.class, () -> {
			bookingService.approveBooking(1L, 1L, Boolean.FALSE);
		});

		assertEquals("Бронирование подтверждено или отклонено", thrown.getMessage());
	}
}