package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repo;
    private final UserService userService;
    private final ItemMapper mapper;

    private final BookingRepository bookingRepo;
    private final CommentRepository commentRepo;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto dto) {
        validateForCreate(dto);
        userService.get(ownerId);
        Item saved = repo.add(mapper.toItem(dto, ownerId));
        return enrichWithDetails(mapper.toItemDto(saved), ownerId);
    }

    @Override
    @Transactional
    public ItemDto update(Long ownerId, Long itemId, ItemDto dto) {
        Item item = repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        if (!item.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Only owner can update item " + itemId);
        }
        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) item.setAvailable(dto.getAvailable());
        Item updated = repo.update(item);
        return enrichWithDetails(mapper.toItemDto(updated), ownerId);
    }

    @Override
    public ItemDto get(Long itemId, Long requesterId) {
        ItemDto dto = mapper.toItemDto(repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId)));
        return enrichWithDetails(dto, requesterId);
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        return repo.findByOwner(ownerId).stream()
                .map(mapper::toItemDto)
                .map(dto -> enrichWithDetails(dto, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return repo.searchAvailable(text).stream()
                .map(mapper::toItemDto)
                .map(dto -> enrichWithDetails(dto, null)) // для поиска last/next не обязательны
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long authorId, CommentDto dto) {
        if (dto.getText() == null || dto.getText().isBlank()) {
            throw new ValidationException("Comment text is required");
        }
        // Разрешаем коммент только если была завершенная одобренная аренда
        boolean ok = bookingRepo.existsByItem_IdAndBooker_IdAndStatusAndEndBefore(
                itemId, authorId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!ok) {
            throw new ValidationException("User has no completed booking for this item");
        }
        Item item = repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        // userService.get вернёт DTO, но нам нужен только authorName в ответе
        var authorDto = userService.get(authorId);

        Comment c = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(ru.practicum.shareit.user.User.builder()
                        .id(authorId)
                        .name(authorDto.getName())
                        .email(authorDto.getEmail())
                        .build())
                .created(LocalDateTime.now())
                .build();
        Comment saved = commentRepo.save(c);

        return CommentDto.builder()
                .id(saved.getId())
                .text(saved.getText())
                .authorName(authorDto.getName())
                .created(saved.getCreated())
                .build();
    }

    private ItemDto enrichWithDetails(ItemDto dto, Long requesterId) {
        // Комментарии всегда
        List<CommentDto> comments = commentRepo.findByItem_IdOrderByCreatedDesc(dto.getId()).stream()
                .map(c -> CommentDto.builder()
                        .id(c.getId())
                        .text(c.getText())
                        .authorName(c.getAuthor().getName())
                        .created(c.getCreated())
                        .build())
                .collect(Collectors.toList());
        dto.setComments(comments);

        // last/next только для владельца
        if (requesterId != null && requesterId.equals(dto.getOwnerId())) {
            LocalDateTime now = LocalDateTime.now();
            Booking last = bookingRepo.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(
                    dto.getId(), now, BookingStatus.APPROVED).orElse(null);
            Booking next = bookingRepo.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(
                    dto.getId(), now, BookingStatus.APPROVED).orElse(null);
            dto.setLastBooking(toShort(last));
            dto.setNextBooking(toShort(next));
        }
        return dto;
    }

    private static BookingShortDto toShort(Booking b) {
        if (b == null) return null;
        return BookingShortDto.builder()
                .id(b.getId())
                .bookerId(b.getBooker().getId())
                .start(b.getStart())
                .end(b.getEnd())
                .build();
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
