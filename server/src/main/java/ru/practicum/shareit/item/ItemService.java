package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long ownerId, ItemDto dto);

    ItemDto update(Long ownerId, Long itemId, ItemDto dto);

    ItemDto get(Long itemId, Long requesterId);

    List<ItemDto> getByOwner(Long ownerId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, Long authorId, CommentDto dto);
}
