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
                .item(BookingDto.ItemShort.builder()
                        .id(b.getItem().getId())
                        .name(b.getItem().getName())
                        .build())
                .booker(BookingDto.UserShort.builder()
                        .id(b.getBooker().getId())
                        .build())
                .build();
    }
}
