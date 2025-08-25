package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemShort> items; // ответы: id вещи, имя, ownerId

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemShort {
        private Long id;
        private String name;
        private Long ownerId;
    }
}