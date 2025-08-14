package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public synchronized User add(User user) {
        long id = seq.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public synchronized User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public synchronized void delete(Long id) {
        users.remove(id);
    }

    @Override
    public boolean existsByEmail(String email, Long excludeId) {
        if (email == null) return false;
        String cmp = email.toLowerCase();
        return users.values().stream()
                .anyMatch(u -> u.getEmail() != null
                        && u.getEmail().toLowerCase().equals(cmp)
                        && (excludeId == null || !u.getId().equals(excludeId)));
    }
}
