package ru.practicum.shareit.request;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.CreateRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;

@Service
public class ItemRequestClient extends BaseClient {
	private static final String API_PREFIX = "/requests";

	@Autowired
	public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
				.requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
				.build()
		);
	}

	public ResponseEntity<Object> create(Long userId, CreateRequest request) {
		return post("", userId, request);
	}

	public ResponseEntity<Object> update(Long userId, UpdateRequest request) {
		return put("", userId, null, request);
	}

	public ResponseEntity<Object> delete(Long requestId) {
		return delete("/" + requestId);
	}

	public ResponseEntity<Object> findItemRequest(Long requestId) {
		return get("/" + requestId);
	}

	public ResponseEntity<Object> findItemRequests(@Nullable String pathPart, Long userId) {
		String path = "";
		if (pathPart != null) {
			path = path + pathPart;
		}

		return get(path, userId);
	}
}