package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
	User create(User user);

	User update(User newUser);

	User findById(Long userId);

	boolean deleteById(Long userId);

	Collection<User> findAll();

	boolean isEmailTaken(String email);
}