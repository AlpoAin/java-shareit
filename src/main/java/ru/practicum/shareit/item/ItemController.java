package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static ru.practicum.shareit.common.HeaderNames.X_SHARER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                          @RequestBody ItemDto dto) {
        return service.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto dto) {
        return service.update(ownerId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(X_SHARER_USER_ID) Long userId,
                       @PathVariable Long itemId) {
        return service.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return service.getByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto dto) {
        return service.addComment(itemId, userId, dto);
    }
}
