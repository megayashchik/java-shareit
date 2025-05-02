package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

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
class UserServiceIntegrationTest {
	private final EntityManager em;
	private final UserService userService;

	private void createUserInDb() {
		Query userQuery = em.createNativeQuery("INSERT INTO Users (id, name, email) " +
				"VALUES (:id , :name , :email);");
		userQuery.setParameter("id", "1");
		userQuery.setParameter("name", "Ivan Ivanov");
		userQuery.setParameter("email", "ivan@email");
		userQuery.executeUpdate();
	}

	@Test
	void should_create_user() {
		CreateUserRequest newUser = new CreateUserRequest("john.doe@mail.com", "John Doe");

		userService.create(newUser);

		TypedQuery<User> query = em.createQuery("Select u from User u where u.name like :nameUser", User.class);
		User user = query.setParameter("nameUser", newUser.getName()).getSingleResult();

		MatcherAssert.assertThat(user.getId(), CoreMatchers.notNullValue());
		MatcherAssert.assertThat(user.getName(), Matchers.equalTo(newUser.getName()));
		MatcherAssert.assertThat(user.getEmail(), Matchers.equalTo(newUser.getEmail()));
	}

	@Test
	void should_update_user() {
		createUserInDb();

		UpdateUserRequest updUser = new UpdateUserRequest(1L, "john.doe1@mail.com", "John Doe");
		UserDto findUser = userService.update(1L, updUser);

		MatcherAssert.assertThat(findUser.getId(), CoreMatchers.notNullValue());
		MatcherAssert.assertThat(findUser.getName(), Matchers.equalTo(updUser.getName()));
		MatcherAssert.assertThat(findUser.getEmail(), Matchers.equalTo(updUser.getEmail()));
	}

	@Test
	void should_delete_user() {
		createUserInDb();

		userService.delete(1L);

		TypedQuery<User> selectQuery =
				em.createQuery("Select u from User u where u.name like :nameUser", User.class);
		List<User> users = selectQuery.setParameter("nameUser", "Ivan Ivanov").getResultList();

		MatcherAssert.assertThat(users, CoreMatchers.equalTo(new ArrayList<>()));
	}

	private CreateUserRequest makeNewUser(String email, String name) {
		CreateUserRequest dto = new CreateUserRequest();
		dto.setName(name);
		dto.setEmail(email);

		return dto;
	}

	@Test
	void should_find_user_by_id() {
		createUserInDb();

		UserDto loadUsers = userService.findById(1L);

		MatcherAssert.assertThat(loadUsers.getId(), CoreMatchers.notNullValue());
		MatcherAssert.assertThat(loadUsers.getName(), Matchers.equalTo("Ivan Ivanov"));
		MatcherAssert.assertThat(loadUsers.getEmail(), Matchers.equalTo("ivan@email"));
	}

	@Test
	void should_find_all_users() {
		List<CreateUserRequest> newUsers = List.of(
				makeNewUser("ivan@email", "Ivan Ivanov"),
				makeNewUser("petr@email", "Pet Petrov"),
				makeNewUser("vasilii@email", "Vasilii Vasiliev"));

		for (CreateUserRequest user : newUsers) {
			userService.create(user);
		}

		Collection<UserDto> loadUsers = userService.findAll();

		assertThat(loadUsers, hasSize(newUsers.size()));
		for (CreateUserRequest user : newUsers) {
			assertThat(loadUsers, hasItem(allOf(
					hasProperty("id", notNullValue()),
					hasProperty("name", equalTo(user.getName())),
					hasProperty("email", equalTo(user.getEmail()))
			)));
		}
	}
}