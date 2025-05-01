package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class BookingMapper {

	public static Booking mapToBooking(CreateBookingRequest request, User booker, Item item) {
		Booking booking = new Booking();
		booking.setStatus(Status.WAITING);
		booking.setBooker(booker);
		booking.setItem(item);
		booking.setStart(request.getStart());
		booking.setEnd(request.getEnd());

		return booking;
	}

	public static BookingResponse mapToBookingDto(Booking booking) {
		BookingResponse response = new BookingResponse();
		response.setId(booking.getId());
		response.setItem(ItemMapper.mapToItemDto(booking.getItem()));
		response.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
		response.setStatus(booking.getStatus());
		response.setStart(booking.getStart());
		response.setEnd(booking.getEnd());

		return response;
	}

	public static List<BookingResponse> mapToBookingList(List<Booking> bookings) {
		return bookings.stream()
				.map(BookingMapper::mapToBookingDto)
				.toList();
	}
}