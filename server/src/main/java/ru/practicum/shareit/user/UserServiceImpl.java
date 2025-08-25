package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.ConflictException;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final UserMapper mapper;

    @Override
    public UserDto create(UserDto dto) {
        validateEmail(dto.getEmail());
        if (repo.existsByEmail(dto.getEmail(), null)) {
            throw new ConflictException("Email already in use: " + dto.getEmail());
        }
        User user = mapper.toUser(dto);
        user.setId(null);
        return mapper.toUserDto(repo.add(user));
    }

    @Override
    public UserDto update(Long userId, UserDto dto) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) {
            validateEmail(dto.getEmail());
            if (repo.existsByEmail(dto.getEmail(), userId)) {
                throw new ConflictException("Email already in use: " + dto.getEmail());
            }
            user.setEmail(dto.getEmail());
        }
        return mapper.toUserDto(repo.update(user));
    }

    @Override
    public UserDto get(Long userId) {
        return mapper.toUserDto(repo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId)));
    }

    @Override
    public List<UserDto> getAll() {
        return repo.findAll().stream().map(mapper::toUserDto).collect(Collectors.toList());
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
