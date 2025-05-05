package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemMapperTest {
	private final LocalDateTime now = LocalDateTime.now();
	private final LocalDateTime nextDay = LocalDateTime.now().plusDays(1);

	private final User user = new User(1L, "john.doe@mail.com", "John Doe");

	private final CommentDto commentDto = new CommentDto(1L, "text", 1L, "John Doe", nextDay);
	private final List<CommentDto> comments = List.of(commentDto);

	private final CreateItemRequest newItem =
			new CreateItemRequest("name", "description", Boolean.TRUE, 1L, 1L);
	private final UpdateItemRequest updItem =
			new UpdateItemRequest(1L, "name", "description", Boolean.TRUE, 1L, 1L);
	private final UpdateItemRequest updEmptyItem =
			new UpdateItemRequest(1L, "", "", null, 1L, 1L);
	private final ItemDto dto =
			new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);

	private final ItemDetailsDto advItemDto =
			new ItemDetailsDto(1L, "name", "description",
					Boolean.TRUE, now, nextDay, comments, 1L, 1L);
	private final ItemDetailsDto advItemDtoNullDates =
			new ItemDetailsDto(1L, "name", "description",
					Boolean.TRUE, null, null, comments, 1L, 1L);

	private final Item item =
			new Item(1L, "name", "description", Boolean.TRUE, user, 1L);
	private final Comment comment =
			new Comment(1L, "text", item, user, nextDay);

	private final CreateItemRequest newItemNoRequest =
			new CreateItemRequest("name", "description", Boolean.TRUE, 1L, null);
	private final Item itemNoRequest =
			new Item(1L, "name", "description", Boolean.TRUE, user, null);
	private final ItemDetailsDto advItemDtoNoRequest =
			new ItemDetailsDto(1L, "name", "description",
					Boolean.TRUE, now, nextDay, comments, 1L, null);
	private final ItemDetailsDto advItemDtoNullDatesNoRequest =
			new ItemDetailsDto(1L, "name", "description",
					Boolean.TRUE, null, null, comments, 1L, null);
	private final ItemDto dtoNoRequest =
			new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, null);

	@Test
	public void should_map_item_to_dto_correctly() {
		ItemDto itemDto = ItemMapper.mapToItemDto(item);
		assertThat(itemDto, equalTo(dto));
	}

	@Test
	public void should_map_item_to_dto_without_request_correctly() {
		ItemDto itemDto = ItemMapper.mapToItemDto(itemNoRequest);
		assertThat(itemDto, equalTo(dtoNoRequest));
	}

	@Test
	public void should_map_item_to_details_dto_correctly() {
		ItemDetailsDto advDto = ItemMapper.mapToItemDetailsDto(item, List.of(comment));
		assertThat(advDto, equalTo(advItemDtoNullDates));
	}

	@Test
	public void should_map_item_to_details_dto_without_request_correctly() {
		ItemDetailsDto advDto = ItemMapper.mapToItemDetailsDto(itemNoRequest, List.of(comment));
		assertThat(advDto, equalTo(advItemDtoNullDatesNoRequest));
	}

	@Test
	public void should_map_item_to_details_dto_with_dates_correctly() {
		ItemDetailsDto advDto =
				ItemMapper.mapToItemDetailsDto(item, List.of(comment), Optional.of(now), Optional.of(nextDay));
		assertThat(advDto, equalTo(advItemDto));
	}

	@Test
	public void should_map_item_to_details_dto_with_dates_without_request_correctly() {
		ItemDetailsDto advDto =
				ItemMapper.mapToItemDetailsDto(itemNoRequest, List.of(comment), Optional.of(now), Optional.of(nextDay));
		assertThat(advDto, equalTo(advItemDtoNoRequest));
	}

	@Test
	public void should_map_request_to_item_correctly() {
		Item i = ItemMapper.mapToItem(user, newItem);
		assertThat(i.getName(), equalTo(item.getName()));
		assertThat(i.getDescription(), equalTo(item.getDescription()));
		assertThat(i.getAvailable(), equalTo(item.getAvailable()));
		assertThat(i.getUser(), equalTo(user));
		assertThat(i.getRequestId(), equalTo(item.getRequestId()));
	}

	@Test
	public void should_map_request_to_item_without_request_correctly() {
		Item i = ItemMapper.mapToItem(user, newItemNoRequest);
		assertThat(i.getName(), equalTo(item.getName()));
		assertThat(i.getDescription(), equalTo(item.getDescription()));
		assertThat(i.getAvailable(), equalTo(item.getAvailable()));
		assertThat(i.getUser(), equalTo(user));
		assertThat(i.getRequestId(), equalTo(null));
	}

	@Test
	public void should_update_item_fields_correctly() {
		Item i = ItemMapper.updateItem(item, updItem);
		assertThat(i.getName(), equalTo(item.getName()));
		assertThat(i.getDescription(), equalTo(item.getDescription()));
		assertThat(i.getAvailable(), equalTo(item.getAvailable()));
		assertThat(i.getUser(), equalTo(user));
		assertThat(i.getRequestId(), equalTo(item.getRequestId()));
	}

	@Test
	public void should_handle_empty_fields_when_updating_item() {
		Item i = ItemMapper.updateItem(item, updEmptyItem);
		assertThat(i.getName(), equalTo(item.getName()));
		assertThat(i.getDescription(), equalTo(item.getDescription()));
		assertThat(i.getAvailable(), equalTo(item.getAvailable()));
		assertThat(i.getUser(), equalTo(user));
		assertThat(i.getRequestId(), equalTo(item.getRequestId()));
	}
}