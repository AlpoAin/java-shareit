package ru.practicum.shareit.item.model;

import lombok.*;

/**
 * Модель вещи (in-memory, без JPA)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;   // владелец (id пользователя)
    private Long requestId; // необязательный id запроса
}
