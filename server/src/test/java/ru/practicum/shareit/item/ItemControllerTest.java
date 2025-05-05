package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentRequest;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

	@Autowired
	ObjectMapper mapper;

	@MockBean
	ItemService itemService;

	@Autowired
	private MockMvc mvc;

	private final String urlTemplate = "/items";
	private final String headerUserId = "X-Sharer-User-Id";

	private ItemDto makeItemDto(Long id, String name, String description,
	                            Boolean available, Long ownerId, Long requestId) {
		return new ItemDto(id, name, description, available, ownerId, requestId);
	}

	private ItemDetailsDto makeItemDetailsDto(Long id, String name, String description,
	                                          Boolean available, LocalDateTime lastBooking,
	                                          LocalDateTime nextBooking,
	                                          List<CommentDto> comments, Long ownerId, Long requestId) {
		return new ItemDetailsDto(id, name, description, available, lastBooking, nextBooking,
				comments, ownerId, requestId);
	}

	private CommentDto makeCommentDto(Long id, String text, Long itemId, String authorName, LocalDateTime created) {
		return new CommentDto(id, text, itemId, authorName, created);
	}

	private CreateItemRequest makeCreateItemRequest(String name, String description,
	                                                Boolean available, Long ownerId, Long requestId) {
		CreateItemRequest request = new CreateItemRequest();
		request.setName(name);
		request.setDescription(description);
		request.setAvailable(available);
		request.setOwnerId(ownerId);
		request.setRequestId(requestId);
		return request;
	}

	private UpdateItemRequest makeUpdateItemRequest(Long id, String name, String description,
	                                                Boolean available, Long ownerId, Long requestId) {
		UpdateItemRequest request = new UpdateItemRequest();
		request.setId(id);
		request.setName(name);
		request.setDescription(description);
		request.setAvailable(available);
		request.setOwnerId(ownerId);
		request.setRequestId(requestId);
		return request;
	}

	private CreateCommentRequest makeCreateCommentRequest(String text) {
		CreateCommentRequest request = new CreateCommentRequest();
		request.setText(text);
		return request;
	}

	@Test
	void should_create_item() throws Exception {
		CreateItemRequest request =
				makeCreateItemRequest("name", "description", true, 1L, 1L);
		ItemDto responseDto =
				makeItemDto(1L, "name", "description", true, 1L, 1L);

		when(itemService.create(anyLong(), any(CreateItemRequest.class))).thenReturn(responseDto);

		mvc.perform(post(urlTemplate)
						.content(mapper.writeValueAsString(request))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.header(headerUserId, 1L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
				.andExpect(jsonPath("$.name", is(responseDto.getName())))
				.andExpect(jsonPath("$.description", is(responseDto.getDescription())))
				.andExpect(jsonPath("$.available", is(responseDto.getAvailable())))
				.andExpect(jsonPath("$.ownerId", is(responseDto.getOwnerId()), Long.class))
				.andExpect(jsonPath("$.requestId", is(responseDto.getRequestId()), Long.class));

		verify(itemService, times(1)).create(1L, request);
	}

	@Test
	void should_update_item() throws Exception {
		UpdateItemRequest request =
				makeUpdateItemRequest(1L,
						"updated name",
						"updated description",
						false,
						1L,
						1L);
		ItemDto responseDto =
				makeItemDto(1L,
						"updated name",
						"updated description",
						false,
						1L,
						1L);

		when(itemService.update(anyLong(), any(UpdateItemRequest.class), anyLong())).thenReturn(responseDto);

		mvc.perform(patch(urlTemplate + "/{id}", 1L)
						.content(mapper.writeValueAsString(request))
						.characterEncoding(StandardCharsets.UTF_8)
						.header(headerUserId, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
				.andExpect(jsonPath("$.name", is(responseDto.getName())))
				.andExpect(jsonPath("$.description", is(responseDto.getDescription())))
				.andExpect(jsonPath("$.available", is(responseDto.getAvailable())))
				.andExpect(jsonPath("$.ownerId", is(responseDto.getOwnerId()), Long.class))
				.andExpect(jsonPath("$.requestId", is(responseDto.getRequestId()), Long.class));

		verify(itemService, times(1)).update(1L, request, 1L);
	}

	@Test
	void should_delete_item() throws Exception {
		mvc.perform(delete(urlTemplate + "/{id}", 1L)
						.header(headerUserId, 1L))
				.andExpect(status().isNoContent());

		verify(itemService, times(1)).delete(1L, 1L);
	}

	@Test
	void should_find_item_by_id() throws Exception {
		CommentDto comment = makeCommentDto(1L, "text", 1L, "authorName",
				LocalDateTime.of(2025, 5, 2, 10, 0));
		ItemDetailsDto responseDto = makeItemDetailsDto(1L, "name", "description", true,
				LocalDateTime.of(2025, 5, 1, 10, 0),
				LocalDateTime.of(2025, 5, 3, 10, 0),
				List.of(comment), 1L, 1L);

		when(itemService.findItemById(anyLong(), anyLong())).thenReturn(responseDto);

		mvc.perform(get(urlTemplate + "/{id}", 1L)
						.characterEncoding(StandardCharsets.UTF_8)
						.header(headerUserId, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
				.andExpect(jsonPath("$.name", is(responseDto.getName())))
				.andExpect(jsonPath("$.description", is(responseDto.getDescription())))
				.andExpect(jsonPath("$.available", is(responseDto.getAvailable())))
				.andExpect(jsonPath("$.ownerId", is(responseDto.getOwnerId()), Long.class))
				.andExpect(jsonPath("$.requestId", is(responseDto.getRequestId()), Long.class))
				.andExpect(jsonPath("$.comments").isArray())
				.andExpect(jsonPath("$.comments[0].id", is(comment.getId()), Long.class))
				.andExpect(jsonPath("$.comments[0].text", is(comment.getText())))
				.andExpect(jsonPath("$.comments[0].authorName", is(comment.getAuthorName())));

		verify(itemService, times(1)).findItemById(1L, 1L);
	}

	@Test
	void should_find_all_items() throws Exception {
		CommentDto comment = makeCommentDto(1L, "text", 1L, "authorName",
				LocalDateTime.of(2025, 5, 2, 10, 0));
		ItemDetailsDto item1 = makeItemDetailsDto(1L, "name1", "description1", true,
				LocalDateTime.of(2025, 5, 1, 10, 0),
				LocalDateTime.of(2025, 5, 3, 10, 0),
				List.of(comment), 1L, 1L);
		ItemDetailsDto item2 = makeItemDetailsDto(2L, "name2", "description2", false,
				LocalDateTime.of(2025, 5, 2, 10, 0),
				LocalDateTime.of(2025, 5, 4, 10, 0),
				List.of(comment), 1L, 2L);
		List<ItemDetailsDto> items = List.of(item1, item2);

		when(itemService.findAll(anyLong())).thenReturn(items);

		mvc.perform(get(urlTemplate)
						.characterEncoding(StandardCharsets.UTF_8)
						.header(headerUserId, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()", is(2)))
				.andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
				.andExpect(jsonPath("$[0].name", is(item1.getName())))
				.andExpect(jsonPath("$[1].id", is(item2.getId()), Long.class))
				.andExpect(jsonPath("$[1].name", is(item2.getName())));

		verify(itemService, times(1)).findAll(1L);
	}

	@Test
	void should_find_items_by_booker() throws Exception {
		ItemDto item1 =
				makeItemDto(1L, "name1", "description1", true, 1L, 1L);
		ItemDto item2 =
				makeItemDto(2L, "name2", "description2", true, 1L, 2L);
		List<ItemDto> items = List.of(item1, item2);

		when(itemService.findItemsByBooker(anyLong(), anyString())).thenReturn(items);

		mvc.perform(get(urlTemplate + "/search")
						.param("text", "name")
						.characterEncoding(StandardCharsets.UTF_8)
						.header(headerUserId, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()", is(2)))
				.andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
				.andExpect(jsonPath("$[0].name", is(item1.getName())))
				.andExpect(jsonPath("$[1].id", is(item2.getId()), Long.class))
				.andExpect(jsonPath("$[1].name", is(item2.getName())));

		verify(itemService, times(1)).findItemsByBooker(1L, "name");
	}

	@Test
	void should_add_comment() throws Exception {
		CreateCommentRequest request = makeCreateCommentRequest("text");
		CommentDto responseDto = makeCommentDto(1L, "text", 1L, "authorName",
				LocalDateTime.of(2025, 5, 2, 10, 0));

		when(itemService.addComment(anyLong(), anyLong(), any(CreateCommentRequest.class))).thenReturn(responseDto);

		mvc.perform(post(urlTemplate + "/{id}/comment", 1L)
						.content(mapper.writeValueAsString(request))
						.characterEncoding(StandardCharsets.UTF_8)
						.header(headerUserId, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
				.andExpect(jsonPath("$.text", is(responseDto.getText())))
				.andExpect(jsonPath("$.itemId", is(responseDto.getItemId()), Long.class))
				.andExpect(jsonPath("$.authorName", is(responseDto.getAuthorName())));

		verify(itemService, times(1)).addComment(1L, 1L, request);
	}
}