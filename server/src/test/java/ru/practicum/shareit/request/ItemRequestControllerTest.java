package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired MockMvc mvc;
    @MockBean ItemRequestService service;

    @Test
    void create_ok() throws Exception {
        when(service.create(eq(1L), any())).thenReturn(
                new ru.practicum.shareit.request.dto.ItemRequestDto(1L, "desc", LocalDateTime.now(), List.of())
        );
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"desc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getOwn_ok() throws Exception {
        when(service.getOwn(1L)).thenReturn(List.of());
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_ok() throws Exception {
        when(service.getAll(1L, 0, 10)).thenReturn(List.of());
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_ok() throws Exception {
        when(service.get(1L, 5L)).thenReturn(
                new ru.practicum.shareit.request.dto.ItemRequestDto(5L, "d", LocalDateTime.now(), List.of())
        );
        mvc.perform(get("/requests/5")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }
}
