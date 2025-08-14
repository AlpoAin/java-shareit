package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking b) {
        return BookingDto.builder()
                .id(b.getId())
                .start(b.getStart())
                .end(b.getEnd())
                .status(b.getStatus())
                .itemId(b.getItem().getId())
                .itemName(b.getItem().getName())
                .bookerId(b.getBooker().getId())
                .build();
    }
}
