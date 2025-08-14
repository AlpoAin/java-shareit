package ru.practicum.shareit.item;

import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    Item toItem(ItemDto dto, Long ownerId);

    @AfterMapping
    default void setOwner(@MappingTarget Item item, Long ownerId) {
        item.setOwnerId(ownerId);
    }
}
