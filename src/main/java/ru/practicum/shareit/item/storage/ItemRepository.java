package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item add(Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    List<Item> findByOwner(Long ownerId);

    List<Item> searchAvailable(String text);
}
