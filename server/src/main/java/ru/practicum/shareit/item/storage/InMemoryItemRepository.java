package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public synchronized Item add(Item item) {
        long id = seq.incrementAndGet();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public synchronized Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findByOwner(Long ownerId) {
        return items.values().stream()
                .filter(i -> Objects.equals(i.getOwnerId(), ownerId))
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchAvailable(String text) {
        String q = text.toLowerCase();
        return items.values().stream()
                .filter(i -> Boolean.TRUE.equals(i.getAvailable()))
                .filter(i -> (i.getName() != null && i.getName().toLowerCase().contains(q))
                        || (i.getDescription() != null && i.getDescription().toLowerCase().contains(q)))
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByRequestId(Long requestId) {
        return items.values().stream()
                .filter(i -> java.util.Objects.equals(i.getRequestId(), requestId))
                .sorted(java.util.Comparator.comparing(ru.practicum.shareit.item.model.Item::getId))
                .toList();
    }
}
