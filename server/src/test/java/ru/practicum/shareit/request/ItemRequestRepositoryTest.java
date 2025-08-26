package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.CrudItemRequestJpaRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(statements = "INSERT INTO users(id, name, email) VALUES (1,'test-user','test@example.com');")
class ItemRequestRepositoryTest {

    @Autowired
    CrudItemRequestJpaRepository repo;

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
