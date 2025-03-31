package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	public UserDto create(NewUserRequest request) {
		log.info("Создание пользователя: {}", request);
		if (userRepository.isEmailTaken(request.getEmail())) {
			throw new DuplicateEmailException("Такой email уже используется");
		}

		User user = UserMapper.mapToUser(request);
		User createdUser = userRepository.create(user);
		log.info("Создан пользователь: {}", createdUser);

		return UserMapper.mapToUserDto(createdUser);
	}

	@Override
	public UserDto update(Long userId, UpdateUserRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("UpdateUserRequest cannot be null");
		}
		User existingUser = userRepository.findById(userId);

		if (request.getName() != null && request.getName().isBlank()) {
			throw new IllegalArgumentException("Имя не может быть пустым");
		}

		if (request.getEmail() != null) {
			if (request.getEmail().isBlank()) {
				throw new IllegalArgumentException("Email не может быть пустым");
			}

			if (!request.getEmail().equals(existingUser.getEmail()) &&
					userRepository.isEmailTaken(request.getEmail())) {
				throw new DuplicateEmailException("Такой email уже используется");
			}
		}

		User updatedUser = UserMapper.updateUserFields(existingUser, request);
		User savedUser = userRepository.update(updatedUser);

		return UserMapper.mapToUserDto(savedUser);
	}

	@Override
	public UserDto findById(Long userId) {
		log.info("Поиск пользователя по id={}", userId);
		User foundUser = userRepository.findById(userId);
		log.info("Найден пользователь с id={}: {}", userId, foundUser);

		return UserMapper.mapToUserDto(foundUser);
	}

	@Override
	public boolean delete(Long userId) {
		log.info("Удаление пользователя по id={}", userId);
		boolean deletedUser = userRepository.deleteById(userId);
		log.info("Пользователь {} с id={} удалён", deletedUser, userId);

		return deletedUser;
	}

	@Override
	public Collection<UserDto> findAll() {
		log.info("Получение всех пользователей");
		Collection<UserDto> foundUsers = userRepository.findAll().stream()
				.map(UserMapper::mapToUserDto)
				.toList();
		log.info("Найдено {} пользователей: {}", foundUsers.size(), foundUsers);

		return foundUsers;
	}
}