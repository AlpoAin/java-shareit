package ru.practicum.shareit.booking;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookingMapper {

    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    BookingDto toDto(Booking booking);

    // вложенные маппинги
    default BookingDto.ItemShort toItemShort(Item item) {
        if (item == null) return null;
        return BookingDto.ItemShort.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    default BookingDto.UserShort toUserShort(User user) {
        if (user == null) return null;
        return BookingDto.UserShort.builder()
                .id(user.getId())
                .build();
    }
}
