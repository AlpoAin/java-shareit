package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.CrudItemRequestJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(statements = {
        "INSERT INTO users(id, name, email) VALUES (1,'u1','u1@example.com');",
        "INSERT INTO users(id, name, email) VALUES (2,'u2','u2@example.com');"
})
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

    @Test
    void findOwn_sorted_desc() {
        var t1 = LocalDateTime.now().minusHours(2);
        var t2 = LocalDateTime.now().minusHours(1);

        repo.saveAll(List.of(
                ItemRequest.builder().description("r1").requestorId(1L).created(t1).build(),
                ItemRequest.builder().description("r2").requestorId(1L).created(t2).build()
        ));

        var own = repo.findByRequestorIdOrderByCreatedDesc(1L);
        assertThat(own).hasSizeGreaterThanOrEqualTo(2);
        // самый свежий первый
        assertThat(own.get(0).getCreated()).isAfterOrEqualTo(own.get(1).getCreated());
    }

    @Test
    void findOthers_paged_sorted_desc() {
        // у user=2 создаём 3 запроса
        var base = LocalDateTime.now().minusDays(1);
        repo.saveAll(List.of(
                ItemRequest.builder().description("a").requestorId(2L).created(base.plusMinutes(1)).build(),
                ItemRequest.builder().description("b").requestorId(2L).created(base.plusMinutes(2)).build(),
                ItemRequest.builder().description("c").requestorId(2L).created(base.plusMinutes(3)).build()
        ));

        // user=1 смотрит чужие, постранично
        var page0 = repo.findByRequestorIdNotOrderByCreatedDesc(1L, PageRequest.of(0, 2));
        var page1 = repo.findByRequestorIdNotOrderByCreatedDesc(1L, PageRequest.of(1, 2));

        assertThat(page0.getContent()).hasSize(2);
        assertThat(page1.getContent()).hasSizeGreaterThanOrEqualTo(1);
        // порядок: новые → старые
        assertThat(page0.getContent().get(0).getCreated())
                .isAfterOrEqualTo(page0.getContent().get(1).getCreated());
    }
}
