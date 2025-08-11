package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repo;
    private final UserService userService; // валидация существования владельца

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        validateForCreate(dto);
        // проверим, что владелец существует
        userService.get(ownerId);
        Item saved = repo.add(ItemMapper.toItem(dto, ownerId));
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto dto) {
        Item item = repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        if (!item.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Only owner can update item " + itemId);
        }
        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) item.setAvailable(dto.getAvailable());
        return ItemMapper.toItemDto(repo.update(item));
    }

    @Override
    public ItemDto get(Long itemId) {
        return ItemMapper.toItemDto(repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId)));
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        return repo.findByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return repo.searchAvailable(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateForCreate(ItemDto dto) {
        if (dto.getName() == null || dto.getName().isBlank())
            throw new ValidationException("Item name is required");
        if (dto.getDescription() == null || dto.getDescription().isBlank())
            throw new ValidationException("Item description is required");
        if (dto.getAvailable() == null)
            throw new ValidationException("Item availability is required");
    }
}
