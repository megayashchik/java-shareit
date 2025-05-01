package ru.practicum.shareit.booking;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.enums.State;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
	private static final String API_PREFIX = "/bookings";

	@Autowired
	public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
				.requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
				.build()
		);
	}

	public ResponseEntity<Object> create(Long userId, CreateBookingRequest request) {
		return post("", userId, request);
	}

	public ResponseEntity<Object> update(Long userId, UpdateBookingRequest request) {
		return put("", userId, request);
	}

	public ResponseEntity<Object> delete(Long bookingId) {
		return delete("/" + bookingId);
	}

	public ResponseEntity<Object> approve(Long bookingId, Long userId, Boolean approve) {
		Map<String, Object> parameters = Map.of("approved", approve);

		return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
	}

	public ResponseEntity<Object> findBooking(Long userId, Long bookingId) {
		return get("/" + bookingId, userId);
	}

	public ResponseEntity<Object> findBookings(@Nullable String pathPart, Long userId, State state) {
		Map<String, Object> parameters = Map.of("state", state.name());

		String path = "";
		if (pathPart != null) {
			path = path + pathPart;
		}

		return get(path + "?state={state}", userId, parameters);
	}
}