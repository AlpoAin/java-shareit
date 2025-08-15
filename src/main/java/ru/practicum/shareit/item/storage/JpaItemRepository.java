package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class JpaItemRepository implements ItemRepository {
    private final CrudItemJpaRepository jpa;

    @Override
    public Item add(Item item) {
        return jpa.save(item);
    }

    @Override
    public Item update(Item item) {
        return jpa.save(item);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Item> findByOwner(Long ownerId) {
        return jpa.findByOwnerId(ownerId);
    }

    @Override
    public List<Item> searchAvailable(String text) {
        return jpa.searchAvailable(text);
    }
}
