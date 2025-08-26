package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(statements = "INSERT INTO users(id, name, email) VALUES (1,'test','test@example.com');")
class ItemRequestServiceImplTest {

    @Autowired ItemRequestService service;

    @Test
    void create_and_read_flow_ok() {
        var created = service.create(1L, ItemRequestDto.builder()
                .description("need drill")
                .build());
        assertThat(created.getId()).isNotNull();

        var own = service.getOwn(1L);
        assertThat(own).extracting(ItemRequestDto::getId).contains(created.getId());

        var byId = service.get(1L, created.getId());
        assertThat(byId.getDescription()).isEqualTo("need drill");
    }
}
