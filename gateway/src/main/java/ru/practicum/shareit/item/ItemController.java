package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id"; // ← константа

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long ownerId,
                                         @RequestBody Map<String, Object> body) {
        if (body.get("name") == null || body.get("name").toString().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (body.get("available") == null) {
            throw new IllegalArgumentException("available is required");
        }
        return client.create(ownerId, body);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable long itemId) {
        return client.getById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody Map<String, Object> body) {
        Object text = body.get("text");
        if (text == null || text.toString().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Item comment text is required"));
        }
        return client.addComment(userId, itemId, body);
    }
}
