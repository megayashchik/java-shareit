package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemRequestMapperTest {
	private final LocalDateTime now = LocalDateTime.now();

	private final User user = new User(1L, "john.doe@mail.com", "John Doe");
	private final Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);

	private final CreateRequest createRequest = new CreateRequest("description", 1L);
	private final UpdateRequest updRequest = new UpdateRequest(1L, "description", 1L, now);
	private final ItemRequest itemRequest = new ItemRequest(1L, "description", user, now);

	private final ResponseDto responseDto = new ResponseDto(1L, "name", 1L);
	private final List<ResponseDto> items = List.of(responseDto);
	private final ItemRequestDto dtoWithComments =
			new ItemRequestDto(1L, "description", 1L, now, items);
	private final ItemRequestDto dto =
			new ItemRequestDto(1L, "description", 1L, now, Collections.emptyList());

	@Test
	public void should_map_item_request_to_dto_correctly() {
		ItemRequestDto userDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
		assertThat(userDto, equalTo(dto));
	}

	@Test
	public void should_map_item_request_to_dto_with_responses_correctly() {
		ItemRequestDto userDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, List.of(item));
		assertThat(userDto, equalTo(dtoWithComments));
	}

	@Test
	public void should_map_create_request_to_item_request_correctly() {
		ItemRequest ir = ItemRequestMapper.mapToItemRequest(createRequest, user, now);
		assertThat(ir.getDescription(), equalTo(itemRequest.getDescription()));
		assertThat(ir.getCreated(), equalTo(itemRequest.getCreated()));
		assertThat(ir.getRequestor(), equalTo(itemRequest.getRequestor()));
	}

	@Test
	public void should_update_item_request_correctly() {
		ItemRequest ir = ItemRequestMapper.updateItemFields(itemRequest, updRequest, user);
		assertThat(ir.getId(), equalTo(itemRequest.getId()));
		assertThat(ir.getDescription(), equalTo(itemRequest.getDescription()));
		assertThat(ir.getCreated(), equalTo(itemRequest.getCreated()));
		assertThat(ir.getRequestor(), equalTo(itemRequest.getRequestor()));
	}
}