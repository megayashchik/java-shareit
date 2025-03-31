package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {

	private final Map<Long, User> users = new HashMap<>();

	@Override
	public User create(User user) {
		log.info("Создание нового пользователя: {}", user);
		user.setId(getNextId());
		users.put(user.getId(), user);
		log.info("Создан пользователь с id={}: {}", user.getId(), user);

		return user;
	}

	@Override
	public User update(User newUser) {
		log.info("Обновление пользователя с id={}: {}", newUser.getId(), newUser);
		if (!users.containsKey(newUser.getId())) {
			log.warn("Попытка удалить несуществующего пользователя с id={}", newUser.getId());
			throw new NotFoundException("Пользователь с id=" + newUser.getId() + " не найден");
		}
		users.put(newUser.getId(), newUser);
		log.info("Пользователь {} с id={} обновлён", newUser, newUser.getId());

		return newUser;
	}

	@Override
	public User findById(Long userId) {
		log.info("Поиск пользователя по id={}", userId);
		User foundUser = Optional.ofNullable(users.get(userId))
				.orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));
		log.info("Найден пользователь с id={}: {}", userId, foundUser);

		return foundUser;
	}

	@Override
	public boolean deleteById(Long userId) {
		log.info("Удаление пользователя с id={}", userId);
		boolean deletedUser = Optional.ofNullable(users.remove(userId)).isPresent();
		log.info("Пользователь {} с id={} удалён", deletedUser, userId);

		return deletedUser;
	}

	@Override
	public Collection<User> findAll() {
		log.info("Получение всех пользователей");
		Collection<User> foundUsers = users.values();
		log.info("Найдено {} пользователей {}", foundUsers.size(), foundUsers);

		return foundUsers;
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