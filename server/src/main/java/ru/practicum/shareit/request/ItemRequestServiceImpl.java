package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.CrudItemRequestJpaRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final CrudItemRequestJpaRepository repo;
    private final ItemRepository itemRepo;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        userService.get(userId); // проверка существования пользователя
        ItemRequest saved = repo.save(ItemRequest.builder()
                .description(dto.getDescription())
                .requestorId(userId)
                .created(LocalDateTime.now())
                .build());
        return toDto(saved, List.of());
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        userService.get(userId);
        List<ItemRequest> requests = repo.findByRequestorIdOrderByCreatedDesc(userId);
        return mapWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, int from, int size) {
        userService.get(userId);
        var page = repo.findByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(from / size, size));
        return mapWithItems(page.getContent());
    }

    @Override
    public ItemRequestDto get(Long userId, Long requestId) {
        userService.get(userId);
        ItemRequest r = repo.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found: " + requestId));
        List<Item> items = itemRepo.findByRequestId(requestId);
        return toDto(r, items);
    }

    private List<ItemRequestDto> mapWithItems(List<ItemRequest> requests) {
        return requests.stream()
                .map(r -> toDto(r, itemRepo.findByRequestId(r.getId())))
                .collect(Collectors.toList());
    }

    private ItemRequestDto toDto(ItemRequest r, List<Item> items) {
        return ItemRequestDto.builder()
                .id(r.getId())
                .description(r.getDescription())
                .created(r.getCreated())
                .items(items.stream().map(i -> ItemRequestDto.ItemShort.builder()
                        .id(i.getId())
                        .name(i.getName())
                        .ownerId(i.getOwnerId())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}