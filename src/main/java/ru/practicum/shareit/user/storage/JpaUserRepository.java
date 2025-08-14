package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {
    private final CrudUserJpaRepository jpa;

    @Override
    public User add(User user) {
        return jpa.save(user);
    }

    @Override
    public User update(User user) {
        return jpa.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<User> findAll() {
        return jpa.findAll();
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email, Long excludeId) {
        if (email == null) return false;
        return (excludeId == null)
                ? jpa.existsByEmailIgnoreCase(email)
                : jpa.existsByEmailIgnoreCaseAndIdNot(email, excludeId);
    }
}
