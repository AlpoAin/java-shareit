package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;

    @Override
    public UserDto create(UserDto dto) {
        validateEmail(dto.getEmail());
        if (repo.existsByEmail(dto.getEmail(), null)) {
            throw new ValidationException("Email already in use: " + dto.getEmail());
        }
        User user = UserMapper.toUser(dto);
        user.setId(null);
        return UserMapper.toUserDto(repo.add(user));
    }

    @Override
    public UserDto update(Long userId, UserDto dto) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) {
            validateEmail(dto.getEmail());
            if (repo.existsByEmail(dto.getEmail(), userId)) {
                throw new ValidationException("Email already in use: " + dto.getEmail());
            }
            user.setEmail(dto.getEmail());
        }
        return UserMapper.toUserDto(repo.update(user));
    }

    @Override
    public UserDto get(Long userId) {
        return UserMapper.toUserDto(repo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId)));
    }

    @Override
    public List<UserDto> getAll() {
        return repo.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        repo.delete(userId);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Invalid email");
        }
    }
}
