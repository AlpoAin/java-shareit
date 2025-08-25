package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                         @RequestBody java.util.Map<String, Object> body) {
        // валидация: name/available обязательны, requestId — опционален
        if (body.get("name") == null || body.get("name").toString().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (body.get("available") == null) {
            throw new IllegalArgumentException("available is required");
        }
        return client.create(ownerId, body);
    }
}