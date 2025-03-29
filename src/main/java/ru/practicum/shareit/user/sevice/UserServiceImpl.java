package ru.practicum.shareit.user.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	public UserDto create(NewUserRequest request) {
		if (userRepository.isEmailTaken(request.getEmail())) {
			throw new DuplicateEmailException("Такой email уже используется");
		}

		User user = UserMapper.mapToUser(request);
		User createdUser = userRepository.create(user);

		return UserMapper.mapToUserDto(createdUser);
	}

	@Override
	public UserDto update(Long userId, UpdateUserRequest request) {
		User existingUser = userRepository.findById(userId);
		String newEmail = request.getEmail();

		if (newEmail != null && !newEmail.equals(existingUser.getEmail())) {
			if (userRepository.isEmailTaken(newEmail)) {
				throw new DuplicateEmailException("Такой email уже используется");
			}
		}

		User updatedUser = UserMapper.updateUserFields(existingUser, request);
		User savedUser = userRepository.update(updatedUser);

		return UserMapper.mapToUserDto(savedUser);
	}

	@Override
	public UserDto findById(Long userId) {
		User user = userRepository.findById(userId);

		return UserMapper.mapToUserDto(user);
	}

	@Override
	public boolean delete(Long userId) {
		return userRepository.deleteById(userId);
	}

	@Override
	public Collection<UserDto> findAll() {
		return userRepository.findAll().stream()
				.map(UserMapper::mapToUserDto)
				.toList();
	}
}