package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingMapperTest {
	private final LocalDateTime now = LocalDateTime.now();
	private final LocalDateTime nextDay = LocalDateTime.now().plusDays(1);

	private final User user = new User(1L, "john.doe@mail.com", "John Doe");
	private final Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);

	private final UserDto userDto = new UserDto(1L, "john.doe@mail.com", "John Doe");
	private final ItemDto itemDto =
			new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);

	private final CreateBookingRequest newBooking = new CreateBookingRequest(now, nextDay, 1L, 1L);
	private final UpdateBookingRequest updBooking =
			new UpdateBookingRequest(1L, now, nextDay, 1L, Status.WAITING, 1L);
	private final UpdateBookingRequest updEmptyBooking =
			new UpdateBookingRequest(1L, null, null, 1L, Status.WAITING, 1L);
	private final BookingDto dto = new BookingDto(1L, now, nextDay, itemDto, Status.WAITING, userDto);
	private final Booking booking = new Booking(1L, now, nextDay, item, Status.WAITING, user);

	@Test
	public void should_map_booking_to_dto_correctly() {
		BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);
		assertThat(bookingDto, equalTo(dto));
	}

	@Test
	public void should_map_request_to_booking_correctly() {
		Booking b = BookingMapper.mapToBooking(newBooking, user, item);
		assertThat(b.getStart(), equalTo(booking.getStart()));
		assertThat(b.getEnd(), equalTo(booking.getEnd()));
		assertThat(b.getStatus(), equalTo(booking.getStatus()));
		assertThat(b.getItem(), equalTo(item));
		assertThat(b.getBooker(), equalTo(user));
	}

	@Test
	public void should_update_booking_fields_correctly() {
		Booking b = BookingMapper.updateBooking(booking, updBooking);
		assertThat(b.getId(), equalTo(booking.getId()));
		assertThat(b.getStart(), equalTo(booking.getStart()));
		assertThat(b.getEnd(), equalTo(booking.getEnd()));
		assertThat(b.getStatus(), equalTo(booking.getStatus()));
	}

	@Test
	public void should_handle_empty_fields_when_updating_booking() {
		Booking b = BookingMapper.updateBooking(booking, updEmptyBooking);
		assertThat(b.getId(), equalTo(booking.getId()));
		assertThat(b.getStart(), equalTo(booking.getStart()));
		assertThat(b.getEnd(), equalTo(booking.getEnd()));
		assertThat(b.getStatus(), equalTo(booking.getStatus()));
	}
}