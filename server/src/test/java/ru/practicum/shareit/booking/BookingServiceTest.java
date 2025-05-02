package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
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

	@InjectMocks
	private ItemServiceImpl itemService;

	@InjectMocks
	private UserServiceImpl userService;

	@InjectMocks
	private BookingServiceImpl bookingService;

	@Test
	void should_fail_create_booking_when_item_unavailable() {
		CreateBookingRequest newBooking =
				new CreateBookingRequest(
						LocalDateTime.of(
								2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2024, 7, 2, 19, 30, 15),
						1L, 2L);

		CreateUserRequest newUser = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

		UserDto userDto = userService.create(newUser);
		User user = new User(1L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.FALSE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.FALSE, user, 1L);
		when(itemRepository.save(any())).thenReturn(item);

		ItemDto findItem = itemService.create(1L, newItem);

		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		NotBookedException thrown = assertThrows(NotBookedException.class, () -> {
			bookingService.create(1L, newBooking);
		});

		assertEquals("Вещь не доступна для бронирования", thrown.getMessage());
	}

	@Test
	void should_fail_create_booking_when_user_is_owner() {
		CreateBookingRequest newBooking =
				new CreateBookingRequest(LocalDateTime.of(
						2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2024, 7, 2, 19, 30, 15),
						1L, 2L);

		CreateUserRequest newUser = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

		UserDto userDto = userService.create(newUser);
		User user = new User(1L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);
		when(itemRepository.save(any())).thenReturn(item);

		ItemDto findItem = itemService.create(1L, newItem);

		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		NotBookedException thrown = assertThrows(NotBookedException.class, () -> {
			bookingService.create(1L, newBooking);
		});

		assertEquals("Нельзя забронировать свою вещь", thrown.getMessage());
	}

	@Test
	void should_fail_find_booking_when_user_not_owner_or_booker() {
		CreateUserRequest newUser1 = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser1.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

		userService.create(newUser1);
		User user1 = new User(1L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

		CreateUserRequest newUser2 = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser2.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(2L, "john.doe@mail.com", "John Doe"));

		userService.create(newUser2);
		User user2 = new User(2L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
		when(itemRepository.save(any())).thenReturn(item);

		ItemDto findItem = itemService.create(1L, newItem);

		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		CreateBookingRequest newBooking =
				new CreateBookingRequest(LocalDateTime.of(
						2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2024, 7, 2, 19, 30, 15),
						1L, 2L);
		Booking booking =
				new Booking(1L, LocalDateTime.of(2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2024, 7, 2, 19, 30, 15),
						item, Status.WAITING, user2);
		when(bookingRepository.save(any())).thenReturn(booking);

		BookingDto bookingItem = bookingService.create(2L, newBooking);

		when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

		NotOwnerException thrown = assertThrows(NotOwnerException.class, () -> {
			bookingService.findBookingById(1L, 999L);
		});

		assertEquals("Доступ к бронированию имеют только его автор или владелец вещи",
				thrown.getMessage());
	}

	@Test
	void should_fail_update_booking_when_id_missing() {
		UpdateBookingRequest updBooking =
				new UpdateBookingRequest(1L, LocalDateTime.of(
						2026, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2026, 7, 2, 19, 30, 15), 1L,
						Status.REJECTED, 1L);
		updBooking.setId(null);

		ValidationException thrown = assertThrows(ValidationException.class, () -> {
			bookingService.update(1L, updBooking);
		});

		assertEquals("Должен быть указан id бронирования", thrown.getMessage());
	}

	@Test
	void should_fail_update_booking_when_user_not_owner_or_booker() {
		CreateUserRequest newUser1 = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser1.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

		userService.create(newUser1);
		User user1 = new User(1L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

		CreateUserRequest newUser2 = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser2.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(2L, "john.doe@mail.com", "John Doe"));

		userService.create(newUser2);
		User user2 = new User(2L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
		when(itemRepository.save(any())).thenReturn(item);

		ItemDto findItem = itemService.create(1L, newItem);

		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		CreateBookingRequest newBooking =
				new CreateBookingRequest(LocalDateTime.of(
						2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2024, 7, 2, 19, 30, 15),
						1L, 2L);
		Booking booking =
				new Booking(1L, LocalDateTime.of(2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(2024, 7, 2, 19, 30, 15),
						item, Status.WAITING, user2);
		when(bookingRepository.save(any())).thenReturn(booking);

		BookingDto bookingItem = bookingService.create(2L, newBooking);

		when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

		UpdateBookingRequest updBooking =
				new UpdateBookingRequest(1L, LocalDateTime.of(
						2026, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2026, 7, 2, 19, 30, 15), 1L,
						Status.REJECTED, 1L);

		NotBookedException thrown = assertThrows(NotBookedException.class, () -> {
			bookingService.update(1L, updBooking);
		});

		assertEquals("Только владелец вещи или арендатор могут подтверждать бронирование",
				thrown.getMessage());
	}

	@Test
	void should_fail_approve_booking_when_user_not_owner() {
		CreateUserRequest newUser1 = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser1.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

		userService.create(newUser1);
		User user1 = new User(1L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

		CreateUserRequest newUser2 = new CreateUserRequest("john.doe@mail.com", "John Doe");

		lenient().when(userRepository.findByEmail(newUser2.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(2L, "john.doe@mail.com", "John Doe"));

		userService.create(newUser2);
		User user2 = new User(2L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
		when(itemRepository.save(any())).thenReturn(item);

		ItemDto findItem = itemService.create(1L, newItem);

		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		CreateBookingRequest newBooking =
				new CreateBookingRequest(LocalDateTime.of(
						2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2024, 7, 2, 19, 30, 15),
						1L, 2L);
		Booking booking =
				new Booking(1L, LocalDateTime.of(2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2024, 7, 2, 19, 30, 15),
						item, Status.WAITING, user2);
		when(bookingRepository.save(any())).thenReturn(booking);

		BookingDto bookingItem = bookingService.create(2L, newBooking);

		when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

		NotOwnerException thrown = assertThrows(NotOwnerException.class, () -> {
			bookingService.approveBooking(1L, 2L, Boolean.TRUE);
		});

		assertEquals("Только владелец вещи может подтверждать бронирование", thrown.getMessage());
	}

	@Test
	void should_fail_approve_booking_when_status_not_waiting() {
		CreateUserRequest newUser1 = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser1.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

		userService.create(newUser1);
		User user1 = new User(1L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

		CreateUserRequest newUser2 = new CreateUserRequest("john.doe@mail.com", "John Doe");

		when(userRepository.findByEmail(newUser2.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(new User(2L, "john.doe@mail.com", "John Doe"));

		userService.create(newUser2);
		User user2 = new User(2L, "john.doe@mail.com", "John Doe");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

		CreateItemRequest newItem =
				new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
		Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
		when(itemRepository.save(any())).thenReturn(item);

		ItemDto findItem = itemService.create(1L, newItem);

		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		CreateBookingRequest newBooking =
				new CreateBookingRequest(LocalDateTime.of(
						2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2024, 7, 2, 19, 30, 15),
						1L, 2L);
		Booking booking =
				new Booking(1L, LocalDateTime.of(2024, 7, 1, 19, 30, 15),
						LocalDateTime.of(
								2024, 7, 2, 19, 30, 15),
						item, Status.APPROVED, user2);
		when(bookingRepository.save(any())).thenReturn(booking);

		BookingDto bookingItem = bookingService.create(2L, newBooking);

		when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

		NotBookedException thrown = assertThrows(NotBookedException.class, () -> {
			bookingService.approveBooking(1L, 1L, Boolean.FALSE);
		});

		assertEquals("Бронирование подтверждено или отклонено", thrown.getMessage());
	}
}