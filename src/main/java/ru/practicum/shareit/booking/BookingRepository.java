package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking b where b.item.ownerId = ?1 order by b.start desc")
    List<Booking> findByOwnerIdOrderByStartDesc(Long ownerId);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(Long itemId, Long bookerId,
                                                             BookingStatus status, LocalDateTime time);

    Optional<Booking> findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(
            Long itemId, LocalDateTime time, BookingStatus status);

    Optional<Booking> findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId, LocalDateTime time, BookingStatus status);
}
