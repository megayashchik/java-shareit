package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {
	private Long id;

	private String name;

	private String description;

	private Boolean available;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long ownerId;

	private BookingResponse lastBooking;

	private BookingResponse nextBooking;

	private List<CommentResponse> comments = new ArrayList<>();
}