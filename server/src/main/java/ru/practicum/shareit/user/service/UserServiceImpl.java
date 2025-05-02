package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserDto create(CreateUserRequest request) {
		log.info("Создание пользователя: {}", request);
		Optional<User> foundUser = userRepository.findByEmail(request.getEmail());
		if (foundUser.isPresent()) {
			throw new DuplicateEmailException(String.format("Этот E-mail \"%s\" уже используется", request.getEmail()));
		}

		if (request.getName() == null || request.getName().isBlank()) {
			throw new IllegalArgumentException("Имя не может быть пустым");
		}

		User user = UserMapper.mapToUser(request);
		User createdUser = userRepository.save(user);
		log.info("Создан пользователь: {}", createdUser);

		return UserMapper.mapToUserDto(createdUser);
	}

	@Override
	@Transactional
	public UserDto update(Long userId, UpdateUserRequest request) {
		if (userId == null) {
			throw new NotFoundException("id пользователя должен быть указан");
		}

		if (request == null) {
			throw new IllegalArgumentException("Запрос на обновление не может быть пустым");
		}
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

		if (request.getEmail() != null) {
			if (request.getEmail().isBlank()) {
				throw new IllegalArgumentException("Email не может быть пустым");
			}

			Optional<User> userWithSameEmail = userRepository.findByEmail(request.getEmail());
			if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(userId)) {
				throw new DuplicateEmailException("Этот email " + request.getEmail() + " уже используется");
			}
		}

		if (request.getName() != null && request.getName().isBlank()) {
			throw new IllegalArgumentException("Имя не может быть пустым");
		}

		User updatedUser = UserMapper.updateUser(existingUser, request);
		User savedUser = userRepository.save(updatedUser);

		return UserMapper.mapToUserDto(savedUser);
	}

	@Override
	@Transactional
	public void delete(Long userId) {
		log.info("Удаление пользователя по id={}", userId);
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
		userRepository.deleteById(userId);
		log.info("Пользователь с id={} удалён", userId);
	}

	@Override
	public UserDto findById(Long userId) {
		log.info("Поиск пользователя по id = {}", userId);
		User foundUser = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
		log.info("Найден пользователь с id = {}: {}", userId, foundUser);

		return UserMapper.mapToUserDto(foundUser);
	}

	@Override
	public List<UserDto> findAll() {
		log.info("Получение всех пользователей");
		List<UserDto> foundUsers = userRepository.findAll().stream()
				.map(UserMapper::mapToUserDto)
				.toList();
		log.info("Найдено {} пользователей: {}", foundUsers.size(), foundUsers);

		return foundUsers;
	}
}