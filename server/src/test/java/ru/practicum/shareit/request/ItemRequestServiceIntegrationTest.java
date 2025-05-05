package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.CreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
		properties = "spring.datasource.username=shareit",
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestServiceIntegrationTest {
	private final EntityManager em;
	private final ItemRequestService itemRequestService;

	private void createUserInDb() {
		Query userQuery = em.createNativeQuery("INSERT INTO Users (id, name, email) " +
				"VALUES (:id , :name , :email);");
		userQuery.setParameter("id", "1");
		userQuery.setParameter("name", "Ivan Ivanov");
		userQuery.setParameter("email", "ivan@email");
		userQuery.executeUpdate();
	}

	private void createRequestInDb() {
		Query requestQuery = em.createNativeQuery("INSERT INTO Requests (id, description, requestor_id, created) " +
				"VALUES (:id , :description , :requestor_id , :created);");
		requestQuery.setParameter("id", "1");
		requestQuery.setParameter("description", "description");
		requestQuery.setParameter("requestor_id", "1");
		requestQuery.setParameter("created",
				LocalDateTime.of(2022, 7, 3, 19, 30, 1));
		requestQuery.executeUpdate();
	}

	@Test
	void should_create_item_request() {
		createUserInDb();

		CreateRequest createRequest = new CreateRequest("description", 1L);

		ItemRequestDto findItemRequest = itemRequestService.create(1L, createRequest);

		MatcherAssert.assertThat(findItemRequest.getId(), CoreMatchers.notNullValue());
		MatcherAssert.assertThat(findItemRequest.getDescription(), Matchers.equalTo(createRequest.getDescription()));
		MatcherAssert.assertThat(findItemRequest.getRequestorId(), CoreMatchers.notNullValue());
	}

	@Test
	void should_update_item_request() {
		createUserInDb();
		createRequestInDb();

		UpdateRequest updItemRequest = new UpdateRequest(1L, "description1", 1L,
				LocalDateTime.of(2023, 7, 3, 19, 30, 1));
		ItemRequestDto findItemRequest = itemRequestService.update(1L, updItemRequest);

		MatcherAssert.assertThat(findItemRequest.getId(), CoreMatchers.notNullValue());
		MatcherAssert.assertThat(findItemRequest.getDescription(), Matchers.equalTo(updItemRequest.getDescription()));
		MatcherAssert.assertThat(findItemRequest.getRequestorId(), CoreMatchers.notNullValue());
	}

	@Test
	void should_delete_item_request() {
		createUserInDb();
		createRequestInDb();

		itemRequestService.delete(1L);

		TypedQuery<ItemRequest> selectQuery =
				em.createQuery("Select r from ItemRequest r where r.description like :description",
						ItemRequest.class);
		List<ItemRequest> users = selectQuery.setParameter("description", "description").getResultList();

		MatcherAssert.assertThat(users, CoreMatchers.equalTo(new ArrayList<>()));
	}

	private CreateRequest makeNewItemRequest(String description, Long requestorId) {
		CreateRequest dto = new CreateRequest();
		dto.setDescription(description);
		dto.setRequestorId(requestorId);

		return dto;
	}

	@Test
	void should_find_item_request_by_id() {
		createUserInDb();
		createRequestInDb();

		ItemRequestDto loadRequest = itemRequestService.findItemRequest(1L);

		MatcherAssert.assertThat(loadRequest.getId(), CoreMatchers.notNullValue());
		MatcherAssert.assertThat(loadRequest.getDescription(), Matchers.equalTo("description"));
		MatcherAssert.assertThat(loadRequest.getRequestorId(), CoreMatchers.notNullValue());
		MatcherAssert.assertThat(loadRequest.getCreated(),
				Matchers.equalTo(LocalDateTime.of(2022, 7, 3, 19, 30, 1)));
	}

	@Test
	void should_find_all_requests_by_requestor() {
		createUserInDb();

		List<CreateRequest> itemRequests = List.of(
				makeNewItemRequest("description1", 1L),
				makeNewItemRequest("description2", 1L),
				makeNewItemRequest("description3", 1L)
		);

		for (CreateRequest itemRequest : itemRequests) {
			itemRequestService.create(1L, itemRequest);
		}

		Collection<ItemRequestDto> loadRequests = itemRequestService.findAllByRequestorId(1L);

		assertThat(loadRequests, hasSize(itemRequests.size()));
		for (CreateRequest itemRequest : itemRequests) {
			assertThat(loadRequests, hasItem(allOf(
					hasProperty("id", notNullValue()),
					hasProperty("description", equalTo(itemRequest.getDescription())),
					hasProperty("requestorId", equalTo(itemRequest.getRequestorId())),
					hasProperty("created", notNullValue()),
					hasProperty("items", CoreMatchers.equalTo(new ArrayList<>()))
			)));
		}
	}

	@Test
	void should_find_all_requests_by_other_requestors() {
		createUserInDb();

		List<CreateRequest> itemRequests = List.of(
				makeNewItemRequest("description1", 1L),
				makeNewItemRequest("description2", 1L),
				makeNewItemRequest("description3", 1L)
		);

		for (CreateRequest itemRequest : itemRequests) {
			itemRequestService.create(1L, itemRequest);
		}

		Collection<ItemRequestDto> loadRequests = itemRequestService.findAllOfAnotherRequestors(2L);

		assertThat(loadRequests, hasSize(itemRequests.size()));
		for (CreateRequest itemRequest : itemRequests) {
			assertThat(loadRequests, hasItem(allOf(
					hasProperty("id", notNullValue()),
					hasProperty("description", equalTo(itemRequest.getDescription())),
					hasProperty("requestorId", equalTo(itemRequest.getRequestorId())),
					hasProperty("created", notNullValue()),
					hasProperty("items", CoreMatchers.equalTo(new ArrayList<>()))
			)));
		}
	}
}