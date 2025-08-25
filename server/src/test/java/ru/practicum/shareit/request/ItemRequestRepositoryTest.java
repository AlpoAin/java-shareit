package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.CrudItemRequestJpaRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired CrudItemRequestJpaRepository repo;

    @Test
    void saveAndFind() {
        ItemRequest r = repo.save(ItemRequest.builder()
                .description("abc")
                .requestorId(1L)
                .created(LocalDateTime.now())
                .build());
        assertThat(repo.findById(r.getId())).isPresent();
    }
}