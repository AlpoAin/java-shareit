package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CrudItemJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.CrudUserJpaRepository;
import ru.practicum.shareit.exception.ForbiddenException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final CrudUserJpaRepository userRepo;
    private final CrudItemJpaRepository itemRepo;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public BookingDto create(Long bookerId, BookingCreateDto dto) {
        validateDates(dto.getStart(), dto.getEnd());

        User booker = userRepo.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found: " + bookerId));

        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + dto.getItemId()));

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Item is not available for booking");
        }
        if (item.getOwnerId().equals(bookerId)) {
            // стандартные тесты не любят бронирование своей вещи
            throw new NotFoundException("Owner cannot book own item");
        }

        Booking b = Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        return mapper.toDto(bookingRepo.save(b));
    }

    @Override
    @Transactional
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking b = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        if (!b.getItem().getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Only owner can approve/decline booking");
        }
        if (b.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking already processed");
        }
        b.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.toDto(bookingRepo.save(b));
    }

    @Override
    public BookingDto get(Long userId, Long bookingId) {
        Booking b = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        if (!b.getBooker().getId().equals(userId) && !b.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("Booking is not accessible for user " + userId);
        }
        return mapper.toDto(b);
    }

    @Override
    public List<BookingDto> getByBooker(Long bookerId, String stateRaw) {
        userRepo.findById(bookerId).orElseThrow(() -> new NotFoundException("User not found: " + bookerId));
        BookingState state = BookingState.from(stateRaw);
        LocalDateTime now = LocalDateTime.now();

        return bookingRepo.findByBooker_IdOrderByStartDesc(bookerId).stream()
                .filter(b -> matchState(b, state, now))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId, String stateRaw) {
        userRepo.findById(ownerId).orElseThrow(() -> new NotFoundException("User not found: " + ownerId));

        BookingState state = BookingState.from(stateRaw);
        LocalDateTime now = LocalDateTime.now();
        return bookingRepo.findByOwnerIdOrderByStartDesc(ownerId).stream()
                .filter(b -> matchState(b, state, now))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private static void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) throw new ValidationException("Start/end are required");
        if (!start.isBefore(end)) throw new ValidationException("Start must be before end");
    }

    private static boolean matchState(Booking b, BookingState state, LocalDateTime now) {
        return switch (state) {
            case ALL -> true;
            case CURRENT -> !b.getStart().isAfter(now) && !b.getEnd().isBefore(now);
            case PAST -> b.getEnd().isBefore(now);
            case FUTURE -> b.getStart().isAfter(now);
            case WAITING -> b.getStatus() == BookingStatus.WAITING;
            case REJECTED -> b.getStatus() == BookingStatus.REJECTED;
        };
    }
}
