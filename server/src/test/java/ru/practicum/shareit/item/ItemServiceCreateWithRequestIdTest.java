package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.CrudItemRequestJpaRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
// было: INSERT INTO users(...)
// стало: MERGE (upsert) — безопасно при повторных прогонках и общем контексте
@Sql(statements = "MERGE INTO users KEY(id) VALUES (1,'owner','o@example.com');")
class ItemServiceCreateWithRequestIdTest {

    @Autowired ItemService itemService;
    @Autowired CrudItemRequestJpaRepository requestRepo;

    @Test
    void create_with_existing_requestId_ok() {
        var req = requestRepo.save(ItemRequest.builder()
                .description("need grinder")
                .requestorId(1L)
                .created(LocalDateTime.now())
                .build());

        var dto = ItemDto.builder()
                .name("Grinder")
                .description("Bosch")
                .available(true)
                .requestId(req.getId())
                .build();

        var saved = itemService.create(1L, dto);
        assertThat(saved.getRequestId()).isEqualTo(req.getId());
    }

    @Test
    void create_with_missing_requestId_throwsNotFound() {
        var dto = ItemDto.builder()
                .name("Saw")
                .description("Makita")
                .available(true)
                .requestId(999L)
                .build();

        assertThatThrownBy(() -> itemService.create(1L, dto))
                .isInstanceOf(NotFoundException.class);
    }
}
