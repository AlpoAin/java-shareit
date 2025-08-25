package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {
    @Autowired JacksonTester<ItemRequestDto> json;

    @Test
    void serialize() throws Exception {
        var dto = ItemRequestDto.builder()
                .id(1L)
                .description("d")
                .created(LocalDateTime.of(2025,1,1,12,0))
                .items(List.of())
                .build();
        var content = json.write(dto);
        assertThat(content).hasJsonPathValue("$.id");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("d");
    }
}