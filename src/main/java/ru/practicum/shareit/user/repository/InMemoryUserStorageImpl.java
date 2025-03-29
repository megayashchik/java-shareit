package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserStorageImpl implements UserRepository {

	private final Map<Long, User> users = new HashMap<>();

	@Override
	public User create(User user) {
		user.setId(getNextId());
		users.put(user.getId(), user);

		return user;
	}

	@Override
	public User update(User newUser) {
		users.put(newUser.getId(), newUser);

		return newUser;
	}

	@Override
	public User findById(Long userId) {
		return Optional.ofNullable(users.get(userId))
				.orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));
	}

	@Override
	public boolean deleteById(Long userId) {
		return Optional.ofNullable(users.remove(userId)).isPresent();
	}

	@Override
	public Collection<User> findAll() {
		return users.values();
	}

	@Override
	public boolean isEmailTaken(String email) {
		return email != null && users.values().stream()
				.anyMatch(user -> user.getEmail().equals(email));
	}

	private long getNextId() {
		long currentMaxId = users.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}
}